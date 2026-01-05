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
        holder.txtDate.setText(Utils.formatDate(session.getScheduledAt()));
        holder.txtAmount.setText(Utils.formatPrice(session.getAmount()));
        holder.txtStatus.setText(Utils.getStatusText(session.getStatus()));
        
        // Show different buttons based on role and status
        holder.btnPay.setVisibility(View.GONE);
        holder.btnComplete.setVisibility(View.GONE);
        holder.btnFeedback.setVisibility(View.GONE);

        if (!isMentor && session.isPaymentPending()) {
            holder.btnPay.setVisibility(View.VISIBLE);
            holder.btnPay.setOnClickListener(v -> listener.onPayClick(session));
        }

        if (isMentor && session.isPending()) {
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.btnComplete.setOnClickListener(v -> listener.onCompleteClick(session));
        }

        if (!isMentor && session.isCompleted()) {
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
        Button btnPay, btnComplete, btnFeedback;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnPay = itemView.findViewById(R.id.btnPay);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnFeedback = itemView.findViewById(R.id.btnFeedback);
        }
    }
}
