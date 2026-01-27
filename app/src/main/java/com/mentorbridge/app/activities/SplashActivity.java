package com.mentorbridge.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.mentorbridge.app.R;
import com.mentorbridge.app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                // Check if user is a mentor
                if (sessionManager.isMentor()) {
                    // Check if profile is complete
                    if (!sessionManager.isProfileComplete()) {
                        // Redirect to profile setup
                        intent = new Intent(SplashActivity.this, MentorProfileSetupActivity.class);
                    } else {
                        // Check approval status
                        String approvalStatus = sessionManager.getApprovalStatus();
                        if ("approved".equals(approvalStatus)) {
                            // Approved, go to main app
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        } else if ("rejected".equals(approvalStatus)) {
                            // Rejected, go back to profile setup
                            sessionManager.setProfileComplete(false);
                            intent = new Intent(SplashActivity.this, MentorProfileSetupActivity.class);
                        } else {
                            // Pending or unknown, show waiting page
                            intent = new Intent(SplashActivity.this, MentorWaitingApprovalActivity.class);
                        }
                    }
                } else {
                    // Mentee or admin, go straight to main app
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
