    package com.example.rentalpropertyapp;

    import android.app.DatePickerDialog;
    import android.os.Bundle;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ProgressBar;
    import android.widget.Spinner;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;

    import com.android.volley.AuthFailureError;
    import com.android.volley.Request;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Locale;
    import java.util.Map;

    public class TenantEditProfileActivity extends AppCompatActivity {

        private EditText etFullName, etEmail, etPhone, etBirthday;
        private Spinner spinnerSex;
        private Button btnSave;
        private ProgressBar progressBar;
        private String userId;
    //    private static final String URL_USER_DETAILS = "http://f29-preview.awardspace.net/rentlify.kesug.com/tenant_get_user_details.php";
        private static final String URL_USER_DETAILS = "http://10.0.2.2/rentlify/tenant_get_user_details.php";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.tenant_activity_edit_profile);

            // Set up toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");

            // Initialize views
            etFullName = findViewById(R.id.et_full_name);
            etEmail = findViewById(R.id.et_email);
            etPhone = findViewById(R.id.et_phone);
            etBirthday = findViewById(R.id.et_birthday);
            spinnerSex = findViewById(R.id.spinner_sex);
            btnSave = findViewById(R.id.btn_save);
            progressBar = findViewById(R.id.progress_bar);

            // Get user ID from intent
            userId = getIntent().getStringExtra("userid");
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Set up sex spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.sex_options, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSex.setAdapter(adapter);

            // Set up birthday picker
            etBirthday.setOnClickListener(v -> showDatePicker());

            // Set up save button
            btnSave.setOnClickListener(v -> saveProfileDetails());

            // Load existing user details
            loadUserDetails();
        }

        private void showDatePicker() {
            final Calendar calendar = Calendar.getInstance();

            // Set initial date from existing value if available
            String currentDate = etBirthday.getText().toString();
            if (!currentDate.equals("Not provided") && !currentDate.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf.parse(currentDate);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        etBirthday.setText(sdf.format(calendar.getTime()));
                    }, year, month, day);

            // Set max date to today (no future dates allowed)
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }

        private void loadUserDetails() {
            progressBar.setVisibility(View.VISIBLE);

            StringRequest request = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                    response -> {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if ("1".equals(obj.getString("success"))) {
                                JSONObject user = obj.getJSONArray("user").getJSONObject(0);

                                // Populate fields with existing data
                                if (user.has("fullname") && !user.isNull("fullname"))
                                    etFullName.setText(user.getString("fullname"));

                                if (user.has("email") && !user.isNull("email"))
                                    etEmail.setText(user.getString("email"));

                                if (user.has("phone") && !user.isNull("phone"))
                                    etPhone.setText(user.getString("phone"));

                                // Set birthday if available
                                if (user.has("birthday") && !user.isNull("birthday"))
                                    etBirthday.setText(user.getString("birthday"));

                                // Set sex spinner selection
                                if (user.has("sex") && !user.isNull("sex")) {
                                    String sex = user.getString("sex");
                                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerSex.getAdapter();
                                    int position = 0;
                                    for (int i = 0; i < adapter.getCount(); i++) {
                                        if (adapter.getItem(i).toString().equalsIgnoreCase(sex)) {
                                            position = i;
                                            break;
                                        }
                                    }
                                    spinnerSex.setSelection(position);
                                }
                            } else {
                                Toast.makeText(TenantEditProfileActivity.this,
                                        obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(TenantEditProfileActivity.this,
                                    "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(TenantEditProfileActivity.this,
                                "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

            Volley.newRequestQueue(this).add(request);
        }

        private void saveProfileDetails() {
            // Validate inputs
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String birthday = etBirthday.getText().toString().trim();
            String sex = spinnerSex.getSelectedItem().toString();

            if (fullName.isEmpty()) {
                etFullName.setError("Full name is required");
                etFullName.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                etPhone.setError("Phone number is required");
                etPhone.requestFocus();
                return;
            }

            if (birthday.isEmpty()) {
                Toast.makeText(this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);

            StringRequest request = new StringRequest(Request.Method.POST, URL_USER_DETAILS,
                    response -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);

                        try {
                            JSONObject obj = new JSONObject(response);
                            if ("1".equals(obj.getString("success"))) {
                                Toast.makeText(TenantEditProfileActivity.this,
                                        "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                // Return result to caller
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(TenantEditProfileActivity.this,
                                        obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(TenantEditProfileActivity.this,
                                    "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(TenantEditProfileActivity.this,
                                "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return new HashMap<String, String>() {{
                        put("id", userId);
                        put("action", "update_profile");
                        put("fullname", fullName);
                        put("sex", sex);
                        put("birthday", birthday);
                        put("email", email);
                        put("phone", phone);
                    }};
                }
            };

            Volley.newRequestQueue(this).add(request);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }