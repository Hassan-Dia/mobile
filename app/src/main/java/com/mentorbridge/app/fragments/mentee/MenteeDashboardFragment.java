package com.mentorbridge.app.fragments.mentee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mentorbridge.app.R;
import com.mentorbridge.app.adapters.CategoryAdapter;
import com.mentorbridge.app.models.Category;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenteeDashboardFragment extends Fragment {

    private TextView txtWelcome;
    private RecyclerView recyclerCategories;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    
    private ApiClient apiClient;
    private SessionManager sessionManager;
    private CategoryAdapter categoryAdapter;
    private List<Category> categories = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentee_dashboard, container, false);

        apiClient = ApiClient.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        initViews(view);
        loadCategories();

        return view;
    }

    private void initViews(View view) {
        txtWelcome = view.findViewById(R.id.txtWelcome);
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);

        txtWelcome.setText("Welcome, " + sessionManager.getFullName());

        recyclerCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter = new CategoryAdapter(requireContext(), categories);
        recyclerCategories.setAdapter(categoryAdapter);

        swipeRefresh.setOnRefreshListener(() -> loadCategories());
    }

    private void loadCategories() {
        showLoading(true);

        apiClient.getCategories(new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        categories.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Category category = new Category();
                            category.setId(obj.getInt("id"));
                            category.setName(obj.getString("name"));
                            category.setDescription(obj.getString("description"));
                            category.setIcon(obj.getString("icon"));
                            category.setMentorCount(obj.getInt("mentor_count"));
                            categories.add(category);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showLoading(false);
            }

            @Override
            public void onError(String error) {
                showLoading(false);
            }
        });
    }

    private void showLoading(boolean show) {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
