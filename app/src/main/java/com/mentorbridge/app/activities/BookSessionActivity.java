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
    private JSONObject availabilitySlots; // Maps "day|time" -> availability object
    
    private int selectedAvailabilityId = -1;

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
        availabilitySlots = new JSONObject();
        
        for (int i = 0; i < availabilityData.length(); i++) {
            JSONObject slot = availabilityData.getJSONObject(i);
            if (slot.getInt("is_available") == 1) {
                String dayOfWeek = slot.getString("day_of_week");
                String sessionDate = slot.optString("session_date", "");
                String timeSlot = slot.getString("time_slot");
                int availId = slot.getInt("id");
                
                // Format display as "Wednesday, Feb 4" instead of just "Wednesday"
                String displayDay = dayOfWeek;
                if (!sessionDate.isEmpty()) {
                    try {
                        String[] parts = sessionDate.split("-");
                        if (parts.length == 3) {
                            String month = getMonthAbbr(Integer.parseInt(parts[1]));
                            displayDay = dayOfWeek + ", " + month + " " + Integer.parseInt(parts[2]);
                        }
                    } catch (Exception e) {
                        // Keep day as is
                    }
                }
                
                if (!availableDays.contains(displayDay)) {
                    availableDays.add(displayDay);
                }
                
                if (!availabilityByDay.has(displayDay)) {
                    availabilityByDay.put(displayDay, new JSONArray());
                }
                availabilityByDay.getJSONArray(displayDay).put(timeSlot);
                
                // Store full slot object by "displayDay|time" key
                String slotKey = displayDay + "|" + timeSlot;
                availabilitySlots.put(slotKey, slot);
            }
        }
    }
    
    private String getMonthAbbr(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return month >= 1 && month <= 12 ? months[month - 1] : "";
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
        // Show only days that have actual availability
        if (availableDays.isEmpty()) {
            availableDays.add("No availability");
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
            // No times available
        }
        
        // Don't show fake slots - only show real availability
        if (times.isEmpty()) {
            times.add("No times available");
        }

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, times);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeAdapter);
        
        // Update selected availability ID when time changes
        spinnerTime.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedDay = spinnerDay.getSelectedItem().toString();
                String selectedTime = spinnerTime.getSelectedItem().toString();
                String slotKey = selectedDay + "|" + selectedTime;
                
                try {
                    if (availabilitySlots != null && availabilitySlots.has(slotKey)) {
                        JSONObject slot = availabilitySlots.getJSONObject(slotKey);
                        selectedAvailabilityId = slot.getInt("id");
                    } else {
                        selectedAvailabilityId = -1;
                    }
                } catch (JSONException e) {
                    selectedAvailabilityId = -1;
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedAvailabilityId = -1;
            }
        });
    }

    private void calculatePrice() {
        // Fixed 1 hour sessions
        double menteePrice = hourlyRate * 1.20;
        txtPrice.setText("Mentor Rate: " + Utils.formatPrice(hourlyRate) + " per hour");
        txtTotalPrice.setText("Total (with 20% platform fee): " + Utils.formatPrice(menteePrice));
    }

    private void bookSession() {
        if (selectedAvailabilityId == -1) {
            Toast.makeText(this, "Please select a valid time slot", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        try {
            JSONObject params = new JSONObject();
            params.put("user_id", sessionManager.getUserId());
            params.put("availability_id", selectedAvailabilityId);

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
