package com.mentorbridge.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mentorbridge.app.R;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MentorWaitingApprovalActivity extends AppCompatActivity {

    private TextView tvStatusTitle, tvStatusMessage;
    private Button btnCheckStatus, btnLogout;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_waiting_approval);

        sessionManager = new SessionManager(this);
        apiClient = ApiClient.getInstance(this);

        initViews();
        setupListeners();
        checkApprovalStatus();
    }

    private void initViews() {
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        btnCheckStatus = findViewById(R.id.btnCheckStatus);
        btnLogout = findViewById(R.id.btnLogout);
        progressBar = findViewById(R.id.progressBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Account Status");
        }
    }

    private void setupListeners() {
        btnCheckStatus.setOnClickListener(v -> checkApprovalStatus());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void checkApprovalStatus() {
        showLoading(true);

        try {
            JSONObject params = new JSONObject();
            params.put("action", "check_mentor_approval");
            params.put("user_id", sessionManager.getUserId());

            apiClient.checkMentorApproval(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    showLoading(false);
                    try {
                        if (response.getBoolean("success")) {
                            String status = response.getString("approval_status");
                            sessionManager.setApprovalStatus(status);

                            switch (status) {
                                case "approved":
                                    // Navigate to main app
                                    Toast.makeText(MentorWaitingApprovalActivity.this, 
                                        "Your profile has been approved!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MentorWaitingApprovalActivity.this, 
                                        MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                    
                                case "rejected":
                                    // Show rejection message and redirect to profile setup
                                    tvStatusTitle.setText("Profile Rejected");
                                    tvStatusMessage.setText(
                                        "Your profile has been rejected by the administrator. " +
                                        "Please update your information and resubmit.");
                                    
                                    // Add button to go back to profile setup
                                    btnCheckStatus.setText("Update Profile");
                                    btnCheckStatus.setOnClickListener(v -> {
                                        sessionManager.setProfileComplete(false);
                                        Intent setupIntent = new Intent(
                                            MentorWaitingApprovalActivity.this, 
                                            MentorProfileSetupActivity.class);
                                        startActivity(setupIntent);
                                        finish();
                                    });
                                    break;
                                    
                                case "pending":
                                default:
                                    tvStatusTitle.setText("Waiting for Approval");
                                    tvStatusMessage.setText(
                                        "Your profile is currently under review by an administrator. " +
                                        "You will be notified once your profile is approved. " +
                                        "This usually takes 1-2 business days.");
                                    Toast.makeText(MentorWaitingApprovalActivity.this, 
                                        "Still pending approval", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(MentorWaitingApprovalActivity.this, message, 
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MentorWaitingApprovalActivity.this, 
                            "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Toast.makeText(MentorWaitingApprovalActivity.this, 
                        "Failed to check status: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            showLoading(false);
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(MentorWaitingApprovalActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCheckStatus.setEnabled(!show);
    }

    @Override
    public void onBackPressed() {
        // Prevent going back, only allow logout
        Toast.makeText(this, "Please wait for approval or logout", Toast.LENGTH_SHORT).show();
    }
}
