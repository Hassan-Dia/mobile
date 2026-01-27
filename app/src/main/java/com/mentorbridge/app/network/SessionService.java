package com.mentorbridge.app.network;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service class for session-related API calls
 */
public class SessionService {
    private static final String TAG = "SessionService";
    private NetworkHelper networkHelper;

    public SessionService(Context context) {
        this.networkHelper = new NetworkHelper(context);
    }

    /**
     * Book a new session
     */
    public void bookSession(int mentorId, String date, String time, String topic, final SessionCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("mentorId", mentorId);
            requestBody.put("date", date);
            requestBody.put("time", time);
            requestBody.put("topic", topic);

            networkHelper.postRequest(ApiConfig.ENDPOINT_SESSIONS, requestBody, new ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSessionBooked(response);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error creating book session request", e);
            callback.onError("Error creating request");
        }
    }

    /**
     * Get all sessions for current user
     */
    public void getUserSessions(int userId, final SessionCallback callback) {
        String url = ApiConfig.getUserSessions(userId);
        
        networkHelper.getArrayRequest(url, new ApiCallback.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                callback.onSessionsLoaded(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Get upcoming sessions
     */
    public void getUpcomingSessions(int userId, final SessionCallback callback) {
        String url = ApiConfig.getUserSessions(userId) + "&status=upcoming";
        
        networkHelper.getArrayRequest(url, new ApiCallback.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                callback.onSessionsLoaded(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Cancel a session
     */
    public void cancelSession(int sessionId, final SessionCallback callback) {
        String url = ApiConfig.ENDPOINT_SESSIONS + "/" + sessionId;
        
        networkHelper.deleteRequest(url, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSessionCancelled(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Update session status
     */
    public void updateSessionStatus(int sessionId, String status, final SessionCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("status", status);

            String url = ApiConfig.ENDPOINT_SESSIONS + "/" + sessionId;
            
            networkHelper.putRequest(url, requestBody, new ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSessionUpdated(response);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error creating update session request", e);
            callback.onError("Error creating request");
        }
    }

    /**
     * Callback interface for session operations
     */
    public interface SessionCallback {
        default void onSessionBooked(JSONObject session) {}
        default void onSessionsLoaded(JSONArray sessions) {}
        default void onSessionCancelled(JSONObject response) {}
        default void onSessionUpdated(JSONObject session) {}
        void onError(String errorMessage);
    }
}
