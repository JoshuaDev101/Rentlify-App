package com.example.rentalpropertyapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class LandlordRentalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;
    private final List<RentalApplication> applicationList = new ArrayList<>();
//    private static final String FETCH_URL = "http://f29-preview.awardspace.net/rentlify.kesug.com/get_rental_applications.php";
    private static final String FETCH_URL = "http://10.0.2.2/rentlify/get_rental_applications.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_rental);

        recyclerView = findViewById(R.id.applicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplicationAdapter(applicationList, this);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String landlordId = prefs.getString("userId", "");

        if (!landlordId.isEmpty()) {
            fetchApplications(landlordId);
        } else {
            Toast.makeText(this, "User not logged in or missing ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchApplications(String landlordId) {
        String url = FETCH_URL + "?landlord_id=" + landlordId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray apps = response.getJSONArray("applications");

                        applicationList.clear();
                        for (int i = 0; i < apps.length(); i++) {
                            JSONObject obj = apps.getJSONObject(i);
                            RentalApplication application = new RentalApplication(
                                    obj.getInt("application_id"),
                                    obj.getString("tenant_name"),
                                    obj.getString("property_title"),
                                    obj.getString("contact_number"),
                                    obj.getString("status"),
                                    obj.getString("created_at")
                            );
                            applicationList.add(application);
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(request);
    }
}
