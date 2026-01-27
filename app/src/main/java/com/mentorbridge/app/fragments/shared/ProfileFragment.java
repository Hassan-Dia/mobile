package com.mentorbridge.app.fragments.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mentorbridge.app.R;
import com.mentorbridge.app.activities.MainActivity;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;
import com.mentorbridge.app.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    private TextView txtName, txtEmail, txtRole;
    private CardView menteeStatsCard;
    private TextView txtTotalSessions, txtTotalSpent;
    private CardView mentorStatsCard;
    private TextView txtMentorSessions, txtMentorRating, txtMentorEarnings;
    private ProgressBar progressBar;
    private Button btnLogout;
    private SessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());
        apiClient = ApiClient.getInstance(requireContext());

        initViews(view);
        displayProfile();
        
        // Load stats for mentees
        if (sessionManager.isMentee()) {
            loadMenteeStats();
        }
        
        // Load stats for mentors
        if (sessionManager.isMentor()) {
            loadMentorStats();
        }

        return view;
    }

    private void initViews(View view) {
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtRole = view.findViewById(R.id.txtRole);
        menteeStatsCard = view.findViewById(R.id.menteeStatsCard);
        txtTotalSessions = view.findViewById(R.id.txtTotalSessions);
        txtTotalSpent = view.findViewById(R.id.txtTotalSpent);
        mentorStatsCard = view.findViewById(R.id.mentorStatsCard);
        txtMentorSessions = view.findViewById(R.id.txtMentorSessions);
        txtMentorRating = view.findViewById(R.id.txtMentorRating);
        txtMentorEarnings = view.findViewById(R.id.txtMentorEarnings);
        progressBar = view.findViewById(R.id.progressBar);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showLogoutOption();
            }
        });
    }

    private void displayProfile() {
        txtName.setText(sessionManager.getFullName());
        txtEmail.setText(sessionManager.getEmail());
        
        String role = sessionManager.getRole();
        String displayRole = role.substring(0, 1).toUpperCase() + role.substring(1);
        txtRole.setText(displayRole);
    }

    private void loadMenteeStats() {
        menteeStatsCard.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        
        apiClient.getSessions(sessionManager.getUserId(), sessionManager.getRole(), 
                new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray sessions = response.getJSONArray("data");
                        int totalSessions = 0;
                        double totalSpent = 0.0;
                        
                        for (int i = 0; i < sessions.length(); i++) {
                            JSONObject session = sessions.getJSONObject(i);
                            String status = session.optString("status", "");
                            String paymentStatus = session.optString("payment_status", "");
                            
                            // Count completed or confirmed sessions
                            if ("completed".equals(status) || "confirmed".equals(status)) {
                                totalSessions++;
                            }
                            
                            // Sum up paid amounts
                            if ("paid".equals(paymentStatus)) {
                                totalSpent += session.optDouble("amount", 0.0);
                            }
                        }
                        
                        txtTotalSessions.setText(String.valueOf(totalSessions));
                        txtTotalSpent.setText(Utils.formatPrice(totalSpent));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    
    private void loadMentorStats() {
        mentorStatsCard.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        
        // Load sessions for mentor stats
        apiClient.getSessions(sessionManager.getUserId(), "mentor", 
                new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray sessions = response.getJSONArray("data");
                        int totalSessions = sessions.length();
                        double totalEarnings = 0.0;
                        
                        // Calculate earnings from completed sessions
                        for (int i = 0; i < sessions.length(); i++) {
                            JSONObject session = sessions.getJSONObject(i);
                            String status = session.optString("status", "");
                            if ("completed".equals(status)) {
                                totalEarnings += session.optDouble("amount", 0.0);
                            }
                        }
                        
                        txtMentorSessions.setText(String.valueOf(totalSessions));
                        txtMentorEarnings.setText(Utils.formatPrice(totalEarnings));
                        
                        // Load mentor rating
                        loadMentorRating();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    
    private void loadMentorRating() {
        apiClient.getMentorDetail(sessionManager.getUserId(), 
                new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject mentor = response.getJSONObject("data");
                        double rating = mentor.optDouble("average_rating", 0.0);
                        txtMentorRating.setText(String.format("%.1f", rating));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
