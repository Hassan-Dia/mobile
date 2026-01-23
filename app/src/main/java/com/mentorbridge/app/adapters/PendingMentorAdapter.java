package com.mentorbridge.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.mentorbridge.app.R;
import com.mentorbridge.app.models.Mentor;
import com.mentorbridge.app.utils.Utils;

import java.util.List;

public class PendingMentorAdapter extends RecyclerView.Adapter<PendingMentorAdapter.ViewHolder> {

    private Context context;
    private List<Mentor> mentors;
    private OnMentorActionListener listener;

    public interface OnMentorActionListener {
        void onApproveClick(Mentor mentor);
        void onRejectClick(Mentor mentor);
    }

    public PendingMentorAdapter(Context context, List<Mentor> mentors, OnMentorActionListener listener) {
        this.context = context;
        this.mentors = mentors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_mentor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mentor mentor = mentors.get(position);
        
        holder.txtName.setText(mentor.getFullName());
        holder.txtBio.setText(mentor.getBio());
        holder.txtSkills.setText("Skills: " + mentor.getSkills());
        holder.txtRate.setText(Utils.formatPrice(mentor.getHourlyRate()) + "/hr");
        
        holder.btnApprove.setOnClickListener(v -> listener.onApproveClick(mentor));
        holder.btnReject.setOnClickListener(v -> listener.onRejectClick(mentor));
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView txtName, txtBio, txtSkills, txtRate;
        Button btnApprove, btnReject;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txtName = itemView.findViewById(R.id.txtName);
            txtBio = itemView.findViewById(R.id.txtBio);
            txtSkills = itemView.findViewById(R.id.txtSkills);
            txtRate = itemView.findViewById(R.id.txtRate);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
