package com.example.rentalpropertyapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Landlord_Fragment_Dashboard extends Fragment {

    private static final String TAG = "FragmentProperties";
    private static final String API_URL = "http://10.0.2.2/rentlify/get_properties.php";
    private static final String MANAGE_API_URL = "http://10.0.2.2/rentlify/manage_property.php";
    private static final String VISITS_API_URL = "http://10.0.2.2/rentlify/get_visit_count.php";
    private static final String RENTAL_API_URL = "http://10.0.2.2/rentlify/get_rental_applications_count.php";
//
//    private static final String RENTAL_API_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_rental_applications_count.php";
//    private static final String VISITS_API_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_visit_count.php";
//    private static final String API_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_properties.php";
//private static final String MANAGE_API_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/manage_property.php";



    TextView textTotalProperties, textTotalIncome, textVisitRequests, textRentalApplications;
    ListView listProperties;
    MaterialButton buttonAddProperty;
    LinearLayout layoutVisitApplications, layoutRentalApplications;

    private boolean isLoading = false;
    private RequestQueue requestQueue;
    private int totalVisitRequests = 0;
    private int totalRentalApplications = 0;
    private ArrayList<HashMap<String, String>> propertyList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.landlord_fragment_dashboard, container, false);

            // Initialize views
            textTotalProperties = view.findViewById(R.id.text_total_properties);
            textTotalIncome = view.findViewById(R.id.text_total_income);
            textVisitRequests = view.findViewById(R.id.text_visit_requests);
            textRentalApplications = view.findViewById(R.id.text_rental_applications);

            // Fixed ID reference to match XML layout
            listProperties = view.findViewById(R.id.list_properties);
            buttonAddProperty = view.findViewById(R.id.button_add_property);

            // Get references to the clickable layouts
            layoutVisitApplications = view.findViewById(R.id.layout_visit_applications);
            layoutRentalApplications = view.findViewById(R.id.layout_rental_applications);

            // Set up button click listener to navigate to Landlord_Fragment_Post.xaml
            buttonAddProperty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new instance of Landlord_Fragment_Post
                    Landlord_Fragment_Post postFragment = new Landlord_Fragment_Post();

                    // Get the FragmentManager and start a transaction
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, postFragment) // Use your actual container ID here
                            .addToBackStack(null) // Add to back stack so user can navi gate back
                            .commit();
                }
            });

            // Set up click listener for visit requests section
            layoutVisitApplications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LandlordVisitsActivity.class);
                    startActivity(intent);
                }
            });

            // Set up click listener for rental applications section
            layoutRentalApplications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LandlordRentalActivity.class);
                    startActivity(intent);
                }
            });

            // Set up item click listener for property list
            listProperties.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the property data
                    HashMap<String, String> property = propertyList.get(position);

                    // Show property options dialog
                    showPropertyOptionsDialog(property);
                }
            });

            // Initialize Volley request queue
            requestQueue = Volley.newRequestQueue(getContext());

            // Load properties based on the logged-in user's landlord_id
            fetchPropertiesFromAPI();

            // Fetch visit request count
            fetchVisitRequestsCount();

            // Fetch rental applications count
            fetchRentalApplicationsCount();

            Log.d(TAG, "FragmentProperties view inflated.");

        } catch (Exception e) {
            Log.e(TAG, "Error loading Landlord_Fragment_Dashboard layout", e);
            Toast.makeText(getActivity(), "Error loading properties view", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void showPropertyOptionsDialog(final HashMap<String, String> property) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.landlord_property_options);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Get the property ID
        final String propertyId = property.get("id");

        // Set up click listeners for each option
        LinearLayout optionViewDetails = dialog.findViewById(R.id.option_view_details);
        LinearLayout optionEditProperty = dialog.findViewById(R.id.option_edit_property);
        LinearLayout optionDeleteProperty = dialog.findViewById(R.id.option_delete_property);
        LinearLayout optionClose = dialog.findViewById(R.id.option_close);

        // View Details option
        optionViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Navigate to property details activity
                    Intent intent = new Intent(getActivity(), LandlordPropertyDetailsActivity.class);
                intent.putExtra("property_id", propertyId);
                startActivity(intent);
            }
        });

        // Edit Property option
        optionEditProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Navigate to edit property activity
                Intent intent = new Intent(getActivity(), LandlordEditPropertyActivity.class);
                intent.putExtra("property_id", propertyId);
                startActivity(intent);
            }
        });

        // Delete Property option
        optionDeleteProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Show confirmation dialog before deleting
                showDeleteConfirmationDialog(propertyId);
            }
        });

        // Close option
        optionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDeleteConfirmationDialog(final String propertyId) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete Property");
        builder.setMessage("Are you sure you want to delete this property? This action cannot be undone.");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteProperty(propertyId);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void deleteProperty(String propertyId) {
        // Add debug log
        Log.d(TAG, "Attempting to delete property with ID: " + propertyId);

        StringRequest request = new StringRequest(Request.Method.POST, MANAGE_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Add debug log for the raw response
                        Log.d(TAG, "Raw response from manage_property.php: " + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                Toast.makeText(getContext(), "Property deleted successfully", Toast.LENGTH_SHORT).show();
                                // Refresh the property list
                                fetchPropertiesFromAPI();
                            } else {
                                Toast.makeText(getContext(), "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "API error: " + jsonResponse.getString("message"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "JSON parsing error. Raw response: " + response, e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Network error";

                        // Get more details about the error
                        if (error.networkResponse != null) {
                            errorMessage += " (Status code: " + error.networkResponse.statusCode + ")";

                            // Try to get response body
                            if (error.networkResponse.data != null) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "UTF-8");
                                    Log.e(TAG, "Error response body: " + responseBody);
                                } catch (UnsupportedEncodingException e) {
                                    Log.e(TAG, "Error parsing error response", e);
                                }
                            }
                        } else if (error.getCause() != null) {
                            errorMessage += ": " + error.getCause().getMessage();
                        } else if (error.getMessage() != null) {
                            errorMessage += ": " + error.getMessage();
                        }

                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Volley error: " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "delete");
                params.put("property_id", propertyId);

                // Debug log for parameters
                Log.d(TAG, "Sending parameters: action=delete, property_id=" + propertyId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // Set a timeout for the request
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000, // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the queue
        requestQueue.add(request);
        Log.d(TAG, "Delete request added to queue");
    }

    private void fetchVisitRequestsCount() {
        // FIXED: Use "UserPrefs" instead of "UserSession"
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            return;
        }

        int landlordId = Integer.parseInt(userId);
        String url = VISITS_API_URL + "?landlord_id=" + landlordId;
        Log.d(TAG, "Fetching visit counts for landlord ID: " + landlordId);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("status") && response.getString("status").equals("success")) {
                                totalVisitRequests = response.getInt("total_visits");
                                updateVisitCounter();
                            } else {
                                Log.e(TAG, "Invalid visit count response format");
                                // Use mock data as fallback
                                totalVisitRequests = 3; // Mock data
                                updateVisitCounter();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing visit counts: " + e.getMessage());
                            // Set mock data if there's an error
                            totalVisitRequests = 3; // Mock data
                            updateVisitCounter();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching visit counts: " + error.getMessage());
                        // Set mock data if there's an error
                        totalVisitRequests = 3; // Mock data
                        updateVisitCounter();
                    }
                });

        requestQueue.add(jsonRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fetchRentalApplicationsCount() {
        // FIXED: Use "UserPrefs" instead of "UserSession"
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            return;
        }

        int landlordId = Integer.parseInt(userId);
        String url = RENTAL_API_URL + "?landlord_id=" + landlordId;
        Log.d(TAG, "Fetching rental application counts for landlord ID: " + landlordId);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("status") && response.getString("status").equals("success")) {
                                totalRentalApplications = response.getInt("total_applications");
                                updateRentalCounter();
                            } else {
                                Log.e(TAG, "Invalid rental applications count response format");
                                totalRentalApplications = 2; // Mock data
                                updateRentalCounter();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing rental applications counts: " + e.getMessage());
                            totalRentalApplications = 2; // Mock data
                            updateRentalCounter();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching rental applications counts: " + error.getMessage());
                        totalRentalApplications = 0; // Mock data
                        updateRentalCounter();
                    }
                });

        requestQueue.add(jsonRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateVisitCounter() {
        textVisitRequests.setText(String.valueOf(totalVisitRequests));

        // If needed, you can style this differently when there are pending requests
        if (totalVisitRequests > 0) {
            textVisitRequests.setTextColor(getResources().getColor(R.color.black, null));
        } else {
            textVisitRequests.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateRentalCounter() {
        textRentalApplications.setText(String.valueOf(totalRentalApplications));

        // If needed, you can style this differently when there are pending applications
        if (totalRentalApplications > 0) {
            textRentalApplications.setTextColor(getResources().getColor(R.color.black, null));
        } else {
            textRentalApplications.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        }
    }

    private void fetchPropertiesFromAPI() {
        if (isLoading) return;
        isLoading = true;

        // Show loading state
        textTotalProperties.setText("Loading...");
        textTotalIncome.setText("");

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        Log.d(TAG, "Retrieved userId from SharedPreferences: " + userId);

        if (userId == null) {
            Toast.makeText(getActivity(), "No landlord ID found. Please login first.", Toast.LENGTH_SHORT).show();
            isLoading = false;
            return;
        }

        // Convert userId to an integer
        int landlordId = Integer.parseInt(userId);

        String url = API_URL + "?landlord_id=" + landlordId;
        Log.d(TAG, "Requesting properties from URL: " + url);

        // Use StringRequest first to see the raw response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Raw API Response: " + response);

                        try {
                            // Now try to parse it as JSON
                            JSONObject jsonResponse = new JSONObject(response);

                            if (jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) {
                                int totalProperties = jsonResponse.getInt("total_properties");
                                double totalIncome = jsonResponse.getDouble("total_income");
                                JSONArray propertiesArray = jsonResponse.getJSONArray("properties");

                                // Update dashboard stats
                                updateDashboard(totalProperties, totalIncome);

                                // Update property list
                                updatePropertyList(propertiesArray);

                                Toast.makeText(getActivity(), "Properties loaded successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonResponse.has("message") ?
                                        jsonResponse.getString("message") : "Unknown error";
                                handleApiError("API returned error: " + message);
                            }
                        } catch (JSONException e) {
                            // Log the first 200 characters of the response to see what's wrong
                            String preview = response.length() > 200 ?
                                    response.substring(0, 200) + "..." : response;
                            Log.e(TAG, "JSON parse error. Response preview: " + preview, e);
                            handleApiError("Error parsing data: " + e.getMessage());
                            loadMockData(); // Fall back to mock data
                        } finally {
                            isLoading = false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Network error";

                        // Get more details about the error
                        if (error.networkResponse != null) {
                            errorMessage += " (Status code: " + error.networkResponse.statusCode + ")";

                            // Try to get response body
                            if (error.networkResponse.data != null) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "UTF-8");
                                    Log.e(TAG, "Error response body: " + responseBody);
                                } catch (UnsupportedEncodingException e) {
                                    Log.e(TAG, "Error parsing error response", e);
                                }
                            }
                        } else if (error.getCause() != null) {
                            errorMessage += ": " + error.getCause().getMessage();
                        } else if (error.getMessage() != null) {
                            errorMessage += ": " + error.getMessage();
                        }

                        handleApiError(errorMessage);
                        isLoading = false;
                        loadMockData(); // Fall back to mock data
                    }
                });

        // Set a timeout for the request
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000, // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add request to queue
        requestQueue.add(stringRequest);
    }

    private void updateDashboard(int totalProperties, double totalIncome) {
        textTotalProperties.setText(String.valueOf(totalProperties));

        // Format currency with thousands separators
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        String formattedIncome = currencyFormat.format(totalIncome);

        // Replace the currency symbol with ₱
        if (formattedIncome.startsWith("PHP")) {
            formattedIncome = "₱" + formattedIncome.substring(3);
        } else if (formattedIncome.startsWith("P")) {
            formattedIncome = "₱" + formattedIncome.substring(1);
        }

        textTotalIncome.setText(formattedIncome);
    }

    private void updatePropertyList(JSONArray propertiesArray) throws JSONException {
        propertyList = new ArrayList<>();


        for (int i = 0; i < propertiesArray.length(); i++) {
            JSONObject property = propertiesArray.getJSONObject(i);
            HashMap<String, String> propertyMap = new HashMap<>();

            // Get the image URL
            String imageUrl = property.getString("image_path");

            // Format rent value with comma separators
            double rentValue = property.getDouble("rent");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
            String formattedRent = currencyFormat.format(rentValue);

            // Replace the currency symbol with ₱
            if (formattedRent.startsWith("PHP")) {
                formattedRent = "₱" + formattedRent.substring(3);
            } else if (formattedRent.startsWith("P")) {
                formattedRent = "₱" + formattedRent.substring(1);
            }

            // Add property ID for edit/delete operations
            propertyMap.put("id", property.getString("id"));
            propertyMap.put("property_name", property.getString("property_name"));
            propertyMap.put("location", property.getString("location"));
            propertyMap.put("rent", formattedRent);
            propertyMap.put("image_url", imageUrl);

            // Add new fields
            propertyMap.put("bedrooms", property.getString("bedroom"));
            propertyMap.put("bathrooms", property.getString("bathroom"));
            propertyMap.put("area", property.getString("area") + " sqft");
            propertyMap.put("availability", property.getString("availability"));

            propertyList.add(propertyMap);
        }

        // Create custom adapter with custom layout
        SimpleAdapter adapter = new SimpleAdapter(
                getContext(),
                propertyList,
                R.layout.landlord_property_item,
                new String[]{"property_name", "location", "rent", "image_url", "bedrooms", "bathrooms", "area", "availability"},
                new int[]{R.id.text_property_name, R.id.text_property_location, R.id.text_property_rent, R.id.image_property,
                        R.id.text_property_bedrooms, R.id.text_property_bathrooms, R.id.text_property_area, R.id.text_availability_tag}
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Find the "More Options" button in the list item layout
                MaterialButton buttonMoreOptions = view.findViewById(R.id.button_more_options);

                // Set click listener for the button
                buttonMoreOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the property data for this position
                        HashMap<String, String> property = propertyList.get(position);

                        // Show property options dialog
                        showPropertyOptionsDialog(property);
                    }
                });

                return view;
            }

            @Override
            public void setViewImage(ImageView v, String value) {
                // Load image using Glide
                Glide.with(getContext())
                        .load(value)
                        .placeholder(R.drawable.ic_placeholder_image)
                        .error(R.drawable.ic_placeholder_image)
                        .into(v);
            }

            @Override
            public void setViewText(TextView view, String text) {
                // Set text for all views
                super.setViewText(view, text);

                // Style the availability tag based on availability status
                if (view.getId() == R.id.text_availability_tag) {
                    switch (text.toLowerCase()) {
                        case "available":
                            view.setBackgroundResource(R.drawable.available_tag_gradient);
                            view.setTextColor(getResources().getColor(android.R.color.white));
                            break;
                        case "rented":
                            view.setBackgroundResource(R.drawable.rented_tag_gradient);
                            view.setTextColor(getResources().getColor(android.R.color.white));
                            break;
                        case "pending":
                            view.setBackgroundResource(R.drawable.pending_tag_gradient);
                            view.setTextColor(getResources().getColor(android.R.color.black));
                            break;
                        default:
                            view.setBackgroundResource(R.drawable.premium_tag_gradient);
                            view.setTextColor(getResources().getColor(android.R.color.white));
                            break;
                    }
                }
            }
        };

        listProperties.setAdapter(adapter);
    }

    private void handleApiError(String errorMessage) {
        Log.e(TAG, errorMessage);
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();

        // Reset dashboard text
        textTotalProperties.setText("0");
        textTotalIncome.setText("₱0.00");
    }

    // Fallback method to load mock data if API fails
    private void loadMockData() {
        // Sample mock data with properly formatted prices and new fields
        propertyList = new ArrayList<>();

        HashMap<String, String> property1 = new HashMap<>();
        property1.put("id", "1");
        property1.put("property_name", "Luxury Apartment");
        property1.put("location", "Makati City");
        property1.put("rent", "₱15,000.00");
        property1.put("image_url", "");
        property1.put("bedrooms", "2");
        property1.put("bathrooms", "1");
        property1.put("area", "850 sqft");
        property1.put("availability", "Available");

        HashMap<String, String> property2 = new HashMap<>();
        property2.put("id", "2");
        property2.put("property_name", "Studio Condo");
        property2.put("location", "BGC, Taguig");
        property2.put("rent", "₱22,000.00");
        property2.put("image_url", "");
        property2.put("bedrooms", "1");
        property2.put("bathrooms", "1");
        property2.put("area", "550 sqft");
        property2.put("availability", "Rented");

        HashMap<String, String> property3 = new HashMap<>();
        property3.put("id", "3");
        property3.put("property_name", "Executive Suite");
        property3.put("location", "Ortigas Center");
        property3.put("rent", "₱35,000.00");
        property3.put("image_url", "");
        property3.put("bedrooms", "3");
        property3.put("bathrooms", "2");
        property3.put("area", "1200 sqft");
        property3.put("availability", "Pending");

        propertyList.add(property1);
        propertyList.add(property2);
        propertyList.add(property3);

        // Update dashboard with mock data
        int total = propertyList.size();
        double totalIncome = 15000 + 22000 + 35000;

        // Format the total income
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        String formattedIncome = currencyFormat.format(totalIncome);
        if (formattedIncome.startsWith("PHP")) {
            formattedIncome = "₱" + formattedIncome.substring(3);
        } else if (formattedIncome.startsWith("P")) {
            formattedIncome = "₱" + formattedIncome.substring(1);
        }

        textTotalProperties.setText(String.valueOf(total));
        textTotalIncome.setText(formattedIncome);

        // Create custom adapter with the mock data
        SimpleAdapter adapter = new SimpleAdapter(
                getContext(),
                propertyList,
                R.layout.landlord_property_item,
                new String[]{"property_name", "location", "rent", "image_url", "bedrooms", "bathrooms", "area", "availability"},
                new int[]{R.id.text_property_name, R.id.text_property_location, R.id.text_property_rent, R.id.image_property,
                        R.id.text_property_bedrooms, R.id.text_property_bathrooms, R.id.text_property_area, R.id.text_availability_tag}
        ) {
            @Override
            public void setViewImage(ImageView v, String value) {
                // Load placeholder image
                v.setImageResource(R.drawable.ic_placeholder_image);
            }

            @Override
            public void setViewText(TextView view, String text) {
                // Set text for all views
                super.setViewText(view, text);

                // Style the availability tag based on availability status
                if (view.getId() == R.id.text_availability_tag) {
                    switch (text.toLowerCase()) {
                        case "available":
                            view.setBackgroundResource(R.drawable.available_tag_gradient);
                            view.setTextColor(getResources().getColor(android.R.color.white));
                            break;
                        case "rented":
                            view.setBackgroundResource(R.drawable.rented_tag_gradient);
                            view.setTextColor(getResources().getColor(android.R.color.white));
                            break;
                        case "pending":
                            view.setBackgroundResource(R.drawable.pending_tag_gradient);
                            view.setTextColor(getResources().getColor(android.R.color.black));
                            break;
                        default:
                            view.setBackgroundResource(R.drawable.premium_tag_gradient);
                            view.setTextColor(getResources().getColor(android.R.color.white));
                            break;
                    }
                }
            }
        };

        listProperties.setAdapter(adapter);

        Toast.makeText(getActivity(), "Loaded mock data (API unavailable)", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        // Refresh the visit count and rental applications count when returning to this fragment
        fetchVisitRequestsCount();
        fetchRentalApplicationsCount();
        // Also refresh properties in case any were added/edited/deleted
        fetchPropertiesFromAPI();
    }
}