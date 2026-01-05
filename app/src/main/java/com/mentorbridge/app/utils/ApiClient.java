package com.mentorbridge.app.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ApiClient {
    private static ApiClient instance;
    private RequestQueue requestQueue;
    private Context context;

    // IMPORTANT: Change this to your server URL
    // For local development, use your computer's IP address (not localhost)
    // Example: "http://192.168.1.100/mentorbridge-php-project-main/api/"
    // For production: "https://yourdomain.com/api/"
    private static final String BASE_URL = "http://10.0.2.2/mentorbridge-php-project-main/api/"; // Android Emulator localhost

    private ApiClient(Context context) {
        this.context = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(this.context);
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    // Auth endpoints
    public void login(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "auth.php";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    public void register(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "auth.php";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    // Mentor endpoints
    public void getMentors(String queryParams, ApiResponseListener listener) {
        String url = BASE_URL + "mentors.php?" + queryParams;
        makeRequest(Request.Method.GET, url, null, listener);
    }

    public void getMentorDetail(int mentorId, ApiResponseListener listener) {
        String url = BASE_URL + "mentors.php?action=detail&id=" + mentorId;
        makeRequest(Request.Method.GET, url, null, listener);
    }

    public void updateMentorProfile(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "mentors.php?action=update_profile";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    // Categories
    public void getCategories(ApiResponseListener listener) {
        String url = BASE_URL + "categories.php";
        makeRequest(Request.Method.GET, url, null, listener);
    }

    // Sessions
    public void getSessions(int userId, String role, ApiResponseListener listener) {
        String url = BASE_URL + "sessions.php?action=list&user_id=" + userId + "&role=" + role;
        makeRequest(Request.Method.GET, url, null, listener);
    }

    public void bookSession(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "sessions.php?action=book";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    public void completeSession(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "sessions.php?action=complete";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    public void paySession(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "sessions.php?action=pay";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    // Availability
    public void getAvailability(int userId, ApiResponseListener listener) {
        String url = BASE_URL + "availability.php?action=list&user_id=" + userId;
        makeRequest(Request.Method.GET, url, null, listener);
    }

    public void addAvailability(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "availability.php?action=add";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    public void deleteAvailability(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "availability.php?action=delete";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    public void toggleAvailability(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "availability.php?action=toggle";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    // Feedback
    public void submitFeedback(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "feedback.php";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    // Admin
    public void getAdminStats(ApiResponseListener listener) {
        String url = BASE_URL + "admin.php?action=stats";
        makeRequest(Request.Method.GET, url, null, listener);
    }

    public void getPendingMentors(ApiResponseListener listener) {
        String url = BASE_URL + "admin.php?action=pending_mentors";
        makeRequest(Request.Method.GET, url, null, listener);
    }

    public void approveMentor(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "admin.php?action=approve";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    public void rejectMentor(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "admin.php?action=reject";
        makeRequest(Request.Method.POST, url, params, listener);
    }

    // Generic request method
    private void makeRequest(int method, String url, JSONObject params, ApiResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                method,
                url,
                params,
                response -> {
                    if (listener != null) {
                        listener.onSuccess(response);
                    }
                },
                error -> {
                    if (listener != null) {
                        String errorMessage = "Network error";
                        if (error.networkResponse != null) {
                            errorMessage = "Error: " + error.networkResponse.statusCode;
                        } else if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }
                        listener.onError(errorMessage);
                    }
                }
        );

        requestQueue.add(request);
    }

    // Response listener interface
    public interface ApiResponseListener {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
}
