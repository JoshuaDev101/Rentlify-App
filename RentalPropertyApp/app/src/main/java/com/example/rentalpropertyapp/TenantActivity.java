package com.example.rentalpropertyapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TenantActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tenant_activity);

        // Initialize views
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Bottom navigation item selection
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navdashboard) {
                selectedFragment = new TenantTimelineFragment();
            } else if (itemId == R.id.navMyrequest) {
                selectedFragment = new TenantFragmentMyRequest();
            } else if (itemId == R.id.navNotifications) {
                selectedFragment = new TenantFragmentNotification();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new TenantProfileFragment();
            } else if (itemId == R.id.fab) {
                selectedFragment = new TenantRentedPropertiesFragment();
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
            bottomNavigation.setSelectedItemId(R.id.navdashboard);
        }
    }
}