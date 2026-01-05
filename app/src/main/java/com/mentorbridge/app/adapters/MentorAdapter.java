package com.mentorbridge.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mentorbridge.app.R;
import com.mentorbridge.app.models.Mentor;
import com.mentorbridge.app.utils.Utils;

import java.util.List;

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.ViewHolder> {

    private Context context;
    private List<Mentor> mentorList;
    private OnMentorClickListener listener;

    public interface OnMentorClickListener {
        void onMentorClick(Mentor mentor);
    }

    public MentorAdapter(Context context, List<Mentor> mentorList, OnMentorClickListener listener) {
        this.context = context;
        this.mentorList = mentorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mentor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mentor mentor = mentorList.get(position);
        
        holder.txtName.setText(mentor.getFullName());
        holder.txtCategories.setText(mentor.getCategories());
        holder.txtBio.setText(Utils.truncate(mentor.getBio(), 100));
        holder.txtRate.setText(Utils.formatPrice(mentor.getHourlyRate()) + "/hr");
        holder.ratingBar.setRating((float) mentor.getAverageRating());
        holder.txtReviews.setText("(" + mentor.getTotalReviews() + ")");

        holder.cardView.setOnClickListener(v -> listener.onMentorClick(mentor));
    }

    @Override
    public int getItemCount() {
        return mentorList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtName, txtCategories, txtBio, txtRate, txtReviews;
        RatingBar ratingBar;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txtName = itemView.findViewById(R.id.txtName);
            txtCategories = itemView.findViewById(R.id.txtCategories);
            txtBio = itemView.findViewById(R.id.txtBio);
            txtRate = itemView.findViewById(R.id.txtRate);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            txtReviews = itemView.findViewById(R.id.txtReviews);
        }
    }
}
