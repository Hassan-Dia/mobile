package com.mentorbridge.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mentorbridge.app.R;
import com.mentorbridge.app.models.Session;
import com.mentorbridge.app.utils.Utils;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private Context context;
    private List<Session> sessions;
    private OnSessionActionListener listener;
    private boolean isMentor;

    public interface OnSessionActionListener {
        void onPayClick(Session session);
        void onCompleteClick(Session session);
        void onFeedbackClick(Session session);
        void onCancelClick(Session session);
    }

    public SessionAdapter(Context context, List<Session> sessions, boolean isMentor, OnSessionActionListener listener) {
        this.context = context;
        this.sessions = sessions;
        this.isMentor = isMentor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Session session = sessions.get(position);
        
        String displayName = isMentor ? session.getMenteeName() : session.getMentorName();
        holder.txtName.setText(displayName);
        
        // Parse scheduled_at to extract date and time (format: "YYYY-MM-DD HH:MM:SS")
        String scheduledAt = session.getScheduledAt();
        String displayDate = "";
        String displayDateWithDay = "";
        String displayTime = "";
        
        if (scheduledAt != null && scheduledAt.contains(" ")) {
            String[] parts = scheduledAt.split(" ");
            displayDate = Utils.formatDate(parts[0]);
            displayDateWithDay = Utils.formatDateWithDay(parts[0]);
            displayTime = parts.length > 1 ? parts[1].substring(0, 5) : ""; // HH:MM
        }
        
        holder.txtDate.setText(displayDateWithDay);
        
        // Mentees pay base amount + 20% platform fee, mentors see only their earnings
        double displayAmount = isMentor ? session.getAmount() : session.getTotalAmount();
        holder.txtAmount.setText(Utils.formatPrice(displayAmount));
        holder.txtStatus.setText(Utils.getStatusText(session.getStatus()));
        
        // Populate detail fields
        holder.sessionDate.setText(displayDate);
        holder.sessionTime.setText(displayTime);
        holder.sessionDuration.setText(session.getDuration() + " min");
        holder.sessionCost.setText(Utils.formatPrice(displayAmount));
        
        // Show different buttons based on role and status
        holder.btnPay.setVisibility(View.GONE);
        holder.btnComplete.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnFeedback.setVisibility(View.GONE);

        if (!isMentor && session.isPaymentPending()) {
            holder.btnPay.setVisibility(View.VISIBLE);
            holder.btnPay.setOnClickListener(v -> listener.onPayClick(session));
        }

        // Mentors can cancel pending sessions only (not yet paid)
        if (isMentor && "pending".equals(session.getStatus())) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(session));
        }

        // Mentors can only complete sessions that are confirmed (paid)
        if (isMentor && "confirmed".equals(session.getStatus())) {
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.btnComplete.setOnClickListener(v -> listener.onCompleteClick(session));
        }

        if (!isMentor && session.isCompleted() && !session.hasFeedback()) {
            holder.btnFeedback.setVisibility(View.VISIBLE);
            holder.btnFeedback.setOnClickListener(v -> listener.onFeedbackClick(session));
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtName, txtDate, txtAmount, txtStatus;
        TextView sessionDate, sessionTime, sessionDuration, sessionCost;
        Button btnPay, btnComplete, btnCancel, btnFeedback;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            sessionDate = itemView.findViewById(R.id.sessionDate);
            sessionTime = itemView.findViewById(R.id.sessionTime);
            sessionDuration = itemView.findViewById(R.id.sessionDuration);
            sessionCost = itemView.findViewById(R.id.sessionCost);
            btnPay = itemView.findViewById(R.id.btnPay);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnFeedback = itemView.findViewById(R.id.btnFeedback);
        }
    }
}
