package com.mentorbridge.app.fragments.mentee;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mentorbridge.app.R;
import com.mentorbridge.app.activities.MentorDetailActivity;
import com.mentorbridge.app.adapters.MentorAdapter;
import com.mentorbridge.app.models.Mentor;
import com.mentorbridge.app.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MentorListFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    
    private ApiClient apiClient;
    private MentorAdapter adapter;
    private List<Mentor> mentorList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentor_list, container, false);

        apiClient = ApiClient.getInstance(requireContext());
        
        initViews(view);
        setupRecyclerView();
        loadMentors("");

        return view;
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadMentors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        swipeRefresh.setOnRefreshListener(() -> loadMentors(searchEditText.getText().toString()));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MentorAdapter(requireContext(), mentorList, mentor -> {
            Intent intent = new Intent(requireContext(), MentorDetailActivity.class);
            intent.putExtra(MentorDetailActivity.EXTRA_MENTOR_ID, mentor.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadMentors(String search) {
        showLoading(true);
        
        String query = "action=list";
        if (!search.isEmpty()) {
            query += "&search=" + search;
        }

        apiClient.getMentors(query, new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        mentorList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Mentor mentor = new Mentor();
                            mentor.setId(obj.getInt("id"));
                            mentor.setFullName(obj.getString("full_name"));
                            mentor.setBio(obj.optString("bio", ""));
                            mentor.setSkills(obj.optString("skills", ""));
                            mentor.setHourlyRate(obj.getDouble("hourly_rate"));
                            mentor.setAverageRating(obj.getDouble("average_rating"));
                            mentor.setTotalReviews(obj.getInt("total_reviews"));
                            mentor.setCategories(obj.optString("categories", ""));
                            mentorList.add(mentor);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error loading mentors", Toast.LENGTH_SHORT).show();
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

    private void showLoading(boolean show) {
        swipeRefresh.setRefreshing(false);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
