package com.example.rentalpropertyapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TenantRequestAdapter extends RecyclerView.Adapter<TenantRequestAdapter.ViewHolder> {

    private static final String TAG = "TenantRequestAdapter";
    private ArrayList<TenantRequestModel> requestList;
    private OnRequestCancelledListener cancelListener;

    public interface OnRequestCancelledListener {
        void onRequestCancelled();
    }

    public TenantRequestAdapter(ArrayList<TenantRequestModel> requestList) {
        this.requestList = requestList;
    }

    public void setOnRequestCancelledListener(OnRequestCancelledListener listener) {
        this.cancelListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tenant_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TenantRequestModel request = requestList.get(position);
        holder.titleView.setText(request.getTitle());

        // Safe formatting of status
        String status = request.getStatus();
        if (status != null && !status.isEmpty()) {
            holder.statusView.setText(status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase());
        } else {
            holder.statusView.setText("Unknown"); // Default text in case of null or empty status
        }

        // Set status color with safe check
        if (status != null) {
            switch (status.toLowerCase()) {
                case "approved":
                    holder.statusView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
                    break;
                case "pending":
                    holder.statusView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.orange));
                    break;
                case "rejected":
                case "cancelled":
                    holder.statusView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
                    break;
                default:
                    holder.statusView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gray));
                    break;
            }
        }

        // Set request type and date
        String requestType = request.getRequestType().equals("rental") ? "Rental Application" : "Visit Request";
        holder.typeView.setText(requestType);
        holder.dateView.setText("Requested on: " + request.getRequestedDate());

        // Show cancel button only for pending requests
        if (status != null && status.equalsIgnoreCase("pending")) {
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setOnClickListener(v -> showCancelConfirmation(holder.itemView.getContext(), request, position));
        } else {
            holder.cancelButton.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private void showCancelConfirmation(Context context, TenantRequestModel request, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Cancel Request")
                .setMessage("Are you sure you want to cancel this request?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    cancelRequest(context, request, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelRequest(Context context, TenantRequestModel request, int position) {
        //String url = "http://f29-preview.awardspace.net/rentlify.kesug.com/cancel_tenant_request.php";
        String url = "http://10.0.2.2/rentlify/cancel_tenant_request.php";

        // Get tenant ID from shared preferences
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String tenantId = prefs.getString("userId", null);

        if (tenantId == null) {
            Toast.makeText(context, "Tenant not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            String message = jsonResponse.getString("message");

                            if (success) {
                                // Update the item in the list
                                requestList.get(position).setStatus("cancelled");
                                notifyItemChanged(position);

                                // Notify listener if set
                                if (cancelListener != null) {
                                    cancelListener.onRequestCancelled();
                                }
                            }

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(context, "Error processing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "Network error";
                        if (error.networkResponse != null) {
                            errorMsg += ": " + error.networkResponse.statusCode;
                        }
                        Log.e(TAG, errorMsg, error);
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request_id", request.getRequestId());
                params.put("request_type", request.getRequestType());
                params.put("tenant_id", tenantId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, statusView, typeView, dateView;
        Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.text_property_title);
            statusView = itemView.findViewById(R.id.text_status);
            typeView = itemView.findViewById(R.id.text_request_type);
            dateView = itemView.findViewById(R.id.text_date);
            cancelButton = itemView.findViewById(R.id.btn_cancel_request);
        }
    }
}