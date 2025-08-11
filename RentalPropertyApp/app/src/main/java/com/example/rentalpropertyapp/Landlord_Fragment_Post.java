package com.example.rentalpropertyapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Landlord_Fragment_Post extends Fragment {
    private TextInputEditText title, description, location, type, price;
    private TextInputEditText bedroomsInput, bathroomsInput, areaInput, yearInput;
    private TextInputEditText availabilityDates;
    private FloatingActionButton addImageButton;
    private MaterialButton postButton;
    private ImageView previewImage;
    private LinearLayout imagePickerPlaceholder;
    private ChipGroup selectedDatesChipGroup;
    private Uri imageUri = null;
            private final String uploadUrl = "http://10.0.2.2/rentlify/submit_post.php";
//        private final String uploadUrl = "http://f29-preview.awardspace.net/rentlify.kesug.com/submit_post.php";
    private final List<Long> selectedDates = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            imageUri = result.getData().getData();
            Log.d("ImagePicker", "Selected Image URI: " + imageUri);

            previewImage.setImageURI(imageUri);
            imagePickerPlaceholder.setVisibility(View.GONE);
        }
    });

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.landlord_fragment_post, container, false);

        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        location = view.findViewById(R.id.location);
        type = view.findViewById(R.id.type);
        price = view.findViewById(R.id.price);

        bedroomsInput = view.findViewById(R.id.bedroomsInput);
        bathroomsInput = view.findViewById(R.id.bathroomsInput);
        areaInput = view.findViewById(R.id.areaInput);
        yearInput = view.findViewById(R.id.yearInput);

        availabilityDates = view.findViewById(R.id.availabilityDates);
        selectedDatesChipGroup = view.findViewById(R.id.selectedDatesChipGroup);

        postButton = view.findViewById(R.id.postButton);
        addImageButton = view.findViewById(R.id.selectImage);
        previewImage = view.findViewById(R.id.previewImage);
        imagePickerPlaceholder = view.findViewById(R.id.imagePickerPlaceholder);

        type.setInputType(InputType.TYPE_NULL);
        type.setKeyListener(null);

        setupDatePicker();

        type.setOnClickListener(v -> {
            String[] propertyTypes = {"Apartment", "House", "Villa", "Condo", "Penthouse"};

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Property Type").setItems(propertyTypes, (dialog, which) -> {
                type.setText(propertyTypes[which]);
            }).show();
        });

        addImageButton.setOnClickListener(v -> {
            // Hide status bar and navigation bar before launching ImagePicker
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = getActivity().getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                decorView.setSystemUiVisibility(uiOptions);
            }

            // Launch ImagePicker
            ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start();
        });

        postButton.setOnClickListener(v -> {
            if (validateFormInputs()) {
                if (imageUri != null) {
                    uploadProperty();
                } else {
                    Toast.makeText(requireContext(), "Please select an image.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean validateFormInputs() {
        // Check if landlord_id is available
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String landlordId = sharedPreferences.getString("userId", "default_id");
        String username = sharedPreferences.getString("username", "default_username");

        if (landlordId == null || landlordId.equals("default_id")) {
            Toast.makeText(requireContext(), "You must be logged in to post a property", Toast.LENGTH_LONG).show();
            return false;
        }

        // Validate price input
        String priceText = price.getText().toString().trim();
        if (priceText.isEmpty() || !priceText.matches("\\d+(\\.\\d{1,2})?")) {
            Toast.makeText(requireContext(), "Please enter a valid price.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check other required fields
        if (title.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a title.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (description.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (location.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a location.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (type.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please select a property type.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setupDatePicker() {
        availabilityDates.setInputType(InputType.TYPE_NULL);
        availabilityDates.setKeyListener(null);

        availabilityDates.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Available Dates").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (!selectedDates.contains(selection)) {
                    selectedDates.add(selection);
                    addDateChip(selection);
                    updateDateInputText();
                }
            });

            datePicker.show(getChildFragmentManager(), "DATE_PICKER");
        });
    }

    private void addDateChip(Long dateInMillis) {
        Date date = new Date(dateInMillis);
        Chip chip = new Chip(requireContext());
        chip.setText(dateFormat.format(date));
        chip.setCloseIconVisible(true);
        chip.setTag(dateInMillis);

        chip.setOnCloseIconClickListener(v -> {
            selectedDatesChipGroup.removeView(chip);
            selectedDates.remove(dateInMillis);
            updateDateInputText();
        });

        selectedDatesChipGroup.addView(chip);
    }

    private void updateDateInputText() {
        if (selectedDates.isEmpty()) {
            availabilityDates.setText("");
        } else {
            availabilityDates.setText(String.format("%d dates selected", selectedDates.size()));
        }
    }

    private String getFormattedDates() {
        StringBuilder dateStr = new StringBuilder();
        for (Long dateMillis : selectedDates) {
            if (dateStr.length() > 0) {
                dateStr.append(",");
            }
            dateStr.append(apiDateFormat.format(new Date(dateMillis)));
        }
        return dateStr.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            previewImage.setImageURI(imageUri);
            imagePickerPlaceholder.setVisibility(View.GONE);
        }
    }

    private void uploadProperty() {
        try {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String landlordId = sharedPreferences.getString("userId", "default_id");
            String username = sharedPreferences.getString("username", "default_username");

            InputStream iStream = requireActivity().getContentResolver().openInputStream(imageUri);
            byte[] inputData = getBytes(iStream);
            String encodedImage = Base64.encodeToString(inputData, Base64.DEFAULT);

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            StringRequest request = new StringRequest(Request.Method.POST, uploadUrl,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("success")) {
                                Toast.makeText(requireContext(), jsonResponse.getString("success"), Toast.LENGTH_LONG).show();
                                // Clear form fields or navigate back
                                clearFormFields();
                                // Optionally navigate back
                                // getActivity().getSupportFragmentManager().popBackStack();
                            } else if (jsonResponse.has("error")) {
                                Toast.makeText(requireContext(), "Error: " + jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(requireContext(), "Invalid response from server", Toast.LENGTH_LONG).show();
                            Log.e("JsonParse", "Error parsing JSON: " + response, e);
                        }
                    },
                    error -> {
                        Toast.makeText(requireContext(), "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("NetworkError", "Error in network request", error);
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("landlord_id", landlordId);
                    params.put("username", username);
                    params.put("title", title.getText().toString().trim());
                    params.put("description", description.getText().toString().trim());
                    params.put("location", location.getText().toString().trim());
                    params.put("property_type", type.getText().toString().trim());
                    params.put("price", price.getText().toString().trim());
                    params.put("bedroom", bedroomsInput.getText().toString().trim());
                    params.put("bathroom", bathroomsInput.getText().toString().trim());
                    params.put("area", areaInput.getText().toString().trim());
                    params.put("year_built", yearInput.getText().toString().trim());
                    params.put("available_dates", getFormattedDates());
                    params.put("image", encodedImage);
                    return params;
                }
            };

            // Set a longer timeout for the request
            request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                    30000,  // 30 seconds timeout
                    1,      // Max retries
                    1.0f    // Backoff multiplier
            ));

            queue.add(request);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error preparing upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("UploadError", "Error preparing upload", e);
        }
    }

    private void clearFormFields() {
        title.setText("");
        description.setText("");
        location.setText("");
        type.setText("");
        price.setText("");
        bedroomsInput.setText("");
        bathroomsInput.setText("");
        areaInput.setText("");
        yearInput.setText("");
        availabilityDates.setText("");
        selectedDatesChipGroup.removeAllViews();
        selectedDates.clear();
        imageUri = null;
        previewImage.setImageResource(android.R.color.transparent);
        imagePickerPlaceholder.setVisibility(View.VISIBLE);
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}