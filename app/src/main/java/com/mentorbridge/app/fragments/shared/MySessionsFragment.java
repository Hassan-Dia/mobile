package com.mentorbridge.app.fragments.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.mentorbridge.app.R;
import com.mentorbridge.app.adapters.SessionAdapter;
import com.mentorbridge.app.models.Session;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MySessionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TabLayout statusTabLayout;
    
    private ApiClient apiClient;
    private SessionManager sessionManager;
    private SessionAdapter adapter;
    private List<Session> sessionList = new ArrayList<>();
    private List<Session> allSessions = new ArrayList<>();
    private String currentFilter = "all";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_sessions, container, false);

        apiClient = ApiClient.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        initViews(view);
        setupTabLayout();
        loadSessions();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        statusTabLayout = view.findViewById(R.id.statusTabLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new SessionAdapter(requireContext(), sessionList, 
            sessionManager.isMentor(), 
            new SessionAdapter.OnSessionActionListener() {
                @Override
                public void onPayClick(Session session) {
                    paySession(session);
                }

                @Override
                public void onCompleteClick(Session session) {
                    completeSession(session);
                }

                @Override
                public void onFeedbackClick(Session session) {
                    showFeedbackDialog(session);
                }
            });
        
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(() -> loadSessions());
    }

    private void setupTabLayout() {
        statusTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        currentFilter = "all";
                        break;
                    case 1:
                        currentFilter = "pending";
                        break;
                    case 2:
                        currentFilter = "confirmed";
                        break;
                    case 3:
                        currentFilter = "completed";
                        break;
                    case 4:
                        currentFilter = "cancelled";
                        break;
                }
                filterSessions();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterSessions() {
        sessionList.clear();
        
        if (currentFilter.equals("all")) {
            sessionList.addAll(allSessions);
        } else {
            for (Session session : allSessions) {
                if (session.getStatus().equalsIgnoreCase(currentFilter) ||
                    (currentFilter.equals("confirmed") && session.getStatus().equalsIgnoreCase("upcoming"))) {
                    sessionList.add(session);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
    }

    private void loadSessions() {
        showLoading(true);

        apiClient.getSessions(sessionManager.getUserId(), sessionManager.getRole(),
            new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray data = response.getJSONArray("data");
                            allSessions.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Session session = new Session();
                                session.setId(obj.getInt("id"));
                                session.setAvailabilityId(obj.optInt("availability_id", 0));
                                session.setSource(obj.optString("source", "sessions"));
                                session.setMentorId(obj.optInt("mentor_id", 0));
                                session.setScheduledAt(obj.getString("scheduled_at"));
                                session.setAmount(obj.getDouble("amount"));
                                session.setStatus(obj.getString("status"));
                                session.setPaymentStatus(obj.getString("payment_status"));
                                session.setMentorName(obj.getString("mentor_name"));
                                session.setMenteeName(obj.getString("mentee_name"));
                                session.setHasFeedback(obj.optBoolean("has_feedback", false));
                                allSessions.add(session);
                            }
                            filterSessions();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error loading sessions", Toast.LENGTH_SHORT).show();
                    }
                    showLoading(false);
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void paySession(Session session) {
        try {
            JSONObject params = new JSONObject();
            
            // For pending bookings from availability table, send availability_id
            if ("availability".equals(session.getSource()) && session.getAvailabilityId() > 0) {
                params.put("availability_id", session.getAvailabilityId());
                params.put("session_id", session.getId());
            } else {
                // For sessions already in sessions table
                params.put("session_id", session.getId());
            }

            apiClient.paySession(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(requireContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                            loadSessions();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), "Payment failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void completeSession(Session session) {
        try {
            JSONObject params = new JSONObject();
            params.put("session_id", session.getId());
            params.put("mentor_user_id", sessionManager.getUserId());

            apiClient.completeSession(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(requireContext(), "Session completed", Toast.LENGTH_SHORT).show();
                            loadSessions();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), "Error completing session", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoading(boolean show) {
        swipeRefresh.setRefreshing(false);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showFeedbackDialog(Session session) {
        // Create dialog layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(
                android.R.layout.simple_list_item_1, null);
        
        // Create custom layout programmatically
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);
        
        // Title
        TextView titleText = new TextView(requireContext());
        titleText.setText("Rate Your Session");
        titleText.setTextSize(18);
        titleText.setPadding(0, 0, 0, 20);
        layout.addView(titleText);
        
        // Mentor name
        TextView mentorText = new TextView(requireContext());
        mentorText.setText("Mentor: " + session.getMentorName());
        mentorText.setTextSize(14);
        mentorText.setPadding(0, 0, 0, 20);
        layout.addView(mentorText);
        
        // Rating bar
        TextView ratingLabel = new TextView(requireContext());
        ratingLabel.setText("Rating:");
        ratingLabel.setTextSize(14);
        layout.addView(ratingLabel);
        
        RatingBar ratingBar = new RatingBar(requireContext(), null, android.R.attr.ratingBarStyle);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(5.0f);
        ratingBar.setIsIndicator(false);
        LinearLayout.LayoutParams ratingParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ratingParams.setMargins(0, 10, 0, 20);
        ratingBar.setLayoutParams(ratingParams);
        layout.addView(ratingBar);
        
        // Review text
        TextView reviewLabel = new TextView(requireContext());
        reviewLabel.setText("Your Review:");
        reviewLabel.setTextSize(14);
        layout.addView(reviewLabel);
        
        EditText reviewInput = new EditText(requireContext());
        reviewInput.setHint("Share your experience with this mentor...");
        reviewInput.setMinLines(3);
        reviewInput.setMaxLines(5);
        reviewInput.setPadding(20, 20, 20, 20);
        layout.addView(reviewInput);
        
        // Show dialog
        new AlertDialog.Builder(requireContext())
                .setView(layout)
                .setPositiveButton("Submit", (dialog, which) -> {
                    float rating = ratingBar.getRating();
                    String review = reviewInput.getText().toString().trim();
                    
                    if (rating == 0) {
                        Toast.makeText(requireContext(), "Please provide a rating", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    submitFeedback(session, rating, review);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void submitFeedback(Session session, float rating, String review) {
        progressBar.setVisibility(View.VISIBLE);
        
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", sessionManager.getUserId());
            params.put("mentor_id", session.getMentorId());
            params.put("rating", (int)rating);
            params.put("review", review);
            
            apiClient.submitFeedback(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(requireContext(), 
                                "Thank you for your feedback! Rated " + (int)rating + " stars", 
                                Toast.LENGTH_LONG).show();
                            loadSessions(); // Reload to update UI
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), "Error submitting feedback", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }
}
