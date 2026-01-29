package com.mentorbridge.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mentorbridge.app.R;
import com.mentorbridge.app.adapters.AvailabilityAdapter;
import com.mentorbridge.app.adapters.ReviewAdapter;
import com.mentorbridge.app.models.Availability;
import com.mentorbridge.app.models.Mentor;
import com.mentorbridge.app.models.Review;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;
import com.mentorbridge.app.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MentorDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MENTOR_ID = "mentor_id";

    private int mentorId;
    private Mentor mentor;

    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private TextView txtName, txtCategories, txtBio, txtSkills, txtExperience, txtHourlyRate;
    private RatingBar ratingBar;
    private TextView txtRatingCount;
    private RecyclerView recyclerAvailability, recyclerReviews;
    private Button btnBookSession, btnContactPhone;
    
    private ApiClient apiClient;
    private SessionManager sessionManager;
    private AvailabilityAdapter availabilityAdapter;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_detail);

        mentorId = getIntent().getIntExtra(EXTRA_MENTOR_ID, 0);
        if (mentorId == 0) {
            Toast.makeText(this, "Invalid mentor", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiClient = ApiClient.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupToolbar();
        loadMentorDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload mentor details when returning from booking to show updated availability
        if (mentor != null) {
            loadMentorDetails();
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.contentLayout);
        
        txtName = findViewById(R.id.txtName);
        txtCategories = findViewById(R.id.txtCategories);
        txtBio = findViewById(R.id.txtBio);
        txtSkills = findViewById(R.id.txtSkills);
        txtExperience = findViewById(R.id.txtExperience);
        txtHourlyRate = findViewById(R.id.txtHourlyRate);
        ratingBar = findViewById(R.id.ratingBar);
        txtRatingCount = findViewById(R.id.txtRatingCount);
        
        recyclerAvailability = findViewById(R.id.recyclerAvailability);
        recyclerReviews = findViewById(R.id.recyclerReviews);
        
        btnBookSession = findViewById(R.id.btnBookSession);
        btnContactPhone = findViewById(R.id.btnContactPhone);

        recyclerAvailability.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));

        btnBookSession.setOnClickListener(v -> openBookSession());
        btnContactPhone.setOnClickListener(v -> contactPhone());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mentor Details");
        }
    }

    private void loadMentorDetails() {
        showLoading(true);

        apiClient.getMentorDetail(mentorId, new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");
                        mentor = parseMentorData(data);
                        displayMentorDetails();
                    } else {
                        Toast.makeText(MentorDetailActivity.this, 
                            response.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MentorDetailActivity.this, 
                        "Error parsing data", Toast.LENGTH_SHORT).show();
                }
                showLoading(false);
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(MentorDetailActivity.this, 
                    "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Mentor parseMentorData(JSONObject data) throws JSONException {
        Mentor m = new Mentor();
        m.setId(data.getInt("id"));
        m.setFullName(data.getString("full_name"));
        m.setBio(data.optString("bio", ""));
        m.setSkills(data.optString("skills", ""));
        m.setExperience(data.optString("experience", ""));
        m.setHourlyRate(data.getDouble("hourly_rate"));
        m.setAverageRating(data.getDouble("average_rating"));
        m.setTotalReviews(data.getInt("total_reviews"));
        m.setCategories(data.optString("categories", ""));
        m.setEmail(data.optString("email", ""));

        // Parse availability
        List<Availability> availList = new ArrayList<>();
        JSONArray availArray = data.getJSONArray("availability");
        for (int i = 0; i < availArray.length(); i++) {
            JSONObject obj = availArray.getJSONObject(i);
            Availability a = new Availability();
            a.setId(obj.getInt("id"));
            a.setDayOfWeek(obj.getString("day_of_week"));
            a.setSessionDate(obj.optString("session_date", ""));
            a.setTimeSlot(obj.getString("time_slot"));
            a.setAvailable(obj.getInt("is_available") == 1);
            availList.add(a);
        }
        m.setAvailability(availList);

        // Parse reviews
        List<Review> reviewList = new ArrayList<>();
        JSONArray reviewArray = data.getJSONArray("reviews");
        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject obj = reviewArray.getJSONObject(i);
            Review r = new Review();
            r.setRating(obj.getInt("rating"));
            r.setComment(obj.getString("comment"));
            r.setCreatedAt(obj.getString("created_at"));
            r.setMenteeName(obj.getString("mentee_name"));
            reviewList.add(r);
        }
        m.setReviews(reviewList);

        return m;
    }

    private void displayMentorDetails() {
        txtName.setText(mentor.getFullName());
        txtCategories.setText(mentor.getCategories());
        txtBio.setText(mentor.getBio());
        txtSkills.setText(mentor.getSkills());
        txtExperience.setText(mentor.getExperience());
        txtHourlyRate.setText(Utils.formatPrice(mentor.getHourlyRate()) + "/hour");
        ratingBar.setRating((float) mentor.getAverageRating());
        txtRatingCount.setText(String.format("(%d reviews)", mentor.getTotalReviews()));

        // Setup availability adapter
        availabilityAdapter = new AvailabilityAdapter(this, mentor.getAvailability(), true);
        recyclerAvailability.setAdapter(availabilityAdapter);

        // Setup reviews adapter
        reviewAdapter = new ReviewAdapter(this, mentor.getReviews());
        recyclerReviews.setAdapter(reviewAdapter);

        // Show book button only for mentees
        btnBookSession.setVisibility(sessionManager.isMentee() ? View.VISIBLE : View.GONE);
    }

    private void openBookSession() {
        Intent intent = new Intent(this, BookSessionActivity.class);
        intent.putExtra(BookSessionActivity.EXTRA_MENTOR_ID, mentor.getId());
        intent.putExtra(BookSessionActivity.EXTRA_MENTOR_NAME, mentor.getFullName());
        intent.putExtra(BookSessionActivity.EXTRA_HOURLY_RATE, mentor.getHourlyRate());
        
        // Pass availability as JSON string
        try {
            JSONArray availabilityArray = new JSONArray();
            for (Availability avail : mentor.getAvailability()) {
                JSONObject obj = new JSONObject();
                obj.put("id", avail.getId());
                obj.put("day_of_week", avail.getDayOfWeek());
                obj.put("session_date", avail.getSessionDate());
                obj.put("time_slot", avail.getTimeSlot());
                obj.put("is_available", avail.isAvailable() ? 1 : 0);
                availabilityArray.put(obj);
            }
            intent.putExtra(BookSessionActivity.EXTRA_AVAILABILITY, availabilityArray.toString());
        } catch (Exception e) {
            // Continue without availability
        }
        
        startActivity(intent);
    }

    private void contactEmail() {
        if (mentor.getEmail() != null && !mentor.getEmail().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + mentor.getEmail()));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Mentorship Inquiry");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Email not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactPhone() {
        // This would require phone number in mentor profile
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        contentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
