package com.mentorbridge.app.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mentorbridge.app.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * API Client - All calls use ONLINE database via Volley
 * NO MOCK DATA, NO SHARED PREFERENCES, NO OFFLINE STORAGE
 */
public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "http://10.0.2.2/mentorbridge/api/";
    
    private static ApiClient instance;
    private Context context;

    private ApiClient(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    // ============ AUTH ENDPOINTS ============
    
    public void login(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "login.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError(response.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing login response", e);
                        listener.onError("Error parsing response");
                    }
                },
                error -> {
                    Log.e(TAG, "Login error", error);
                    String errorMsg = "Network error";
                    if (error.networkResponse != null) {
                        errorMsg = "Error " + error.networkResponse.statusCode;
                    }
                    listener.onError(errorMsg);
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void register(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "register.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            url,
            params,
            response -> listener.onSuccess(response),
            error -> {
                Log.e(TAG, "Registration error", error);
                if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                    listener.onError("Email already registered");
                } else {
                    listener.onError("Registration failed");
                }
            }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // ============ MENTOR ENDPOINTS ============
    
    public void getMentors(String queryParams, ApiResponseListener listener) {
        String url = BASE_URL + "get_mentors.php" + (queryParams != null && !queryParams.isEmpty() ? "?" + queryParams : "");
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error loading mentors", error);
                    listener.onError("Failed to load mentors");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getMentorDetail(int mentorId, ApiResponseListener listener) {
        String url = BASE_URL + "get_mentor.php?id=" + mentorId;
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error loading mentor details", error);
                    listener.onError("Failed to load mentor details");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
    
    public void getMentorByUserId(int userId, ApiResponseListener listener) {
        String url = BASE_URL + "get_mentor_by_user.php?user_id=" + userId;
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error loading mentor by user", error);
                    listener.onError("Failed to load mentor info");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void updateMentorProfile(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "setup_mentor_profile.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error updating mentor profile", error);
                    listener.onError("Failed to update profile");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // ============ CATEGORIES ============
    
    public void getCategories(ApiResponseListener listener) {
        String url = BASE_URL + "get_categories.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error loading categories", error);
                    listener.onError("Failed to load categories");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // ============ SESSIONS ============
    
    public void getSessions(int userId, String role, ApiResponseListener listener) {
        String url = BASE_URL + "get_sessions.php?user_id=" + userId + "&role=" + role;
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error loading sessions", error);
                    listener.onError("Failed to load sessions");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void bookSession(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "sessions.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error booking session", error);
                    listener.onError("Failed to book session");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void completeSession(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "complete_session.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error completing session", error);
                    listener.onError("Failed to complete session");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void paySession(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "pay_session.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error processing payment", error);
                    listener.onError("Payment failed");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // ============ AVAILABILITY ============
    
    public void getAvailability(int userId, ApiResponseListener listener) {
        String url = BASE_URL + "get_availability.php?user_id=" + userId;
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error loading availability", error);
                    listener.onError("Failed to load availability");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void addAvailability(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "add_availability.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error adding availability", error);
                    listener.onError("Failed to add availability");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void deleteAvailability(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "delete_availability.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error deleting availability", error);
                    listener.onError("Failed to delete availability");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void toggleAvailability(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "toggle_availability.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error toggling availability", error);
                    listener.onError("Failed to toggle availability");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // ============ FEEDBACK ============
    
    public void submitFeedback(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "reviews.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error submitting feedback", error);
                    listener.onError("Failed to submit feedback");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
    
    public void hasFeedback(int sessionId, HasFeedbackListener listener) {
        String url = BASE_URL + "check_feedback.php?session_id=" + sessionId;
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        listener.onResult(response.getBoolean("has_feedback"));
                    } catch (JSONException e) {
                        listener.onResult(false);
                    }
                },
                error -> listener.onResult(false)
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // ============ ADMIN ============
    
    public void getAdminStats(ApiResponseListener listener) {
        String url = BASE_URL + "admin_stats.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error loading admin stats", error);
                    listener.onError("Failed to load admin statistics");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getPendingMentors(ApiResponseListener listener) {
        String url = BASE_URL + "get_pending_mentors.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> listener.onSuccess(response),
            error -> {
                Log.e(TAG, "Error loading pending mentors", error);
                listener.onError("Failed to load pending mentors");
            }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void approveMentor(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "approve_mentor.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            url,
            params,
            response -> listener.onSuccess(response),
            error -> {
                Log.e(TAG, "Error approving mentor", error);
                listener.onError("Failed to approve mentor");
            }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void rejectMentor(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "approve_mentor.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            url,
            params,
            response -> listener.onSuccess(response),
            error -> {
                Log.e(TAG, "Error rejecting mentor", error);
                listener.onError("Failed to reject mentor");
            }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // ============ MENTOR PROFILE SETUP ============
    
    public void setupMentorProfile(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "setup_mentor_profile.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            url,
            params,
            response -> listener.onSuccess(response),
            error -> {
                Log.e(TAG, "Error setting up mentor profile", error);
                listener.onError("Failed to submit profile");
            }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void checkMentorApproval(JSONObject params, ApiResponseListener listener) {
        String url = BASE_URL + "check_approval.php";
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> listener.onSuccess(response),
                error -> {
                    Log.e(TAG, "Error checking approval status", error);
                    listener.onError("Failed to check approval status");
                }
        );
        
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    // Response listener interfaces
    public interface ApiResponseListener {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
    
    public interface HasFeedbackListener {
        void onResult(boolean hasFeedback);
    }
}
