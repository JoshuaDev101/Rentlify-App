package com.example.rentalpropertyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TenantRentedPropertiesFragment extends Fragment {

    private static final String TAG = "TenantRentedFragment";
    private RecyclerView recyclerView;
    private TenantRentedPropertyAdapter adapter;
    private List<TenantRentedProperty> rentedProperties = new ArrayList<>();
//    private static final String FETCH_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_tenant_rented_properties.php";
    private static final String FETCH_URL = "http://10.0.2.2/rentlify/get_tenant_rented_properties.php";

    public TenantRentedPropertiesFragment() {
        // Required empty public constructor
    }

    public static TenantRentedPropertiesFragment newInstance() {
        return new TenantRentedPropertiesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_tenant_rented_properties, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.tenantRentedPropertiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TenantRentedPropertyAdapter(rentedProperties, getContext());
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String tenantId = prefs.getString("userId", "");

        if (!tenantId.isEmpty()) {
            fetchRentedProperties(tenantId);
        } else {
            Toast.makeText(getContext(), "User not logged in or missing ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRentedProperties(String tenantId) {
        String url = FETCH_URL + "?tenant_id=" + tenantId;
        Log.d(TAG, "Fetching rented properties from: " + url);

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONArray rentalsArray = response.getJSONArray("rentals");
                            Log.d(TAG, "Received " + rentalsArray.length() + " rented properties");

                            rentedProperties.clear();
                            for (int i = 0; i < rentalsArray.length(); i++) {
                                JSONObject obj = rentalsArray.getJSONObject(i);
                                TenantRentedProperty property = new TenantRentedProperty(
                                        obj.getInt("rental_id"),
                                        obj.getInt("property_id"),
                                        obj.getString("property_title"),
                                        obj.getString("address"),
                                        obj.getString("rental_price"),
                                        obj.getString("description"),
                                        obj.getInt("landlord_id"),
                                        obj.getString("landlord_name"),
                                        obj.getString("landlord_email"),
                                        obj.getString("landlord_phone"),
                                        obj.getString("rented_at")
                                );
                                rentedProperties.add(property);
                            }
                            adapter.notifyDataSetChanged();

                            if (rentedProperties.isEmpty()) {
                                Toast.makeText(getContext(), "No rented properties found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = response.optString("message", "Unknown error");
                            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching data: " + error.toString(), error);
                    Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}