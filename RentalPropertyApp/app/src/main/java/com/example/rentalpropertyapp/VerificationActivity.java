package com.example.rentalpropertyapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = "VerificationActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    // URLs for the API endpoints
    private static final String URL_SUBMIT_VERIFICATION = "http://10.0.2.2/rentlify/submit_verification.php";
    private static final String URL_CHECK_STATUS = "http://10.0.2.2/rentlify/verification.php";

    // UI Components
    private TextView verificationMessage;
    private TextInputEditText editFullname, editBirthday, editEmail, editAddress, editPhone;
    private TextInputEditText editLocation, editFacebook, editInstagram, editTwitter, editLinkedin;
    private AutoCompleteTextView dropdownSex, dropdownPropertyType;
    private ImageView imageFacialPreview;
    private TextView textDocumentName;
    private Button btnTakePhoto, btnChoosePhoto, btnUploadDocument, btnSubmitVerification;
    private Handler statusCheckHandler;
    private Runnable statusCheckRunnable;
    // Data fields
    private String userId;
    private String username;
    private Bitmap facialBitmap;
    private Uri documentUri;
    private boolean facialVerificationDone = false;
    private boolean documentVerificationDone = false;

    // Activity result launchers
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> pickDocumentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Initialize UI components
        initializeUI();

        // Set up dropdown adapters
        setupDropdowns();

        // Get user ID from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("userid");
        username = intent.getStringExtra("username");

        // Initialize activity result launchers
        initializeActivityResultLaunchers();

        // Set up click listeners
        setupClickListeners();

        // Check verification status
        checkVerificationStatus();

        // Refresh status every 30 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkVerificationStatus();
            }
        }, 30000);
        statusCheckHandler = new Handler();
        statusCheckRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDestroyed()) {
                    checkVerificationStatus();
                    // Schedule next run
                    statusCheckHandler.postDelayed(this, 30000);
                }
            }
        };
        statusCheckHandler.postDelayed(statusCheckRunnable, 30000);
    }
    @Override
    protected void onDestroy() {
        // Remove callbacks when activity is destroyed
        if (statusCheckHandler != null) {
            statusCheckHandler.removeCallbacks(statusCheckRunnable);
        }
        super.onDestroy();
    }

    private void initializeUI() {
        verificationMessage = findViewById(R.id.verification_message);

        // Personal info fields
        editFullname = findViewById(R.id.edit_fullname);
        editBirthday = findViewById(R.id.edit_birthday);
        editEmail = findViewById(R.id.edit_email);
        editAddress = findViewById(R.id.edit_address);
        editPhone = findViewById(R.id.edit_phone);
        dropdownSex = findViewById(R.id.dropdown_sex);

        // Property info fields
        editLocation = findViewById(R.id.edit_location);
        dropdownPropertyType = findViewById(R.id.dropdown_property_type);

        // Social media fields
        editFacebook = findViewById(R.id.edit_facebook);
        editInstagram = findViewById(R.id.edit_instagram);
        editTwitter = findViewById(R.id.edit_twitter);
        editLinkedin = findViewById(R.id.edit_linkedin);

        // Document upload components
        imageFacialPreview = findViewById(R.id.image_facial_preview);
        textDocumentName = findViewById(R.id.text_document_name);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnChoosePhoto = findViewById(R.id.btn_choose_photo);
        btnUploadDocument = findViewById(R.id.btn_upload_document);
        btnSubmitVerification = findViewById(R.id.btn_submit_verification);
    }

    private void setupDropdowns() {
        // Set up sex dropdown
        String[] sexOptions = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, sexOptions);
        dropdownSex.setAdapter(sexAdapter);

        // Set up property type dropdown
        String[] propertyTypes = new String[]{"Apartment", "House", "Condo", "Dormitory", "Other"};
        ArrayAdapter<String> propertyAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, propertyTypes);
        dropdownPropertyType.setAdapter(propertyAdapter);
    }

    private void initializeActivityResultLaunchers() {
        // Take picture launcher
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        facialBitmap = (Bitmap) extras.get("data");
                        imageFacialPreview.setImageBitmap(facialBitmap);
                        imageFacialPreview.setVisibility(View.VISIBLE);
                        facialVerificationDone = true;
                    }
                });

        // Pick image launcher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            facialBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imageFacialPreview.setImageBitmap(facialBitmap);
                            imageFacialPreview.setVisibility(View.VISIBLE);
                            facialVerificationDone = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Pick document launcher
        pickDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        documentUri = result.getData().getData();
                        String fileName = getFileNameFromUri(documentUri);
                        textDocumentName.setText(fileName);
                        documentVerificationDone = true;
                    }
                });
    }

    private void setupClickListeners() {
        // Date picker for birthday field
        editBirthday.setOnClickListener(v -> showDatePickerDialog());

        // Photo buttons
        btnTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        btnChoosePhoto.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        // Document button
        btnUploadDocument.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openDocumentPicker();
            } else {
                requestStoragePermission();
            }
        });

        // Submit button
        btnSubmitVerification.setOnClickListener(v -> validateAndSubmit());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Format date as needed (e.g. YYYY-MM-DD)
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        editBirthday.setText(date);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION
        );
    }

    private boolean checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // For Android 12 and below
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_STORAGE_PERMISSION
            );
        } else {
            // For Android 12 and below
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION
            );
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // This is important - tell the user to retry the action now that we have permission
                Toast.makeText(this, "Storage permission granted, please try again", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission required to access files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(takePictureIntent);
    }

    private void openGallery() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickImageIntent);
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // All file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickDocumentLauncher.launch(Intent.createChooser(intent, "Select Document"));
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    protected void onPause() {
        // Pause status checks when activity is not visible
        if (statusCheckHandler != null) {
            statusCheckHandler.removeCallbacks(statusCheckRunnable);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume status checks when activity becomes visible again
        if (statusCheckHandler != null && statusCheckRunnable != null) {
            statusCheckHandler.removeCallbacks(statusCheckRunnable); // Remove any pending callbacks
            statusCheckHandler.postDelayed(statusCheckRunnable, 30000);
        }
    }
    private void checkVerificationStatus() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking verification status...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Make API request to check status
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHECK_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");

                            if (!error) {
                                String status = jsonObject.getString("status");
                                updateUIBasedOnStatus(status);

                                // Populate fields if there is existing data
                                if (jsonObject.has("data")) {
                                    populateFields(jsonObject.getJSONObject("data"));
                                }

                                // Always ensure form is enabled
                                enableForm(true);
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(VerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VerificationActivity.this,
                                    "Error parsing response: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(VerificationActivity.this,
                                "Network error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userId);
                return params;
            }
        };

        // Add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,  // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void updateUIBasedOnStatus(String status) {
        switch (status) {
            case "Not Verified":
                verificationMessage.setText("Please provide your information for verification");
                verificationMessage.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                enableForm(true);
                break;
            case "Pending":
                verificationMessage.setText("Your verification is pending approval");
                verificationMessage.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                enableForm(true);
                break;
            case "Verified":
                // Automatically redirect to LandlordActivity if verified
                Toast.makeText(this, "You are verified! Redirecting to landlord dashboard...", Toast.LENGTH_SHORT).show();
                redirectToLandlordActivity();
                break;
            case "Rejected":
                verificationMessage.setText("Your verification was rejected. Please update and submit again");
                verificationMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                enableForm(true);
                break;
            default:
                verificationMessage.setText("Unknown status. Please contact support");
                verificationMessage.setTextColor(getResources().getColor(android.R.color.darker_gray));
                enableForm(true);
                break;
        }
    }

    // Add this new method to handle the redirection
    private void redirectToLandlordActivity() {
        Intent intent = new Intent(VerificationActivity.this, LandlordActivity.class);
        // Pass necessary data to the LandlordActivity
        intent.putExtra("userid", userId);
        intent.putExtra("username", username);
        startActivity(intent);
        finish(); // Close this activity so the user can't go back with the back button
    }

    private void enableForm(boolean enable) {
        // Enable/disable all input fields
        editFullname.setEnabled(enable);
        editBirthday.setEnabled(enable);
        editEmail.setEnabled(enable);
        editAddress.setEnabled(enable);
        editPhone.setEnabled(enable);
        dropdownSex.setEnabled(enable);
        editLocation.setEnabled(enable);
        dropdownPropertyType.setEnabled(enable);
        editFacebook.setEnabled(enable);
        editInstagram.setEnabled(enable);
        editTwitter.setEnabled(enable);
        editLinkedin.setEnabled(enable);

        // Enable/disable buttons
        btnTakePhoto.setEnabled(enable);
        btnChoosePhoto.setEnabled(enable);
        btnUploadDocument.setEnabled(enable);
        btnSubmitVerification.setEnabled(enable);
    }

    private void populateFields(JSONObject data) {
        try {
            if (data.has("fullname")) editFullname.setText(data.getString("fullname"));
            if (data.has("sex")) dropdownSex.setText(data.getString("sex"), false);
            if (data.has("birthday")) editBirthday.setText(data.getString("birthday"));
            if (data.has("email")) editEmail.setText(data.getString("email"));
            if (data.has("address")) editAddress.setText(data.getString("address"));
            if (data.has("phone")) editPhone.setText(data.getString("phone"));
            if (data.has("location")) editLocation.setText(data.getString("location"));
            if (data.has("property_type")) dropdownPropertyType.setText(data.getString("property_type"), false);
            if (data.has("facebook")) editFacebook.setText(data.getString("facebook"));
            if (data.has("instagram")) editInstagram.setText(data.getString("instagram"));
            if (data.has("twitter")) editTwitter.setText(data.getString("twitter"));
            if (data.has("linkedin")) editLinkedin.setText(data.getString("linkedin"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void validateAndSubmit() {
        // Validate all required fields
        if (validateForm()) {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Submit Verification")
                    .setMessage("Are you sure you want to submit your verification details?")
                    .setPositiveButton("Submit", (dialog, which) -> {
                        submitVerification();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        // Validate required fields
        if (editFullname.getText().toString().trim().isEmpty()) {
            editFullname.setError("Full name is required");
            valid = false;
        }

        if (dropdownSex.getText().toString().trim().isEmpty()) {
            dropdownSex.setError("Sex is required");
            valid = false;
        }

        if (editBirthday.getText().toString().trim().isEmpty()) {
            editBirthday.setError("Date of birth is required");
            valid = false;
        }

        if (editEmail.getText().toString().trim().isEmpty()) {
            editEmail.setError("Email is required");
            valid = false;
        }

        if (editAddress.getText().toString().trim().isEmpty()) {
            editAddress.setError("Address is required");
            valid = false;
        }

        if (editPhone.getText().toString().trim().isEmpty()) {
            editPhone.setError("Phone number is required");
            valid = false;
        }

        if (editLocation.getText().toString().trim().isEmpty()) {
            editLocation.setError("Property location is required");
            valid = false;
        }

        if (dropdownPropertyType.getText().toString().trim().isEmpty()) {
            dropdownPropertyType.setError("Property type is required");
            valid = false;
        }

        // Check if facial verification is done
        if (!facialVerificationDone) {
            Toast.makeText(this, "Facial verification photo is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        // Check if document verification is done
        if (!documentVerificationDone) {
            Toast.makeText(this, "Verification document is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void submitVerification() {
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting verification...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Convert bitmap to base64 string
        String facialBase64 = "";
        if (facialBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            facialBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            facialBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        final String finalFacialBase64 = facialBase64;

        // Convert document to base64 string
        final String[] documentBase64 = {""};
        if (documentUri != null) {
            try {
                byte[] fileBytes = getBytesFromUri(documentUri);
                documentBase64[0] = Base64.encodeToString(fileBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error reading document file", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
        }

        // Make API request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBMIT_VERIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            // Log the raw response to see what's actually being returned
                            Log.d(TAG, "Raw response: " + response);

                            // Check if the response starts with HTML content
                            if (response.trim().startsWith("<!")) {
                                Toast.makeText(VerificationActivity.this,
                                        "Server returned HTML instead of JSON. Check server configuration.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            JSONObject jsonObject = new JSONObject(response);
                            // Rest of your code...
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(VerificationActivity.this,
                                    "Error parsing response: " + e.getMessage() + "\nFirst 100 chars: " +
                                            (response.length() > 100 ? response.substring(0, 100) : response),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(VerificationActivity.this,
                                "Network error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("userid", userId);
                params.put("username", username);
                params.put("fullname", editFullname.getText().toString().trim());
                params.put("sex", dropdownSex.getText().toString().trim());
                params.put("birthday", editBirthday.getText().toString().trim());
                params.put("email", editEmail.getText().toString().trim());
                params.put("address", editAddress.getText().toString().trim());
                params.put("phone", editPhone.getText().toString().trim());

                // Property info
                params.put("location", editLocation.getText().toString().trim());
                params.put("property_type", dropdownPropertyType.getText().toString().trim());

                // Social media (optional)
                params.put("facebook", editFacebook.getText().toString().trim());
                params.put("instagram", editInstagram.getText().toString().trim());
                params.put("twitter", editTwitter.getText().toString().trim());
                params.put("linkedin", editLinkedin.getText().toString().trim());

                // Image and document data
                params.put("facial_image", finalFacialBase64);
                params.put("document_file", documentBase64[0]);
                params.put("document_name", textDocumentName.getText().toString().trim());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,  // 60 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try (java.io.InputStream inputStream = getContentResolver().openInputStream(uri)) {
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        }
        return byteBuffer.toByteArray();
    }
}