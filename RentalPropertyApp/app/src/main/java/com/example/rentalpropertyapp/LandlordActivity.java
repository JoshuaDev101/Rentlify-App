package com.example.rentalpropertyapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * @noinspection ALL
 */
public class LandlordActivity extends AppCompatActivity {

    private static final String TAG = "LandlordActivity";
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord);

        // Add debugging code to check user ID in SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", "not found");
        Log.d(TAG, "User ID in SharedPreferences: " + userId);

        // Also check if intent extras contain the user ID
        String intentUserId = getIntent().getStringExtra("userid");
        Log.d(TAG, "User ID from Intent: " + intentUserId);

        // If the IDs don't match or SharedPreferences doesn't have the ID, update it from intent
        if (userId.equals("not found") && intentUserId != null && !intentUserId.isEmpty()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userId", intentUserId);
            editor.apply();
            Log.d(TAG, "Updated SharedPreferences with User ID from intent: " + intentUserId);
        }

        // Initialize views
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Bottom navigation item selection
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_properties) {
                selectedFragment = new Landlord_Fragment_Dashboard();
            } else if (itemId == R.id.navigation_tenants) {
                selectedFragment = new LandlordRentedPropertiesFragment();
            } else if (itemId == R.id.navigation_notifications) {
                selectedFragment = new LandlordNotificationFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new Landlord_Fragment_Profile();
            } else if (itemId == R.id.fab) {
                selectedFragment = new Landlord_Fragment_Post();
            }
            // Replace the fragment
            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                return true;
            }
            return false;
        });

        // Set default fragment (optional)
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.navigation_properties);
        }
    }
}