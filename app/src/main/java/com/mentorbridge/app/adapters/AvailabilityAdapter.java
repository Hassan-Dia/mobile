package com.mentorbridge.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mentorbridge.app.R;
import com.mentorbridge.app.models.Availability;
import com.mentorbridge.app.utils.Utils;

import java.util.List;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.ViewHolder> {

    private Context context;
    private List<Availability> availabilityList;
    private boolean isReadOnly;

    public AvailabilityAdapter(Context context, List<Availability> availabilityList, boolean isReadOnly) {
        this.context = context;
        this.availabilityList = availabilityList;
        this.isReadOnly = isReadOnly;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_availability, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Availability availability = availabilityList.get(position);
        
        // Show day with date if available (e.g., "Wednesday, Feb 4")
        String dayText = availability.getDayOfWeek();
        if (availability.getSessionDate() != null && !availability.getSessionDate().isEmpty()) {
            try {
                String[] parts = availability.getSessionDate().split("-");
                if (parts.length == 3) {
                    String month = getMonthAbbr(Integer.parseInt(parts[1]));
                    dayText = availability.getDayOfWeek() + ", " + month + " " + Integer.parseInt(parts[2]);
                }
            } catch (Exception e) {
                // Keep day text as is
            }
        }
        holder.txtDay.setText(dayText);
        holder.txtTime.setText(availability.getTimeSlot());
        
        String status = availability.getSlotStatus() != null ? 
                        availability.getSlotStatus() : 
                        (availability.isAvailable() ? "available" : "disabled");
        
        holder.txtStatus.setText(Utils.getStatusText(status));
        
        int color = Utils.getStatusColor(status, context);
        holder.txtStatus.setTextColor(color);
        holder.viewIndicator.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return availabilityList.size();
    }

    private String getMonthAbbr(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return month >= 1 && month <= 12 ? months[month - 1] : "";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDay, txtTime, txtStatus;
        View viewIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            viewIndicator = itemView.findViewById(R.id.viewIndicator);
        }
    }
}
