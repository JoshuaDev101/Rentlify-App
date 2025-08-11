    package com.example.rentalpropertyapp;
    
    import android.content.Context;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.util.Log;
    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;
    
    import androidx.recyclerview.widget.RecyclerView;
    import androidx.recyclerview.widget.LinearLayoutManager;
    
    import com.android.volley.DefaultRetryPolicy;
    import com.android.volley.Request;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    
    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;
    
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.Map;
    
    public class TenantFragmentMyRequest extends Fragment implements TenantRequestAdapter.OnRequestCancelledListener {
    
        private static final String TAG = "TenantFragmentMyRequest";
        private RecyclerView recyclerView;
        private TenantRequestAdapter adapter;
        private ArrayList<TenantRequestModel> requestList;
        private String tenantId;
        private Button btnRentRequest, btnVisitRequest;
        private TextView activeCount, pendingCount, completedCount;
        private String currentFilterType = "all"; // Default to show all requests
    
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tenant_fragment_myrequest, container, false);
    
            recyclerView = view.findViewById(R.id.recycler_view_timeline);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    
            // Initialize filter buttons
            btnRentRequest = view.findViewById(R.id.btn_rent_request);
            btnVisitRequest = view.findViewById(R.id.btn_visit_request);
    
            // Find stats TextViews - find them by their parent view IDs to avoid issues
            try {
                View statsCard = view.findViewById(R.id.stats_card);
                if (statsCard != null) {
                    ViewGroup statsLayout = (ViewGroup) ((ViewGroup) statsCard).getChildAt(0);

                    // First stat (Active)
                    ViewGroup firstStatLayout = (ViewGroup) statsLayout.getChildAt(0);
                    activeCount = (TextView) firstStatLayout.getChildAt(1);
    
                    // Second stat (Pending)
                    ViewGroup secondStatLayout = (ViewGroup) statsLayout.getChildAt(2);
                    pendingCount = (TextView) secondStatLayout.getChildAt(1);
    
                    // Third stat (Completed)
                    ViewGroup thirdStatLayout = (ViewGroup) statsLayout.getChildAt(4);
                    completedCount = (TextView) thirdStatLayout.getChildAt(1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error finding stats TextViews", e);
            }
    
            // Set up click listeners for filter buttons
            btnRentRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFilterType = "rental";
                    updateButtonStates();
                    filterRequests(currentFilterType);
                }
            });
    
            btnVisitRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFilterType = "visit";
                    updateButtonStates();
                    filterRequests(currentFilterType);
                }
            });
    
            requestList = new ArrayList<>();
            adapter = new TenantRequestAdapter(requestList);
            adapter.setOnRequestCancelledListener(this);
            recyclerView.setAdapter(adapter);
    
            // Get tenant ID from shared preferences
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            tenantId = prefs.getString("userId", null);
    
            if (tenantId != null) {
                fetchTenantRequests();
            } else {
                Toast.makeText(getContext(), "Tenant not logged in", Toast.LENGTH_SHORT).show();
            }
    
            return view;
        }
    
        private void updateButtonStates() {
            // Update button states based on current filter
            if (currentFilterType.equals("rental")) {
                btnRentRequest.setBackgroundColor(getResources().getColor(R.color.purple_500));
                btnRentRequest.setTextColor(getResources().getColor(android.R.color.white));
                btnVisitRequest.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnVisitRequest.setTextColor(getResources().getColor(R.color.gray));
            } else if (currentFilterType.equals("visit")) {
                btnVisitRequest.setBackgroundColor(getResources().getColor(R.color.purple_500));
                btnVisitRequest.setTextColor(getResources().getColor(android.R.color.white));
                btnRentRequest.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnRentRequest.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    
        private void filterRequests(String filterType) {
            if (filterType.equals("all")) {
                adapter = new TenantRequestAdapter(requestList);
            } else {
                ArrayList<TenantRequestModel> filteredList = new ArrayList<>();
                for (TenantRequestModel request : requestList) {
                    if (request.getRequestType().equals(filterType)) {
                        filteredList.add(request);
                    }
                }
                adapter = new TenantRequestAdapter(filteredList);
            }
    
            adapter.setOnRequestCancelledListener(this);
            recyclerView.setAdapter(adapter);
        }
    
        @Override
        public void onRequestCancelled() {
            // Refresh data when a request is cancelled
            fetchTenantRequests();
        }
    
        private void fetchTenantRequests() {
    //        String url = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_tenant_requests.php";
            String url = "http://10.0.2.2/rentlify/get_tenant_requests.php";
    
            // Log the request URL and parameters
            Log.d(TAG, "Fetching requests for tenant ID: " + tenantId);
            Log.d(TAG, "URL: " + url);
    
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Log the response
                            Log.d(TAG, "Response: " + response);
    
                            try {
                                JSONArray array = new JSONArray(response);
                                requestList.clear();
    
                                int activeCounter = 0;
                                int pendingCounter = 0;
                                int completedCounter = 0;
    
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    TenantRequestModel requestModel = new TenantRequestModel(
                                            obj.getString("title"),
                                            obj.getString("status"),
                                            obj.getString("requested_date"),
                                            obj.getString("request_type"),
                                            obj.getString("request_id")
                                    );
                                    requestList.add(requestModel);
    
                                    // Count statistics
                                    String status = obj.getString("status").toLowerCase();
                                    if (status.equals("approved")) {
                                        activeCounter++;
                                    } else if (status.equals("pending")) {
                                        pendingCounter++;
                                    } else if (status.equals("rejected") || status.equals("cancelled")) {
                                        completedCounter++;
                                    }
                                }
    
                                // Update statistics if views are available
                                if (activeCount != null) activeCount.setText(String.valueOf(activeCounter));
                                if (pendingCount != null) pendingCount.setText(String.valueOf(pendingCounter));
                                if (completedCount != null) completedCount.setText(String.valueOf(completedCounter));
    
                                adapter.notifyDataSetChanged();
    
                                // Apply current filter
                                if (!currentFilterType.equals("all")) {
                                    filterRequests(currentFilterType);
                                }
    
                                // Log the number of items loaded
                                Log.d(TAG, "Loaded " + array.length() + " requests");
    
                                if (array.length() == 0) {
                                    // Show message if no requests found
                                    Toast.makeText(getContext(), "No requests found", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Parsing error: " + e.getMessage(), e);
                                Toast.makeText(getContext(), "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // More detailed error logging
                            String errorMsg = "Unknown error";
                            if (error.networkResponse != null) {
                                errorMsg = "Error code: " + error.networkResponse.statusCode;
    
                                // Try to get response body on error
                                String responseBody = new String(error.networkResponse.data);
                                Log.e(TAG, "Error response body: " + responseBody);
                            } else if (error.getMessage() != null) {
                                errorMsg = error.getMessage();
                            }
                            Log.e(TAG, "Network error: " + errorMsg, error);
                            Toast.makeText(getContext(), "Network error: " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("tenant_id", tenantId);
                    Log.d(TAG, "Request params: tenant_id=" + tenantId);
                    return params;
                }
    
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
            };
    
            // Set a longer timeout to give the server more time to respond
            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000, // 30 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
    
            // Tag the request for easy cancellation if needed
            request.setTag(TAG);
    
            // Add the request to the RequestQueue
            Volley.newRequestQueue(requireContext()).add(request);
        }
    
        @Override
        public void onStop() {
            super.onStop();
            // Cancel any pending requests when fragment is stopped
            if (getContext() != null) {
                Volley.newRequestQueue(getContext()).cancelAll(TAG);
            }
        }
    }