package com.example.rentalpropertyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextInputEditText txt_fullname, txt_email, txt_contact, txt_username, txt_password, txt_conpassword;
    MaterialAutoCompleteTextView txt_role;  // Dropdown for role selection
    MaterialButton btn_register;

//  String url_register = "http://f29-preview.awardspace.net/rentlify.kesug.com/register.php";
    String url_register = "http://10.0.2.2/rentlify/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize all form fields
        txt_fullname = findViewById(R.id.register_fullname);
        txt_email = findViewById(R.id.register_email);
        txt_contact = findViewById(R.id.register_contact);
        txt_username = findViewById(R.id.register_username);
        txt_password = findViewById(R.id.register_password);
        txt_conpassword = findViewById(R.id.register_cpassword);
        txt_role = findViewById(R.id.register_role);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoRegister();
            }
        });
    }

    private void GoRegister() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");

        // Get all input values
        String fullname = txt_fullname.getText().toString().trim();
        String email = txt_email.getText().toString().trim();
        String contact = txt_contact.getText().toString().trim();
        String username = txt_username.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        String conpassword = txt_conpassword.getText().toString().trim();
        String role = txt_role.getText().toString().trim();

        // Validate input fields
        if (fullname.isEmpty()) {
            Toast.makeText(Register.this, "Full name is required", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(Register.this, "Email is required", Toast.LENGTH_SHORT).show();
        } else if (contact.isEmpty()) {
            Toast.makeText(Register.this, "Contact number is required", Toast.LENGTH_SHORT).show();
        } else if (username.isEmpty()) {
            Toast.makeText(Register.this, "Username is required", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(Register.this, "Password is required", Toast.LENGTH_SHORT).show();
        } else if (conpassword.isEmpty()) {
            Toast.makeText(Register.this, "Confirm Password is required", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(conpassword)) {
            Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else if (role.isEmpty() || (!role.equals("tenant") && !role.equals("landlord"))) {
            Toast.makeText(Register.this, "Please select a valid role", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_register,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");

                                if (success.equals("1")) {
                                    Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this, MainActivity.class));
                                    finish();  // Close registration activity
                                } else {
                                    Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("fullname", fullname);
                    params.put("email", email);
                    params.put("contact", contact);
                    params.put("username", username);
                    params.put("password", password);
                    params.put("role", role);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    public void Register(View view) {
        startActivity(new Intent(Register.this, MainActivity.class));
    }
}