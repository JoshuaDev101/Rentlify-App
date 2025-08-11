package com.example.rentalpropertyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LandlordEditPropertyActivity extends AppCompatActivity {

    private static final String TAG = "EditPropertyActivity";
//    private static final String MANAGE_API_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/manage_property.php";
    private static final String MANAGE_API_URL = "http://10.0.2.2/rentlify/manage_property.php";

    private TextInputEditText editTitle, editLocation, editPrice, editDescription, editBedrooms, editBathrooms, editArea;
    private Spinner spinnerAvailability;
    private MaterialButton buttonSave, buttonCancel;

    private String propertyId;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landlord_edit_property);

        editTitle = findViewById(R.id.edit_property_title);
        editLocation = findViewById(R.id.edit_property_location);
        editPrice = findViewById(R.id.edit_property_price);
        editDescription = findViewById(R.id.edit_property_description);
        editBedrooms = findViewById(R.id.edit_property_bedrooms);
        editBathrooms = findViewById(R.id.edit_property_bathrooms);
        editArea = findViewById(R.id.edit_property_area);
        spinnerAvailability = findViewById(R.id.spinner_availability);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        // Set up availability options
        String[] availabilityOptions = {"Available", "Rented", "Pending"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availabilityOptions);
        spinnerAvailability.setAdapter(adapter);

        // Get property ID from intent
        propertyId = getIntent().getStringExtra("property_id");
        if (propertyId == null) {
            Toast.makeText(this, "Error: No property ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Load property details
        loadPropertyDetails();

        // Set up click listeners
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePropertyChanges();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Just close the activity
            }
        });
    }

    private void loadPropertyDetails() {
        String url = MANAGE_API_URL + "?action=get&property_id=" + propertyId;
        Log.d(TAG, "Loading property details from URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Got response: " + response.toString());
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONObject property = response.getJSONObject("property");

                                // Populate fields with existing data
                                editTitle.setText(property.getString("title"));
                                editLocation.setText(property.getString("location"));
                                editPrice.setText(String.valueOf(property.getDouble("price")));
                                editDescription.setText(property.getString("description"));

                                // Set new fields
                                editBedrooms.setText(property.getString("bedrooms"));
                                editBathrooms.setText(property.getString("bathrooms"));
                                editArea.setText(property.getString("area"));

                                // Set availability spinner
                                String availability = property.getString("availability");
                                String[] availabilityOptions = {"Available", "Rented", "Pending"};
                                for (int i = 0; i < availabilityOptions.length; i++) {
                                    if (availabilityOptions[i].equalsIgnoreCase(availability)) {
                                        spinnerAvailability.setSelection(i);
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(LandlordEditPropertyActivity.this,
                                        "Error loading property details: " + response.optString("message", "Unknown error"),
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error response: " + response.toString());
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
                            Toast.makeText(LandlordEditPropertyActivity.this, "Error parsing property data", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Network error: " + error.getMessage(), error);
                        Toast.makeText(LandlordEditPropertyActivity.this, "Network error: " +
                                        (error.getMessage() != null ? error.getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        requestQueue.add(request);
    }

    private void savePropertyChanges() {
        // Validate inputs first
        if (editTitle.getText().toString().trim().isEmpty() ||
                editLocation.getText().toString().trim().isEmpty() ||
                editPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress to user
        Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT).show();

        StringRequest request = new StringRequest(Request.Method.POST, MANAGE_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Server response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                Toast.makeText(LandlordEditPropertyActivity.this, "Property updated successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Close the activity to return to the dashboard
                            } else {
                                String errorMsg = jsonResponse.optString("message", "Unknown error");
                                Log.e(TAG, "Error updating property: " + errorMsg);
                                Toast.makeText(LandlordEditPropertyActivity.this,
                                        "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
                            Toast.makeText(LandlordEditPropertyActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Network error: " + error.getMessage(), error);
                        Toast.makeText(LandlordEditPropertyActivity.this,
                                "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "update");
                params.put("property_id", propertyId);
                params.put("title", editTitle.getText().toString().trim());
                params.put("location", editLocation.getText().toString().trim());
                params.put("price", editPrice.getText().toString().trim());
                params.put("description", editDescription.getText().toString().trim());
                params.put("bedrooms", editBedrooms.getText().toString().trim());
                params.put("bathrooms", editBathrooms.getText().toString().trim());
                params.put("area", editArea.getText().toString().trim());
                params.put("availability", spinnerAvailability.getSelectedItem().toString());

                // Log parameters for debugging
                Log.d(TAG, "Sending params: " + params.toString());

                return params;
            }
        };

        requestQueue.add(request);
    }
}