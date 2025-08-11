    package com.example.rentalpropertyapp;
    
    import android.content.Context;
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
    import com.android.volley.RequestQueue;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    
    import org.json.JSONObject;
    
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    
    public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {
    
        private List<RentalApplication> applicationList;
        private Context context;
        private static final String TAG = "ApplicationAdapter";
    
        public ApplicationAdapter(List<RentalApplication> applicationList, Context context) {
            this.applicationList = applicationList;
            this.context = context;
            Log.d(TAG, "Adapter initialized with " + applicationList.size() + " items");
        }
    
        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView propertyTitle, tenantName, contact, status, date;
            Button btnAccept, btnReject;
    
            public ViewHolder(View itemView) {
                super(itemView);
                propertyTitle = itemView.findViewById(R.id.propertyTitleText);
                tenantName = itemView.findViewById(R.id.tenantNameText);
                contact = itemView.findViewById(R.id.contactText);
                status = itemView.findViewById(R.id.statusText);
                date = itemView.findViewById(R.id.dateText);
                btnAccept = itemView.findViewById(R.id.btnAccept);
                btnReject = itemView.findViewById(R.id.btnReject);
            }
        }
    
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "Creating new ViewHolder");
            // Change this to use item_application layout instead of item_property
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
            return new ViewHolder(view);
        }
    
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RentalApplication app = applicationList.get(position);
            Log.d(TAG, "Binding item at position " + position + ": " + app.getPropertyTitle());
    
            // Set values with null checks
            if (holder.propertyTitle != null) {
                holder.propertyTitle.setText(app.getPropertyTitle());
            }
    
            if (holder.tenantName != null) {
                holder.tenantName.setText("Tenant: " + app.getTenantName());
            }
    
            if (holder.contact != null) {
                String contact = app.getContactNumber();
                if (contact == null || contact.trim().isEmpty() || contact.equalsIgnoreCase("null")) {
                    holder.contact.setText("Contact: No contact provided");
                } else {
                    holder.contact.setText("Contact: " + contact);
                }
            }
    
            if (holder.status != null) {
                holder.status.setText("Status: " + app.getStatus());
            }
    
            if (holder.date != null) {
                String[] dateParts = app.getCreatedAt().split(" ");
                String dateOnly = dateParts.length > 0 ? dateParts[0] : app.getCreatedAt();
                holder.date.setText("Applied on: " + dateOnly);
            }
    
            // Add null checks before setting click listeners
            if (holder.btnAccept != null) {
                holder.btnAccept.setOnClickListener(v -> updateApplicationStatus(app.getApplicationId(), "approved", position));
    
                // Hide accept button if application is not pending
                if (!app.getStatus().equals("pending")) {
                    holder.btnAccept.setVisibility(View.GONE);
                } else {
                    holder.btnAccept.setVisibility(View.VISIBLE);
                }
            }
    
            if (holder.btnReject != null) {
                holder.btnReject.setOnClickListener(v -> updateApplicationStatus(app.getApplicationId(), "rejected", position));
    
                // Hide reject button if application is not pending
                if (!app.getStatus().equals("pending")) {
                    holder.btnReject.setVisibility(View.GONE);
                } else {
                    holder.btnReject.setVisibility(View.VISIBLE);
                }
            }
        }
    
        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: " + applicationList.size());
            return applicationList.size();
        }
    //        String url = "http://f29-preview.awardspace.net/rentlify.kesug.com/update_application_status.php"; // Use actual server URL in production
        private void updateApplicationStatus(int applicationId, String newStatus, int position) {
            String url = "http://10.0.2.2/rentlify/update_application_status.php"; // Localhost for emulator
    //        String url = "http://f29-preview.awardspace.net/rentlify.kesug.com/update_application_status.php"; // Localhost for emulator
            Log.d(TAG, "Updating status for application ID " + applicationId + " to " + newStatus);
    
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d(TAG, "Status update response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            if (status.equals("success")) {
                                // Update status in list and notify RecyclerView
                                applicationList.get(position).setStatus(newStatus);
                                notifyItemChanged(position);
                                Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonResponse.optString("message", "Unknown error");
                                Toast.makeText(context, "Failed: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(context, "Response parse error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Status update error: " + error.toString(), error);
                        Toast.makeText(context, "Failed to update status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("application_id", String.valueOf(applicationId)); // Send as string
                    params.put("status", newStatus);
                    return params;
                }
            };

            queue.add(request);
        }
    }