package com.example.rentalpropertyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TenantFragmentNotification extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId;
    private static final String FETCH_NOTIFICATIONS_URL = "http://10.0.2.2/rentlify/get_tenant_notifications.php";
    private static final String UPDATE_NOTIFICATION_URL = "http://10.0.2.2/rentlify/update_notification_status.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Get user ID from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchNotifications);

        fetchNotifications();

        return view;
    }

    private void fetchNotifications() {
        notificationList.clear();
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_NOTIFICATIONS_URL,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("success")) {
                            JSONArray notifications = jsonObject.getJSONArray("notifications");

                            for (int i = 0; i < notifications.length(); i++) {
                                JSONObject notification = notifications.getJSONObject(i);

                                int notificationId = notification.getInt("id");
                                String status = notification.getString("status");
                                String requestType = notification.getString("request");
                                int propertyId = notification.getInt("property_id");
                                String message = notification.getString("message");
                                boolean readStatus = notification.getInt("read_status") == 1;
                                String createdAt = notification.getString("created_at");

                                // Format date
                                String formattedDate = formatDate(createdAt);

                                NotificationItem item = new NotificationItem(
                                        notificationId,
                                        status,
                                        requestType,
                                        propertyId,
                                        message,
                                        readStatus,
                                        formattedDate
                                );

                                notificationList.add(item);
                            }

                            adapter.notifyDataSetChanged();

                            if (notificationList.isEmpty()) {
                                emptyText.setVisibility(View.VISIBLE);
                                emptyText.setText("No notifications");
                            }
                        } else {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText(jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        emptyText.setVisibility(View.VISIBLE);
                        emptyText.setText("Error parsing data");
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("Network error. Pull down to try again.");
                    error.printStackTrace();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tenant_id", userId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    @Override
    public void onNotificationClick(NotificationItem notification) {
        // Update notification status to read
        updateNotificationStatus(notification.getId());

        // For tenants, show details of their request status
        String requestType = notification.getRequest();
        if (requestType.equals("rent")) {
            // Open RentStatusActivity
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new TenantFragmentMyRequest());
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (requestType.equals("visit")) {
            // Open VisitStatusActivity
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new TenantFragmentMyRequest());
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    private void updateNotificationStatus(int notificationId) {
        StringRequest updateRequest = new StringRequest(Request.Method.POST, UPDATE_NOTIFICATION_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            // Update the local notification list
                            for (NotificationItem item : notificationList) {
                                if (item.getId() == notificationId) {
                                    item.setReadStatus(true);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("notification_id", String.valueOf(notificationId));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(updateRequest);
    }
}