package com.example.rentalpropertyapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TenantBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "TenantBottomSheet"; // Tag for logging
    private static final String ARG_PROPERTY_ID = "property_id";

    // API URLs defined at the top for easy maintenance
    // Development URLs (localhost)
    private static final String APPLY_RENT_URL = "http://10.0.2.2/rentlify/apply_rent.php";
    private static final String GET_AVAILABLE_DATES_URL = "http://10.0.2.2/rentlify/get_properties_with_dates.php";
    private static final String SCHEDULE_VISIT_URL = "http://10.0.2.2/rentlify/schedule_visit.php";
    private static final String GET_LANDLORD_INFO_URL = "http://10.0.2.2/rentlify/get_landlord_info.php";

    // Production URLs (web server)
//    private static final String APPLY_RENT_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/apply_rent.php";
//    private static final String GET_AVAILABLE_DATES_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_properties_with_dates.php";
//    private static final String SCHEDULE_VISIT_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/schedule_visit.php";
//    private static final String GET_LANDLORD_INFO_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_landlord_info.php";

    private int propertyId;
    private String tenantId;

    public static TenantBottomSheet newInstance(int propertyId) {
        TenantBottomSheet fragment = new TenantBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_PROPERTY_ID, propertyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            propertyId = getArguments().getInt(ARG_PROPERTY_ID);
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        tenantId = prefs.getString("userId", null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheetlayout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialCardView applyNowButton = view.findViewById(R.id.apply_for_rent_option);
        MaterialCardView scheduleVisitButton = view.findViewById(R.id.schedule_visit_option);
        MaterialCardView viewDetailsButton = view.findViewById(R.id.view_details_option);
        MaterialCardView saveButton = view.findViewById(R.id.save_button);

        applyNowButton.setOnClickListener(v -> {
            if (tenantId == null) {
                showToast("Tenant not logged in");
                return;
            }
            applyForRent();
        });

        scheduleVisitButton.setOnClickListener(v -> fetchAvailableDatesAndShowPicker());

        viewDetailsButton.setOnClickListener(v -> fetchAndShowLandlordDetails());

        saveButton.setOnClickListener(v -> showToast("Property saved"));
    }

    private void fetchAndShowLandlordDetails() {
        // Show loading dialog
        AlertDialog loadingDialog = showLoadingDialog();

        // Prepare the request
        StringRequest request = new StringRequest(Request.Method.POST, GET_LANDLORD_INFO_URL,
                response -> {
                    // Dismiss loading dialog
                    loadingDialog.dismiss();

                    Log.d(TAG, "Landlord info response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.getString("status"))) {
                            JSONObject landlordInfo = obj.getJSONObject("landlord_info");
                            showLandlordDetailsDialog(landlordInfo);
                        } else {
                            showCustomErrorDialog("Error", obj.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse landlord info: " + e.getMessage());
                        showCustomErrorDialog("Error", "Failed to retrieve landlord information. Please try again.");
                    }
                },
                error -> {
                    // Dismiss loading dialog
                    loadingDialog.dismiss();

                    Log.e(TAG, "Network error: " + error.getMessage(), error);
                    showCustomErrorDialog("Network Error", "Please check your connection and try again.");
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("property_id", String.valueOf(propertyId));
                return params;
            }
        };

        // Set timeout for the request
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,  // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private AlertDialog showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.loading_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
        return dialog;
    }

    // This is the modified method to handle and display the landlord's avatar
    private void showLandlordDetailsDialog(JSONObject landlordInfo) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.landlord_details_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        try {
            // Find views in the dialog layout
            ImageView landlordAvatar = dialogView.findViewById(R.id.landlord_avatar);
            TextView landlordName = dialogView.findViewById(R.id.landlord_name);
            TextView propertyTitle = dialogView.findViewById(R.id.property_title);
            TextView propertyLocation = dialogView.findViewById(R.id.property_location);
            TextView landlordPhone = dialogView.findViewById(R.id.landlord_phone);
            TextView landlordEmail = dialogView.findViewById(R.id.landlord_email);
            TextView landlordAddress = dialogView.findViewById(R.id.landlord_address);
            Button closeButton = dialogView.findViewById(R.id.close_button);

            // Social media icons
            ImageView iconFacebook = dialogView.findViewById(R.id.icon_facebook);
            ImageView iconInstagram = dialogView.findViewById(R.id.icon_instagram);
            ImageView iconTwitter = dialogView.findViewById(R.id.icon_twitter);
            ImageView iconLinkedin = dialogView.findViewById(R.id.icon_linkedin);

            // Set values from the JSON response
            landlordName.setText(landlordInfo.getString("fullname"));
            propertyTitle.setText(landlordInfo.getJSONObject("property").getString("title"));
            propertyLocation.setText(landlordInfo.getJSONObject("property").getString("location"));
            landlordPhone.setText(landlordInfo.getString("phone"));
            landlordEmail.setText(landlordInfo.getString("email"));
            landlordAddress.setText(landlordInfo.getString("address"));

            // Load landlord's avatar if available
            String avatarUrl = landlordInfo.isNull("avatar") ? null : landlordInfo.getString("avatar");
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                // Determine the base URL from one of the API URLs
                String baseUrl = "http://10.0.2.2/rentlify/";
                String fullAvatarUrl = baseUrl + avatarUrl;

                // Use Glide or Picasso to load the image
                // For this example, I'll use Glide which needs to be added to your build.gradle
                try {
                    Glide.with(requireContext())
                            .load(fullAvatarUrl)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .circleCrop()
                            .into(landlordAvatar);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading avatar: " + e.getMessage());
                    landlordAvatar.setImageResource(R.drawable.ic_person);
                }
            } else {
                // Set default avatar if none available
                landlordAvatar.setImageResource(R.drawable.ic_person);
            }

            // Handle social media icons visibility based on availability
            JSONObject socialMedia = landlordInfo.getJSONObject("social_media");

            setupSocialMediaIcon(iconFacebook, socialMedia.getString("facebook"));
            setupSocialMediaIcon(iconInstagram, socialMedia.getString("instagram"));
            setupSocialMediaIcon(iconTwitter, socialMedia.getString("twitter"));
            setupSocialMediaIcon(iconLinkedin, socialMedia.getString("linkedin"));

            // Set click listener for close button
            closeButton.setOnClickListener(v -> alertDialog.dismiss());

            alertDialog.show();
        } catch (JSONException e) {
            Log.e(TAG, "Error setting landlord details: " + e.getMessage());
            showCustomErrorDialog("Error", "Failed to display landlord information. Please try again.");
        }
    }

    private void setupSocialMediaIcon(ImageView icon, String url) {
        if (url == null || url.isEmpty()) {
            icon.setVisibility(View.GONE);
        } else {
            icon.setVisibility(View.VISIBLE);
            icon.setOnClickListener(v -> {
                // Handle opening the social media link
                // You might want to use an Intent to open the URL in a browser
                showToast("Opening social media link");
            });
        }
    }

    private void applyForRent() {
        // Debug logging to verify values
        Log.d(TAG, "Applying for rent - Tenant ID: " + tenantId + ", Property ID: " + propertyId);

        // Check if values are valid
        if (tenantId == null || tenantId.isEmpty() || propertyId <= 0) {
            showCustomErrorDialog("Invalid Data", "Your user data or property data is invalid. Please try logging in again.");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APPLY_RENT_URL,
                response -> {
                    try {
                        Log.d(TAG, "Apply Response: " + response);
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.getString("status"))) {
                            showCustomSuccessDialog("Application Submitted",
                                    "Your rental application has been submitted successfully. The landlord will review your application shortly.");
                            dismiss();
                        } else {
                            // Handle the specific case where user already applied
                            String message = obj.getString("message");
                            if (message.contains("already applied")) {
                                showCustomInfoDialog("Already Applied",
                                        "You have already submitted an application for this property. You can check its status in your applications section.");
                            } else {
                                showCustomErrorDialog("Application Failed", message);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Parse error: " + e.getMessage());
                        showCustomErrorDialog("Error", "Unable to process your application. Please try again later.");
                    }
                },
                error -> {
                    String errorMessage = error.toString();
                    if (error.networkResponse != null) {
                        errorMessage += " Status Code: " + error.networkResponse.statusCode;
                    }
                    Log.e(TAG, "Network error: " + errorMessage, error);
                    showCustomErrorDialog("Network Error", "Please check your connection and try again.");
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tenant_id", tenantId);
                params.put("property_id", String.valueOf(propertyId));

                // Log the parameters being sent
                Log.d(TAG, "Request Params: " + params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // Set timeout for the request
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,  // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }

    private void fetchAvailableDatesAndShowPicker() {
        StringRequest request = new StringRequest(Request.Method.POST, GET_AVAILABLE_DATES_URL,
                response -> {
                    Log.d(TAG, "Raw dates response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.getString("status"))) {
                            JSONArray datesArray = obj.getJSONArray("available_dates");

                            ArrayList<String> dateList = new ArrayList<>();
                            for (int i = 0; i < datesArray.length(); i++) {
                                dateList.add(datesArray.getString(i));
                            }

                            if (dateList.isEmpty()) {
                                showCustomInfoDialog("No Available Dates",
                                        "There are currently no available dates for visits to this property. Please check back later.");
                            } else {
                                showCustomDatePickerDialog(dateList);
                            }
                        } else {
                            showCustomErrorDialog("Error", obj.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse dates: " + e.getMessage());
                        showCustomErrorDialog("Error", "Failed to retrieve available dates. Please try again.");
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.getMessage(), error);
                    showCustomErrorDialog("Network Error", "Please check your connection and try again.");
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("property_id", String.valueOf(propertyId));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void showCustomDatePickerDialog(ArrayList<String> dateList) {
        // Create a custom dialog
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_date_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();

        // Set transparent background and rounded corners
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Find views in custom layout
        TextView titleText = dialogView.findViewById(R.id.dialog_title);
        RecyclerView dateRecyclerView = dialogView.findViewById(R.id.date_recycler_view);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        titleText.setText("Select Visit Date");

        // Set up RecyclerView with date options
        dateRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DateAdapter adapter = new DateAdapter(dateList, date -> {
            alertDialog.dismiss();
            sendScheduleVisitRequest(date);
        });
        dateRecyclerView.setAdapter(adapter);

        // Set up cancel button
        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void sendScheduleVisitRequest(String selectedDate) {
        StringRequest request = new StringRequest(Request.Method.POST, SCHEDULE_VISIT_URL,
                response -> {
                    Log.d(TAG, "Schedule response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.getString("status"))) {
                            showScheduleSuccessDialog(selectedDate);
                        } else {
                            showCustomErrorDialog("Scheduling Failed", obj.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        showCustomErrorDialog("Error", "Unable to schedule your visit. Please try again later.");
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.getMessage(), error);
                    showCustomErrorDialog("Network Error", "Please check your connection and try again.");
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tenant_id", tenantId);
                params.put("property_id", String.valueOf(propertyId));
                params.put("visit_date", selectedDate);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void showScheduleSuccessDialog(String selectedDate) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_success_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView titleText = dialogView.findViewById(R.id.success_title);
        TextView messageText = dialogView.findViewById(R.id.success_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        titleText.setText("Visit Scheduled");
        messageText.setText("Your visit has been scheduled for " + selectedDate + ".\n\n" +
                "The landlord will verify this request shortly.\n\n" +
                "You can check the status in 'My Visits' section.");

        okButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            dismiss(); // Dismiss the bottom sheet
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showCustomSuccessDialog(String title, String message) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_success_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView titleText = dialogView.findViewById(R.id.success_title);
        TextView messageText = dialogView.findViewById(R.id.success_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        titleText.setText(title);
        messageText.setText(message);

        okButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void showCustomErrorDialog(String title, String message) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_error_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView titleText = dialogView.findViewById(R.id.error_title);
        TextView messageText = dialogView.findViewById(R.id.error_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        titleText.setText(title);
        messageText.setText(message);

        okButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void showCustomInfoDialog(String title, String message) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_info_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView titleText = dialogView.findViewById(R.id.info_title);
        TextView messageText = dialogView.findViewById(R.id.info_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        titleText.setText(title);
        messageText.setText(message);

        okButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Date adapter for the recycler view
    private static class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
        private final ArrayList<String> dates;
        private final OnDateSelectedListener listener;

        public interface OnDateSelectedListener {
            void onDateSelected(String date);
        }

        public DateAdapter(ArrayList<String> dates, OnDateSelectedListener listener) {
            this.dates = dates;
            this.listener = listener;
        }

        @NonNull
        @Override
        public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.date_item, parent, false);
            return new DateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
            String date = dates.get(position);
            holder.dateText.setText(date);
            holder.itemView.setOnClickListener(v -> listener.onDateSelected(date));
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }

        static class DateViewHolder extends RecyclerView.ViewHolder {
            TextView dateText;

            public DateViewHolder(@NonNull View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.date_text);
            }
        }
    }
}