package com.example.rentalpropertyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TenantProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int EDIT_PROFILE_REQUEST = 102;
//  private static final String URL_USER_DETAILS = "http://f29-preview.awardspace.net/rentlify.kesug.com/tenant_get_user_details.php";

    private static final String URL_USER_DETAILS = "http://10.0.2.2/rentlify/tenant_get_user_details.php";


    private ShapeableImageView profileImage;
    private TextView usernameTextView, fullnameTextView, sexTextView, emailTextView, phoneTextView, birthdayTextView;
    private String userId;
    private RequestQueue requestQueue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.landlord_fragment_profile, container, false);

        // Get userId from intent if available
        Activity activity = getActivity();
        if (activity != null) {
            userId = activity.getIntent().getStringExtra("userid");
        }

        // Connect all views from XML
        profileImage = v.findViewById(R.id.profile_image);
        usernameTextView = v.findViewById(R.id.userName);
        fullnameTextView = v.findViewById(R.id.fullname_text_view);
        sexTextView = v.findViewById(R.id.sex_text_view);
        emailTextView = v.findViewById(R.id.email_text_view);
        phoneTextView = v.findViewById(R.id.phone_text_view);
        birthdayTextView = v.findViewById(R.id.birthday_text_view);

        // Connect menu options
        v.findViewById(R.id.edit_profile_option).setOnClickListener(b -> {
            if (!isAdded()) return;
            Intent intent = new Intent(getActivity(), TenantEditProfileActivity.class);
            intent.putExtra("userid", userId);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });

        v.findViewById(R.id.change_password_option).setOnClickListener(b -> {
            if (isAdded()) showToast("Change Password feature coming soon!");
        });

        v.findViewById(R.id.my_properties_option).setOnClickListener(b -> {
            if (isAdded()) showToast("My Properties feature coming soon!");
        });

        v.findViewById(R.id.settings_button).setOnClickListener(b -> {
            if (isAdded()) showToast("Settings feature coming soon!");
        });

        v.findViewById(R.id.report_button).setOnClickListener(b -> {
            if (isAdded()) showToast("Report feature coming soon!");
        });

        v.findViewById(R.id.logout_button).setOnClickListener(b -> {
            if (isAdded()) logout();
        });

        // Set click listener on profile image
        profileImage.setOnClickListener(b -> {
            if (isAdded()) {
                showToast("Select a new profile picture");
                openImagePicker();
            }
        });

        fetchUserDetails();
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(this); // Cancel all requests with this tag
        }
    }

    private void logout() {
        Activity activity = getActivity();
        if (activity == null) return;

        // Clear shared preferences before logout
        SharedPreferences prefs = activity.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Return to login screen
        startActivity(new Intent(activity, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        activity.finish();
    }

    private void openImagePicker() {
        ImagePicker.with(this)
                .cropSquare()                // Crop image to 1:1 (square)
                .compress(1024)              // Final image max size will be below 1 MB
                .maxResultSize(1080, 1080)   // Max resolution of 1080x1080
                .start(PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!isAdded()) return;

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Get the Uri of the selected and cropped image
                Uri imageUri = data.getData();
                try {
                    // Convert Uri to Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().getContentResolver(), imageUri);

                    // Update UI with cropped image
                    profileImage.setImageBitmap(bitmap);

                    // Upload the cropped image
                    uploadProfileImage(bitmap);
                } catch (IOException e) {
                    showToast("Error processing image: " + e.getMessage());
                }
            }
        } else if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Refresh user details after editing profile
            fetchUserDetails();
            showToast("Profile updated successfully");
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            showToast(ImagePicker.getError(data));
        } else if (resultCode == Activity.RESULT_CANCELED) {
            showToast("Action cancelled");
        }
    }

    private void uploadProfileImage(Bitmap bitmap) {
        if (!isAdded()) return;

        showToast("Uploading profile image...");

        VolleyMultipartRequest request = new VolleyMultipartRequest(
                Request.Method.POST, URL_USER_DETAILS,
                response -> {
                    if (!isAdded()) return;

                    try {
                        JSONObject res = new JSONObject(new String(response.data));
                        if (res.has("upload_message")) {
                            showToast(res.getString("upload_message"));
                        } else if (res.has("message")) {
                            showToast(res.getString("message"));
                        } else {
                            showToast("Profile image updated successfully!");
                        }
                    } catch (JSONException e) {
                        showToast("Invalid server response: " + e.getMessage());
                    }
                },
                error -> {
                    if (isAdded()) {
                        showToast("Upload failed: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("id", userId);
                    put("action", "update_profile_image");
                }};
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                return new HashMap<String, DataPart>() {{
                    put("profile_image", new DataPart("profile.jpg", baos.toByteArray(), "image/jpeg"));
                }};
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(this);
        requestQueue.add(request);
    }

    private void fetchUserDetails() {
        if (userId == null || userId.isEmpty()) {
            if (isAdded()) {
                showToast("User ID missing");
            }
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                response -> {
                    // Check if fragment is still attached before processing response
                    if (!isAdded()) {
                        return;
                    }

                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("1".equals(obj.getString("success"))) {
                            JSONObject user = obj.getJSONArray("user").getJSONObject(0);

                            // Update username
                            usernameTextView.setText(user.getString("username"));

                            // Update fullname
                            if (user.has("fullname") && !user.isNull("fullname"))
                                fullnameTextView.setText(user.getString("fullname"));
                            else
                                fullnameTextView.setText("Not provided");

                            // Update sex
                            if (user.has("sex") && !user.isNull("sex"))
                                sexTextView.setText(user.getString("sex"));
                            else
                                sexTextView.setText("Not provided");

                            // Update email
                            if (user.has("email") && !user.isNull("email"))
                                emailTextView.setText(user.getString("email"));
                            else
                                emailTextView.setText("Not provided");

                            // Update phone
                            if (user.has("phone") && !user.isNull("phone"))
                                phoneTextView.setText(user.getString("phone"));
                            else
                                phoneTextView.setText("Not provided");

                            // Update birthday
                            if (user.has("birthday") && !user.isNull("birthday"))
                                birthdayTextView.setText(user.getString("birthday"));
                            else
                                birthdayTextView.setText("Not provided");

                            // Update profile image
                            if (user.has("profile_image") && !user.isNull("profile_image"))
//                                new DownloadImageTask(profileImage).execute("http://f29-preview.awardspace.net/rentlify.kesug.com/" + user.getString("profile_image"));
                                new DownloadImageTask(profileImage).execute("http://10.0.2.2/rentlify/" + user.getString("profile_image"));

                            // Safe access to activity for SharedPreferences
                            Activity activity = getActivity();
                            if (activity != null) {
                                // Save user data to SharedPreferences
                                SharedPreferences.Editor editor = activity
                                        .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit();
                                editor.putString("username", user.getString("username"));
                                editor.putString("landlord_id", userId);

                                // Save additional fields to shared preferences
                                if (user.has("fullname") && !user.isNull("fullname"))
                                    editor.putString("fullname", user.getString("fullname"));

                                if (user.has("sex") && !user.isNull("sex"))
                                    editor.putString("sex", user.getString("sex"));

                                if (user.has("email") && !user.isNull("email"))
                                    editor.putString("email", user.getString("email"));

                                if (user.has("phone") && !user.isNull("phone"))
                                    editor.putString("phone", user.getString("phone"));

                                if (user.has("birthday") && !user.isNull("birthday"))
                                    editor.putString("birthday", user.getString("birthday"));

                                editor.apply();
                            }
                        } else {
                            showToast(obj.getString("message"));
                        }
                    } catch (JSONException e) {
                        showToast("Parse error: " + e.getMessage());
                    }
                },
                error -> {
                    if (isAdded()) {
                        showToast("Network error: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return new HashMap<String, String>() {{
                    put("id", userId);
                    put("action", "get_details");
                }};
            }
        };

        request.setTag(this);
        requestQueue.add(request);
    }

    private void showToast(String msg) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ShapeableImageView view;
        private boolean cancelled = false;

        DownloadImageTask(ShapeableImageView v) {
            view = v;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancelled = true;
        }

        protected Bitmap doInBackground(String... urls) {
            try (InputStream in = new URL(urls[0]).openStream()) {
                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if (!cancelled && result != null && view != null) {
                view.setImageBitmap(result);
            }
        }
    }
}