package com.mentorbridge.app.fragments.mentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.mentorbridge.app.R;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;
import com.mentorbridge.app.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class MentorDashboardFragment extends Fragment {

    private TextView txtWelcome;
    private TextView totalSessions, avgRating, totalEarnings, approvalStatus;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentor_dashboard, container, false);

        sessionManager = new SessionManager(requireContext());
        apiClient = ApiClient.getInstance(requireContext());

        initViews(view);
        loadStats();

        return view;
    }

    private void initViews(View view) {
        txtWelcome = view.findViewById(R.id.txtWelcome);
        totalSessions = view.findViewById(R.id.totalSessions);
        avgRating = view.findViewById(R.id.avgRating);
        totalEarnings = view.findViewById(R.id.totalEarnings);
        approvalStatus = view.findViewById(R.id.approvalStatus);
        progressBar = view.findViewById(R.id.progressBar);

        txtWelcome.setText("Welcome, " + sessionManager.getFullName());
    }

    private void loadStats() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Load sessions for this mentor
        apiClient.getSessions(sessionManager.getUserId(), "mentor", new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        int sessionCount = data.length();
                        double earnings = 0;
                        
                        // Calculate total earnings from completed sessions
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject session = data.getJSONObject(i);
                            if ("completed".equals(session.getString("status"))) {
                                earnings += session.getDouble("amount");
                            }
                        }
                        
                        totalSessions.setText(String.valueOf(sessionCount));
                        totalEarnings.setText(Utils.formatPrice(earnings));
                        
                        // Load mentor details for rating
                        loadMentorDetails();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Error loading stats", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadMentorDetails() {
        apiClient.getMentorDetail(sessionManager.getUserId(), new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject mentor = response.getJSONObject("data");
                        double rating = mentor.getDouble("average_rating");
                        avgRating.setText(String.format("%.1f", rating));
                        approvalStatus.setText("Approved");
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
