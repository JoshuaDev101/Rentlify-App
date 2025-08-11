package com.example.rentalpropertyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TenantTimelineFragment extends Fragment {

    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private List<Property> propertyList = new ArrayList<>();
    private List<Property> fullPropertyList = new ArrayList<>();
    private static final String TAG = "TenantTimelineFragment";
// private String fetchUrl = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_user_properties.php";
    private String fetchUrl = "http://10.0.2.2/rentlify/get_user_properties.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tenant_fragment_timeline, container, false);

        recyclerView = view.findViewById(R.id.propertiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PropertyAdapter(propertyList, getContext());
        recyclerView.setAdapter(adapter);

        // SearchView Logic
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProperties(newText);
                return true;
            }
        });

        fetchProperties();

        return view;
    }

    private void fetchProperties() {
        Log.d(TAG, "Fetching properties from: " + fetchUrl);
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fetchUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray properties = response.getJSONArray("properties");
                            propertyList.clear();
                            fullPropertyList.clear();

                            for (int i = 0; i < properties.length(); i++) {
                                JSONObject propertyObj = properties.getJSONObject(i);

                                Property property = new Property();
                                property.setId(propertyObj.optInt("id"));
                                property.setTitle(propertyObj.optString("title"));
                                property.setDescription(propertyObj.optString("description"));
                                property.setLocation(propertyObj.optString("location"));
                                property.setPropertyType(propertyObj.optString("property_type"));
                                property.setPrice(propertyObj.optDouble("price"));
                                property.setDatePosted(propertyObj.optString("date_posted"));
                                property.setImageUrl(propertyObj.optString("image_url"));
                                property.setUsername(propertyObj.optString("username"));
                                property.setProfileImage(propertyObj.optString("profile_image", ""));

                                // Add these lines to get bedroom, bathroom, and area
                                property.setBedroom(propertyObj.optInt("bedroom", 0));
                                property.setBathroom(propertyObj.optInt("bathroom", 0));
                                property.setArea(propertyObj.optDouble("area", 0.0));

                                // Year built might be null
                                if (!propertyObj.isNull("year_built")) {
                                    property.setYearBuilt(propertyObj.optInt("year_built"));
                                }

                                // Debug log to verify data
                                Log.d(TAG, "Property ID: " + property.getId() +
                                        ", Bedroom: " + property.getBedroom() +
                                        ", Bathroom: " + property.getBathroom() +
                                        ", Area: " + property.getArea());

                                propertyList.add(property);
                                fullPropertyList.add(property);
                            }

                            adapter.updateList(propertyList);

                            // Log the count of properties retrieved
                            Log.d(TAG, "Successfully loaded " + propertyList.size() + " properties");

                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching properties: " + error.getMessage());
                        Toast.makeText(getContext(), "Error fetching properties", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    private void filterProperties(String query) {
        List<Property> filteredList = new ArrayList<>();
        for (Property property : fullPropertyList) {
            if (property.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    property.getLocation().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(property);
            }
        }
        adapter.updateList(filteredList);
    }
}