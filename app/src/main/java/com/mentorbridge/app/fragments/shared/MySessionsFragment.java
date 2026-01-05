package com.mentorbridge.app.fragments.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    
    private ApiClient apiClient;
    private SessionManager sessionManager;
    private SessionAdapter adapter;
    private List<Session> sessionList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_sessions, container, false);

        apiClient = ApiClient.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        initViews(view);
        loadSessions();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);

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
                    // Show feedback dialog
                    Toast.makeText(requireContext(), "Feedback feature", Toast.LENGTH_SHORT).show();
                }
            });
        
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(() -> loadSessions());
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
                            sessionList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Session session = new Session();
                                session.setId(obj.getInt("id"));
                                session.setScheduledAt(obj.getString("scheduled_at"));
                                session.setAmount(obj.getDouble("amount"));
                                session.setStatus(obj.getString("status"));
                                session.setPaymentStatus(obj.getString("payment_status"));
                                session.setMentorName(obj.getString("mentor_name"));
                                session.setMenteeName(obj.getString("mentee_name"));
                                sessionList.add(session);
                            }
                            adapter.notifyDataSetChanged();
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
            params.put("session_id", session.getId());

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
}
