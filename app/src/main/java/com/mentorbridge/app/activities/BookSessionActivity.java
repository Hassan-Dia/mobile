package com.mentorbridge.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mentorbridge.app.R;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;
import com.mentorbridge.app.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookSessionActivity extends AppCompatActivity {

    public static final String EXTRA_MENTOR_ID = "mentor_id";
    public static final String EXTRA_MENTOR_NAME = "mentor_name";
    public static final String EXTRA_HOURLY_RATE = "hourly_rate";
    public static final String EXTRA_AVAILABILITY = "availability";

    private int mentorId;
    private String mentorName;
    private double hourlyRate;
    private JSONArray availabilityData;

    private TextView txtMentorName, txtPrice, txtTotalPrice;
    private Spinner spinnerDay, spinnerTime;
    private Button btnBook;
    private ProgressBar progressBar;

    private ApiClient apiClient;
    private SessionManager sessionManager;
    
    private List<String> availableDays;
    private JSONObject availabilityByDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_session);

        mentorId = getIntent().getIntExtra(EXTRA_MENTOR_ID, 0);
        mentorName = getIntent().getStringExtra(EXTRA_MENTOR_NAME);
        hourlyRate = getIntent().getDoubleExtra(EXTRA_HOURLY_RATE, 0);
        String availabilityJson = getIntent().getStringExtra(EXTRA_AVAILABILITY);

        if (mentorId == 0) {
            Toast.makeText(this, "Invalid mentor", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            availabilityData = new JSONArray(availabilityJson != null ? availabilityJson : "[]");
            parseAvailability();
        } catch (Exception e) {
            availabilityData = new JSONArray();
            availableDays = new ArrayList<>();
            availabilityByDay = new JSONObject();
        }

        apiClient = ApiClient.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupToolbar();
        setupSpinners();
        calculatePrice();
    }
    
    private void parseAvailability() throws JSONException {
        availableDays = new ArrayList<>();
        availabilityByDay = new JSONObject();
        
        for (int i = 0; i < availabilityData.length(); i++) {
            JSONObject slot = availabilityData.getJSONObject(i);
            if (slot.getInt("is_available") == 1) {
                String day = slot.getString("day_of_week");
                String timeSlot = slot.getString("time_slot");
                
                if (!availableDays.contains(day)) {
                    availableDays.add(day);
                }
                
                if (!availabilityByDay.has(day)) {
                    availabilityByDay.put(day, new JSONArray());
                }
                availabilityByDay.getJSONArray(day).put(timeSlot);
            }
        }
    }

    private void initViews() {
        txtMentorName = findViewById(R.id.txtMentorName);
        txtPrice = findViewById(R.id.txtPrice);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerTime = findViewById(R.id.spinnerTime);
        btnBook = findViewById(R.id.btnBook);
        progressBar = findViewById(R.id.progressBar);

        txtMentorName.setText(mentorName);
        
        btnBook.setOnClickListener(v -> bookSession());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Book Session");
        }
    }

    private void setupSpinners() {
        // Only show days that have availability
        if (availableDays.isEmpty()) {
            // Fallback to all days if no availability data
            availableDays.add("Monday");
            availableDays.add("Tuesday");
            availableDays.add("Wednesday");
            availableDays.add("Thursday");
            availableDays.add("Friday");
        }

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, availableDays);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Update time slots when day changes
        spinnerDay.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateTimeSlots();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Initial time slots setup
        updateTimeSlots();
    }
    
    private void updateTimeSlots() {
        String selectedDay = spinnerDay.getSelectedItem().toString();
        List<String> times = new ArrayList<>();
        
        try {
            if (availabilityByDay.has(selectedDay)) {
                JSONArray dayTimes = availabilityByDay.getJSONArray(selectedDay);
                for (int i = 0; i < dayTimes.length(); i++) {
                    times.add(dayTimes.getString(i));
                }
            }
        } catch (Exception e) {
            // Fallback times if parsing fails
        }
        
        // Fallback to default times if none available
        if (times.isEmpty()) {
            times.add("09:00-10:00");
            times.add("10:00-11:00");
            times.add("14:00-15:00");
            times.add("15:00-16:00");
        }

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, times);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeAdapter);
    }

    private void calculatePrice() {
        // Fixed 1 hour sessions
        double menteePrice = hourlyRate * 1.20;
        txtPrice.setText("Mentor Rate: " + Utils.formatPrice(hourlyRate) + " per hour");
        txtTotalPrice.setText("Total (with 20% platform fee): " + Utils.formatPrice(menteePrice));
    }

    private void bookSession() {
        String selectedDay = spinnerDay.getSelectedItem().toString();
        String selectedTime = spinnerTime.getSelectedItem().toString();

        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        try {
            JSONObject params = new JSONObject();
            params.put("mentee_user_id", sessionManager.getUserId());
            params.put("mentor_id", mentorId);
            params.put("selected_day", selectedDay);
            params.put("selected_time", selectedTime);

            apiClient.bookSession(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    showLoading(false);
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(BookSessionActivity.this, 
                                "Session booked successfully! Please complete payment.", 
                                Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(BookSessionActivity.this, 
                                response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(BookSessionActivity.this, 
                            "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Toast.makeText(BookSessionActivity.this, 
                        "Booking failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            showLoading(false);
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnBook.setEnabled(!show);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
