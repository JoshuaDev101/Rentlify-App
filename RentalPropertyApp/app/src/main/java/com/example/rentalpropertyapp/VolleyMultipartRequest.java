package com.example.rentalpropertyapp;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> listener;
    private final String boundary = "----" + System.currentTimeMillis();

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String name, byte[] data, String mimeType) {
            this.fileName = name;
            this.content = data;
            this.type = mimeType;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }

    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<>();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    protected Map<String, String> getParams() {
        return new HashMap<>();
    }

    protected Map<String, DataPart> getByteData() {
        return new HashMap<>();
    }

    @Override
    public byte[] getBody() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {

            for (Map.Entry<String, String> entry : getParams().entrySet())
                writeTextPart(dos, entry.getKey(), entry.getValue());

            for (Map.Entry<String, DataPart> entry : getByteData().entrySet())
                writeDataPart(dos, entry.getKey(), entry.getValue());

            dos.writeBytes("--" + boundary + "--\r\n");
            return bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeTextPart(DataOutputStream dos, String name, String value) throws IOException {
        dos.writeBytes("--" + boundary + "\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        dos.writeBytes(value + "\r\n");
    }

    private void writeDataPart(DataOutputStream dos, String name, DataPart data) throws IOException {
        dos.writeBytes("--" + boundary + "\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + data.getFileName() + "\"\r\n");
        if (data.getType() != null) {
            dos.writeBytes("Content-Type: " + data.getType() + "\r\n");
        }
        dos.writeBytes("\r\n");
        dos.write(data.getContent());
        dos.writeBytes("\r\n");
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }
}
