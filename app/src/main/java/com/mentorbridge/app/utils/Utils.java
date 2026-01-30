package com.mentorbridge.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    // Check internet connectivity
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Format date string
    public static String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    // Format date only
    public static String formatDateOnly(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    // Format date as "Day, Month DD" (e.g., "Monday, Feb 3")
    public static String formatDateWithDay(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // Try alternate format with time
            try {
                SimpleDateFormat altInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
                Date date = altInput.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException ex) {
                return dateString;
            }
        }
    }

    // Format time only
    public static String formatTimeOnly(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    // Format price
    public static String formatPrice(double price) {
        return String.format(Locale.getDefault(), "$%.2f", price);
    }

    // Format rating
    public static String formatRating(double rating) {
        return String.format(Locale.getDefault(), "%.1f", rating);
    }

    // Validate email
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Get status color
    public static int getStatusColor(String status, Context context) {
        switch (status) {
            case "available":
                return context.getResources().getColor(android.R.color.holo_green_dark);
            case "booked":
                return context.getResources().getColor(android.R.color.holo_orange_dark);
            case "waiting_feedback":
                return context.getResources().getColor(android.R.color.holo_blue_dark);
            case "disabled":
                return context.getResources().getColor(android.R.color.darker_gray);
            default:
                return context.getResources().getColor(android.R.color.black);
        }
    }

    // Get status text
    public static String getStatusText(String status) {
        switch (status) {
            case "available":
                return "Available";
            case "booked":
                return "Booked";
            case "waiting_feedback":
                return "Waiting for Feedback";
            case "disabled":
                return "Disabled";
            case "pending":
                return "Pending";
            case "confirmed":
                return "Confirmed";
            case "completed":
                return "Completed";
            case "cancelled":
                return "Cancelled";
            case "paid":
                return "Paid";
            default:
                return status;
        }
    }

    // Truncate text
    public static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}
