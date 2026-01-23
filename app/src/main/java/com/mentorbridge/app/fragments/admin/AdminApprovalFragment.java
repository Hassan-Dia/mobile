package com.mentorbridge.app.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mentorbridge.app.R;
import com.mentorbridge.app.adapters.PendingMentorAdapter;
import com.mentorbridge.app.models.Mentor;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminApprovalFragment extends Fragment implements PendingMentorAdapter.OnMentorActionListener {

    private TextView txtWelcome, emptyView;
    private RecyclerView pendingMentorsRecyclerView;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private ApiClient apiClient;
    private PendingMentorAdapter adapter;
    private List<Mentor> pendingMentors = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_approval, container, false);

        sessionManager = new SessionManager(requireContext());
        apiClient = ApiClient.getInstance(requireContext());

        initViews(view);
        loadPendingMentors();

        return view;
    }

    private void initViews(View view) {
        txtWelcome = view.findViewById(R.id.txtWelcome);
        pendingMentorsRecyclerView = view.findViewById(R.id.pendingMentorsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        txtWelcome.setText("Pending Mentor Approvals");

        adapter = new PendingMentorAdapter(requireContext(), pendingMentors, this);
        pendingMentorsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pendingMentorsRecyclerView.setAdapter(adapter);
    }

    private void loadPendingMentors() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Get all mentors (in real app, filter by pending status)
        apiClient.getMentors("", new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        pendingMentors.clear();
                        
                        // In offline mode, show all mentors as "pending"
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Mentor mentor = new Mentor();
                            mentor.setId(obj.getInt("id"));
                            mentor.setFullName(obj.getString("full_name"));
                            mentor.setBio(obj.getString("bio"));
                            mentor.setSkills(obj.getString("skills"));
                            mentor.setHourlyRate(obj.getDouble("hourly_rate"));
                            mentor.setCategories(obj.optString("categories", ""));
                            pendingMentors.add(mentor);
                        }
                        
                        adapter.notifyDataSetChanged();
                        emptyView.setVisibility(pendingMentors.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Error loading mentors", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onApproveClick(Mentor mentor) {
        progressBar.setVisibility(View.VISIBLE);
        
        try {
            JSONObject params = new JSONObject();
            params.put("mentor_user_id", mentor.getId());
            params.put("action", "approve");
            
            apiClient.approveMentor(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(requireContext(), "Mentor approved", Toast.LENGTH_SHORT).show();
                            loadPendingMentors();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), "Error approving mentor", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRejectClick(Mentor mentor) {
        progressBar.setVisibility(View.VISIBLE);
        
        try {
            JSONObject params = new JSONObject();
            params.put("mentor_user_id", mentor.getId());
            params.put("action", "reject");
            
            apiClient.approveMentor(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(requireContext(), "Mentor rejected", Toast.LENGTH_SHORT).show();
                            loadPendingMentors();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), "Error rejecting mentor", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }
}
