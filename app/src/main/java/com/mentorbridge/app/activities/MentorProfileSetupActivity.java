package com.mentorbridge.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mentorbridge.app.R;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MentorProfileSetupActivity extends AppCompatActivity {

    private EditText etBio, etSkills, etExperience, etHourlyRate;
    private Spinner spinnerCategory;
    private Button btnSubmitProfile;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private ApiClient apiClient;
    
    private String[] categories = {"Java Development", "Web Development", "Mobile Development", "Data Science", "UI/UX Design"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_profile_setup);

        sessionManager = new SessionManager(this);
        apiClient = ApiClient.getInstance(this);

        initViews();
        setupListeners();
        
        // Check if redirected due to rejection
        boolean isRejected = getIntent().getBooleanExtra("is_rejected", false);
        
        if (isRejected) {
            // Post dialog to handler to ensure UI is ready
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    showRejectionDialog();
                }
            }, 500);
        }
    }

    private void showRejectionDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Profile Rejected")
            .setMessage("Your account was rejected by the administrator. Please update your profile information and resubmit for approval.")
            .setPositiveButton("OK", null)
            .setCancelable(true)
            .show();
    }

    private void initViews() {
        etBio = findViewById(R.id.etBio);
        etSkills = findViewById(R.id.etSkills);
        etExperience = findViewById(R.id.etExperience);
        etHourlyRate = findViewById(R.id.etHourlyRate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSubmitProfile = findViewById(R.id.btnSubmitProfile);
        progressBar = findViewById(R.id.progressBar);
        
        // Setup category spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Complete Your Profile");
        }
    }

    private void setupListeners() {
        btnSubmitProfile.setOnClickListener(v -> submitProfile());
    }

    private void submitProfile() {
        String bio = etBio.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();
        String experience = etExperience.getText().toString().trim();
        String hourlyRateStr = etHourlyRate.getText().toString().trim();
        String categories = spinnerCategory.getSelectedItem().toString();

        // Validation
        if (TextUtils.isEmpty(bio)) {
            etBio.setError("Bio is required");
            etBio.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(skills)) {
            etSkills.setError("Skills are required");
            etSkills.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(experience)) {
            etExperience.setError("Experience is required");
            etExperience.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(hourlyRateStr)) {
            etHourlyRate.setError("Hourly rate is required");
            etHourlyRate.requestFocus();
            return;
        }

        double hourlyRate;
        try {
            hourlyRate = Double.parseDouble(hourlyRateStr);
            if (hourlyRate <= 0) {
                etHourlyRate.setError("Hourly rate must be greater than 0");
                etHourlyRate.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etHourlyRate.setError("Invalid hourly rate");
            etHourlyRate.requestFocus();
            return;
        }

        showLoading(true);

        try {
            JSONObject params = new JSONObject();
            params.put("action", "setup_mentor_profile");
            params.put("user_id", sessionManager.getUserId());
            params.put("bio", bio);
            params.put("skills", skills);
            params.put("experience", experience);
            params.put("hourly_rate", hourlyRate);
            params.put("categories", categories);

            apiClient.setupMentorProfile(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    showLoading(false);
                    try {
                        if (response.getBoolean("success")) {
                            // Mark profile as complete
                            sessionManager.setProfileComplete(true);
                            sessionManager.setApprovalStatus("pending");
                            
                            Toast.makeText(MentorProfileSetupActivity.this, 
                                "Profile submitted for approval", Toast.LENGTH_SHORT).show();
                            
                            // Navigate to waiting for approval page
                            Intent intent = new Intent(MentorProfileSetupActivity.this, 
                                MentorWaitingApprovalActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(MentorProfileSetupActivity.this, message, 
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MentorProfileSetupActivity.this, 
                            "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Toast.makeText(MentorProfileSetupActivity.this, 
                        "Failed to submit profile: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            showLoading(false);
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSubmitProfile.setEnabled(!show);
    }

    @Override
    public void onBackPressed() {
        // Prevent going back without completing profile
        Toast.makeText(this, "Please complete your profile to continue", Toast.LENGTH_SHORT).show();
    }
}
