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

public class LandlordRentedPropertiesFragment extends Fragment {

    private static final String TAG = "LandlordRentedFragment";
    private RecyclerView recyclerView;
    private RentedPropertyAdapter adapter;
    private List<RentedProperty> rentedProperties = new ArrayList<>();
//    private static final String FETCH_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_landlord_rented_properties.php";
    private static final String FETCH_URL = "http://10.0.2.2/rentlify/get_landlord_rented_properties.php";

    public LandlordRentedPropertiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_landlord_rented_properties, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rentedPropertiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RentedPropertyAdapter(rentedProperties, getContext());
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String landlordId = prefs.getString("userId", "");

        if (!landlordId.isEmpty()) {
            fetchRentedProperties(landlordId);
        } else {
            Toast.makeText(getContext(), "User not logged in or missing ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRentedProperties(String landlordId) {
        String url = FETCH_URL + "?landlord_id=" + landlordId;
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

                                // Updated to match PHP response field names
                                RentedProperty property = new RentedProperty(
                                        obj.getInt("rental_id"),
                                        obj.getInt("property_id"),
                                        obj.getInt("user_id"), // Changed from tenant_id to user_id
                                        obj.getString("property_title"),
                                        obj.getString("location"), // Changed from address to location
                                        obj.getString("price"), // Changed from rental_price to price
                                        obj.getString("tenant_name"),
                                        obj.getString("tenant_email"),
                                        obj.getString("tenant_phone"),
                                        obj.getString("rented_at")
                                );
                                rentedProperties.add(property);
                            }
                            adapter.notifyDataSetChanged();

                            if (rentedProperties.isEmpty() && isAdded()) {
                                Toast.makeText(getContext(), "No rented properties found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = response.optString("message", "Unknown error");
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage(), e);
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Failed to parse response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching data: " + error.toString(), error);
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }
}