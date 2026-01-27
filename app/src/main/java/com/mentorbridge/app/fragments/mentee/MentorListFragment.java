package com.mentorbridge.app.fragments.mentee;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
    private Spinner categorySpinner;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyView;
    
    private ApiClient apiClient;
    private MentorAdapter adapter;
    private List<Mentor> mentorList = new ArrayList<>();
    private String selectedCategory = "";

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
        categorySpinner = view.findViewById(R.id.categorySpinner);
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        // Setup category spinner
        String[] categories = {"All Categories", "Java Development", "Web Development", "Mobile Development", "Data Science", "UI/UX Design"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = position == 0 ? "" : categories[position];
                loadMentors(searchEditText.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
        if (!selectedCategory.isEmpty()) {
            query += "&category=" + selectedCategory;
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
                        emptyView.setVisibility(mentorList.isEmpty() ? View.VISIBLE : View.GONE);
                        recyclerView.setVisibility(mentorList.isEmpty() ? View.GONE : View.VISIBLE);
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
