package com.mentorbridge.app.fragments.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mentorbridge.app.R;
import com.mentorbridge.app.network.VolleySingleton;
import com.mentorbridge.app.utils.SessionManager;
import com.mentorbridge.app.utils.Utils;

import org.json.JSONObject;

/**
 * Admin Dashboard Fragment
 * Displays real-time statistics from the database
 */
public class AdminDashboardFragment extends Fragment {

    private static final String TAG = "AdminDashboard";
    private static final String API_URL = "http://10.0.2.2/mentorbridge/api/admin_stats.php";
    
    // UI Components
    private TextView txtWelcome;
    private TextView totalUsers, totalMentors, totalMentees, totalSessions;
    private TextView pendingApprovals, totalRevenue;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        
        sessionManager = new SessionManager(requireContext());
        handler = new Handler(Looper.getMainLooper());
        
        initializeViews(view);
        loadStatistics();
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - refreshing statistics");
        loadStatistics();
    }
    
    private void initializeViews(View view) {
        txtWelcome = view.findViewById(R.id.txtWelcome);
        totalUsers = view.findViewById(R.id.totalUsers);
        totalMentors = view.findViewById(R.id.totalMentors);
        totalMentees = view.findViewById(R.id.totalMentees);
        totalSessions = view.findViewById(R.id.totalSessions);
        pendingApprovals = view.findViewById(R.id.pendingApprovals);
        totalRevenue = view.findViewById(R.id.totalRevenue);
        progressBar = view.findViewById(R.id.progressBar);
        
        String welcomeText = "Admin Dashboard";
        if (sessionManager != null && sessionManager.getFullName() != null) {
            welcomeText += " - " + sessionManager.getFullName();
        }
        txtWelcome.setText(welcomeText);
        
        // Set initial loading state
        setLoadingState(true);
    }
    
    /**
     * Public method to refresh statistics from external calls
     */
    public void refreshStats() {
        Log.d(TAG, "refreshStats called");
        loadStatistics();
    }
    
    /**
     * Load statistics from the database via API
     */
    private void loadStatistics() {
        if (!isAdded()) {
            Log.w(TAG, "Fragment not added, skipping load");
            return;
        }
        
        Log.d(TAG, "Loading statistics from: " + API_URL);
        setLoadingState(true);
        
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            API_URL,
            null,
            response -> {
                if (!isAdded()) return;
                handleSuccessResponse(response);
            },
            error -> {
                if (!isAdded()) return;
                handleErrorResponse(error);
            }
        );
        
        // Add request to queue
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }
    
    /**
     * Handle successful API response
     */
    private void handleSuccessResponse(JSONObject response) {
        try {
            Log.d(TAG, "Response received: " + response.toString());
            
            if (!response.has("success") || !response.getBoolean("success")) {
                String message = response.optString("message", "Unknown error");
                Log.e(TAG, "API returned error: " + message);
                showError("Failed to load statistics: " + message);
                return;
            }
            
            if (!response.has("data")) {
                Log.e(TAG, "Response missing 'data' field");
                showError("Invalid response format");
                return;
            }
            
            JSONObject data = response.getJSONObject("data");
            
            // Extract values with defaults
            int users = data.optInt("total_users", 0);
            int mentors = data.optInt("total_mentors", 0);
            int mentees = data.optInt("total_mentees", 0);
            int sessions = data.optInt("total_sessions", 0);
            int pending = data.optInt("pending_approvals", 0);
            double revenue = data.optDouble("total_revenue", 0.0);
            
            Log.d(TAG, String.format("Stats - Users: %d, Mentors: %d, Mentees: %d, Sessions: %d, Pending: %d, Revenue: %.2f",
                users, mentors, mentees, sessions, pending, revenue));
            
            // Update UI on main thread
            handler.post(() -> {
                if (!isAdded()) return;
                
                totalUsers.setText(String.valueOf(users));
                totalMentors.setText(String.valueOf(mentors));
                totalMentees.setText(String.valueOf(mentees));
                totalSessions.setText(String.valueOf(sessions));
                pendingApprovals.setText(String.valueOf(pending));
                totalRevenue.setText(Utils.formatPrice(revenue));
                
                setLoadingState(false);
                Log.d(TAG, "UI updated successfully");
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing response", e);
            showError("Error processing statistics: " + e.getMessage());
        }
    }
    
    /**
     * Handle API error response
     */
    private void handleErrorResponse(com.android.volley.VolleyError error) {
        String errorMessage = "Network error";
        
        if (error.networkResponse != null) {
            errorMessage = "HTTP " + error.networkResponse.statusCode;
            if (error.networkResponse.data != null) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    Log.e(TAG, "Error response: " + responseBody);
                } catch (Exception e) {
                    Log.e(TAG, "Error reading response body", e);
                }
            }
        } else if (error.getMessage() != null) {
            errorMessage = error.getMessage();
        }
        
        Log.e(TAG, "API Error: " + errorMessage, error);
        showError("Failed to load statistics. Please check your connection.");
    }
    
    /**
     * Show error message to user
     */
    private void showError(String message) {
        handler.post(() -> {
            if (!isAdded()) return;
            
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            setLoadingState(false);
            
            // Set default values on error
            totalUsers.setText("0");
            totalMentors.setText("0");
            totalMentees.setText("0");
            totalSessions.setText("0");
            pendingApprovals.setText("0");
            totalRevenue.setText(Utils.formatPrice(0.0));
        });
    }
    
    /**
     * Set loading state for UI
     */
    private void setLoadingState(boolean isLoading) {
        if (!isAdded()) return;
        
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler callbacks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
