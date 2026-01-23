package com.mentorbridge.app.fragments.admin;

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

import org.json.JSONObject;

public class AdminDashboardFragment extends Fragment {

    private TextView txtWelcome;
    private TextView totalUsers, totalMentors, totalMentees, totalSessions;
    private TextView pendingApprovals, totalRevenue;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        sessionManager = new SessionManager(requireContext());
        apiClient = ApiClient.getInstance(requireContext());

        initViews(view);
        loadStats();

        return view;
    }

    private void initViews(View view) {
        txtWelcome = view.findViewById(R.id.txtWelcome);
        totalUsers = view.findViewById(R.id.totalUsers);
        totalMentors = view.findViewById(R.id.totalMentors);
        totalMentees = view.findViewById(R.id.totalMentees);
        totalSessions = view.findViewById(R.id.totalSessions);
        pendingApprovals = view.findViewById(R.id.pendingApprovals);
        totalRevenue = view.findViewById(R.id.totalRevenue);
        progressBar = view.findViewById(R.id.progressBar);

        txtWelcome.setText("Admin Dashboard - " + sessionManager.getFullName());
    }

    private void loadStats() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiClient.getAdminStats(new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");
                        
                        totalUsers.setText(String.valueOf(data.getInt("total_users")));
                        totalMentors.setText(String.valueOf(data.getInt("total_mentors")));
                        totalMentees.setText(String.valueOf(data.getInt("total_mentees")));
                        totalSessions.setText(String.valueOf(data.getInt("total_sessions")));
                        pendingApprovals.setText(String.valueOf(data.getInt("pending_approvals")));
                        totalRevenue.setText(Utils.formatPrice(data.getDouble("total_revenue")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Error loading stats", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
