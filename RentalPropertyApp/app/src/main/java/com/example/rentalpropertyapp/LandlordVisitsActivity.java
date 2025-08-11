package com.example.rentalpropertyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandlordVisitsActivity extends AppCompatActivity {

    private static final String TAG = "LandlordVisitsActivity";
    private static final String GET_PENDING_VISITS_URL = "http://10.0.2.2/rentlify/get_pending_visits.php";
    private static final String UPDATE_VISIT_STATUS_URL = "http://10.0.2.2/rentlify/update_visit_status.php";

    // Alternative hosted URLs (commented out)
    // private static final String GET_PENDING_VISITS_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_pending_visits.php";
    // private static final String UPDATE_VISIT_STATUS_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/update_visit_status.php";

    private RecyclerView recyclerView;
    private VisitRequestAdapter adapter;
    private List<VisitRequest> visitRequests;
    private String landlordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_visits);

        // Initialize UI components
        recyclerView = findViewById(R.id.visits_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize visit requests list
        visitRequests = new ArrayList<>();
        adapter = new VisitRequestAdapter(visitRequests);
        recyclerView.setAdapter(adapter);

        // Get landlord ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        landlordId = prefs.getString("userId", null);

        if (landlordId == null) {
            showToast("Please login to view visit requests");
            finish();
            return;
        }

        // Load visit requests
        loadVisitRequests();
    }

    private void loadVisitRequests() {
        StringRequest request = new StringRequest(Request.Method.POST, GET_PENDING_VISITS_URL,
                response -> {
                    Log.d(TAG, "Visits response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.getString("status"))) {
                            JSONArray visitsArray = obj.getJSONArray("visits");
                            visitRequests.clear();

                            for (int i = 0; i < visitsArray.length(); i++) {
                                JSONObject visit = visitsArray.getJSONObject(i);

                                VisitRequest request1 = new VisitRequest(
                                        visit.getInt("id"),
                                        visit.getInt("tenant_id"),
                                        visit.getInt("property_id"),
                                        visit.getString("visit_date"),
                                        visit.getString("status"),
                                        visit.getString("property_name"),
                                        visit.getString("tenant_name")
                                );

                                visitRequests.add(request1);
                            }

                            adapter.notifyDataSetChanged();

                            if (visitRequests.isEmpty()) {
                                showToast("No pending visit requests");
                            }
                        } else {
                            showToast("Error: " + obj.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse visits: " + e.getMessage());
                        showToast("Failed to parse visits: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.getMessage(), error);
                    showToast("Network error: " + error.getMessage());
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("landlord_id", landlordId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateVisitStatus(int visitId, String status) {
        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_VISIT_STATUS_URL,
                response -> {
                    Log.d(TAG, "Update status response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.getString("status"))) {
                            showToast("Visit " + status + " successfully");
                            // Reload the list to reflect changes
                            loadVisitRequests();
                        } else {
                            showToast("Failed: " + obj.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        showToast("Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.getMessage(), error);
                    showToast("Network error: " + error.getMessage());
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("visit_id", String.valueOf(visitId));
                params.put("status", status);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Model class for visit requests
    private static class VisitRequest {
        private final int id;
        private final int tenantId;
        private final int propertyId;
        private final String visitDate;
        private final String status;
        private final String propertyName;
        private final String tenantName;

        public VisitRequest(int id, int tenantId, int propertyId, String visitDate,
                            String status, String propertyName, String tenantName) {
            this.id = id;
            this.tenantId = tenantId;
            this.propertyId = propertyId;
            this.visitDate = visitDate;
            this.status = status;
            this.propertyName = propertyName;
            this.tenantName = tenantName;
        }
    }

    // Adapter for the RecyclerView
    private class VisitRequestAdapter extends RecyclerView.Adapter<VisitRequestAdapter.ViewHolder> {
        private final List<VisitRequest> visits;

        public VisitRequestAdapter(List<VisitRequest> visits) {
            this.visits = visits;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_visit_request, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VisitRequest visit = visits.get(position);

            holder.propertyNameTextView.setText(visit.propertyName);
            holder.tenantNameTextView.setText(visit.tenantName);
            holder.visitDateTextView.setText(visit.visitDate);

            holder.approveButton.setOnClickListener(v -> updateVisitStatus(visit.id, "approved"));
            holder.rejectButton.setOnClickListener(v -> updateVisitStatus(visit.id, "rejected"));
        }

        @Override
        public int getItemCount() {
            return visits.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView propertyNameTextView;
            public TextView tenantNameTextView;
            public TextView visitDateTextView;
            public Button approveButton;
            public Button rejectButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                propertyNameTextView = itemView.findViewById(R.id.property_name);
                tenantNameTextView = itemView.findViewById(R.id.tenant_name);
                visitDateTextView = itemView.findViewById(R.id.visit_date);
                approveButton = itemView.findViewById(R.id.approve_button);
                rejectButton = itemView.findViewById(R.id.reject_button);
            }
        }
    }
}