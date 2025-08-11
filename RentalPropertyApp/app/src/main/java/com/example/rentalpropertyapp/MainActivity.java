package com.example.rentalpropertyapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText txt_username, txt_password;
    private TextView tv_error;
//    private static final String LOGIN_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/login.php";
    private static final String LOGIN_URL = "http://10.0.2.2/rentlify/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        txt_username = findViewById(R.id.login_username);
        txt_password = findViewById(R.id.login_password);
        tv_error = findViewById(R.id.error_main);
        MaterialButton btn_login = findViewById(R.id.login_btnlogin);

        // Auto-login if session exists
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            String role = prefs.getString("userRole", "");
            String status = prefs.getString("userStatus", "");
            String userId = prefs.getString("userId", "");
            String username = prefs.getString("username", "");

            Intent intent = role.equalsIgnoreCase("landlord")
                    ? new Intent(this, (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("In Review"))
                    ? VerificationActivity.class : LandlordActivity.class)
                    : new Intent(this, TenantActivity.class);

            intent.putExtra("userid", userId);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        }

        btn_login.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = txt_username.getText().toString().trim();
        String password = txt_password.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tv_error.setText("Please enter all fields");
            Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.progress_layout)
                .create();
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    progressDialog.dismiss();
                    handleLoginResponse(response);
                },
                error -> {
                    progressDialog.dismiss();
                    tv_error.setText("Network error: " + error.toString());
                    Toast.makeText(this, "Network error. Try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void handleLoginResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            if ("1".equals(json.getString("success"))) {
                JSONObject user = json.getJSONArray("login").getJSONObject(0);
                String userId = user.getString("userid");
                String userName = user.getString("username").trim();
                String userRole = user.getString("role").trim();
                String userStatus = user.getString("status").trim();

                SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                editor.putString("userId", userId);
                editor.putString("username", userName);
                editor.putString("userRole", userRole);
                editor.putString("userStatus", userStatus);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Intent intent = userRole.equalsIgnoreCase("landlord")
                        ? new Intent(this, (userStatus.equalsIgnoreCase("Pending") || userStatus.equalsIgnoreCase("In Review"))
                        ? VerificationActivity.class : LandlordActivity.class)
                        : new Intent(this, TenantActivity.class);

                intent.putExtra("username", userName);
                intent.putExtra("userid", userId);
                startActivity(intent);
                finish();
            } else {
                tv_error.setText(json.getString("message"));
                Toast.makeText(this, "Login failed: " + json.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            tv_error.setText("Server error");
            Toast.makeText(this, "JSON parsing error", Toast.LENGTH_SHORT).show();
        }
    }

    public void reglog(View view) {
        startActivity(new Intent(this, Register.class));
    }
}
