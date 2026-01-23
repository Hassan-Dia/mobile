package com.mentorbridge.app.fragments.mentor;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mentorbridge.app.R;
import com.mentorbridge.app.adapters.AvailabilityAdapter;
import com.mentorbridge.app.models.Availability;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AvailabilityFragment extends Fragment {

    private TextView txtWelcome, emptyView;
    private MaterialButton btnAddSlot;
    private RecyclerView availabilityRecyclerView;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private ApiClient apiClient;
    private AvailabilityAdapter adapter;
    private List<Availability> availabilityList = new ArrayList<>();
    
    private String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_availability, container, false);

        sessionManager = new SessionManager(requireContext());
        apiClient = ApiClient.getInstance(requireContext());

        initViews(view);
        loadAvailability();

        return view;
    }

    private void initViews(View view) {
        txtWelcome = view.findViewById(R.id.txtWelcome);
        btnAddSlot = view.findViewById(R.id.btnAddSlot);
        availabilityRecyclerView = view.findViewById(R.id.availabilityRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        txtWelcome.setText("Manage your availability, " + sessionManager.getFullName());

        adapter = new AvailabilityAdapter(requireContext(), availabilityList, false);
        availabilityRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        availabilityRecyclerView.setAdapter(adapter);

        btnAddSlot.setOnClickListener(v -> showAddSlotDialog());
    }

    private void showAddSlotDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_slot, null);
        
        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        TextView txtTimeSlot = dialogView.findViewById(R.id.txtTimeSlot);
        MaterialButton btnSelectTime = dialogView.findViewById(R.id.btnSelectTime);
        
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, daysOfWeek);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);
        
        final String[] selectedTime = {"09:00"};
        txtTimeSlot.setText(selectedTime[0]);
        
        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            
            TimePickerDialog timePicker = new TimePickerDialog(requireContext(), 
                (view, hourOfDay, minuteOfHour) -> {
                    selectedTime[0] = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    txtTimeSlot.setText(selectedTime[0]);
                }, hour, minute, true);
            timePicker.show();
        });
        
        builder.setView(dialogView)
            .setTitle("Add Time Slot")
            .setPositiveButton("Add", (dialog, which) -> {
                String day = daysOfWeek[spinnerDay.getSelectedItemPosition()];
                addTimeSlot(day, selectedTime[0]);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void addTimeSlot(String day, String timeSlot) {
        progressBar.setVisibility(View.VISIBLE);
        
        try {
            JSONObject params = new JSONObject();
            params.put("mentor_user_id", sessionManager.getUserId());
            params.put("day_of_week", day);
            params.put("time_slot", timeSlot);
            params.put("is_available", true);
            
            apiClient.addAvailability(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(requireContext(), "Time slot added", Toast.LENGTH_SHORT).show();
                            loadAvailability();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), "Error adding slot", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadAvailability() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiClient.getAvailability(sessionManager.getUserId(), new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        availabilityList.clear();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Availability availability = new Availability();
                            availability.setId(obj.getInt("id"));
                            availability.setDayOfWeek(obj.getString("day_of_week"));
                            availability.setTimeSlot(obj.getString("time_slot"));
                            availability.setAvailable(obj.getBoolean("is_available"));
                            availabilityList.add(availability);
                        }
                        
                        adapter.notifyDataSetChanged();
                        emptyView.setVisibility(availabilityList.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Error loading availability", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }
}
