package com.mentorbridge.app.network;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service class for mentor-related API calls
 */
public class MentorService {
    private static final String TAG = "MentorService";
    private NetworkHelper networkHelper;

    public MentorService(Context context) {
        this.networkHelper = new NetworkHelper(context);
    }

    /**
     * Get all mentors
     */
    public void getAllMentors(final MentorCallback callback) {
        networkHelper.getArrayRequest(ApiConfig.ENDPOINT_MENTORS, new ApiCallback.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                callback.onMentorsLoaded(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Get mentor by ID
     */
    public void getMentorById(int mentorId, final MentorCallback callback) {
        String url = ApiConfig.getMentorById(mentorId);
        
        networkHelper.getRequest(url, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onMentorLoaded(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Search mentors by expertise
     */
    public void searchMentorsByExpertise(String expertise, final MentorCallback callback) {
        String url = ApiConfig.ENDPOINT_MENTORS + "?expertise=" + expertise;
        
        networkHelper.getArrayRequest(url, new ApiCallback.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                callback.onMentorsLoaded(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Get mentor reviews
     */
    public void getMentorReviews(int mentorId, final MentorCallback callback) {
        String url = ApiConfig.getMentorReviews(mentorId);
        
        networkHelper.getArrayRequest(url, new ApiCallback.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                callback.onReviewsLoaded(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Submit review for mentor
     */
    public void submitReview(int mentorId, int rating, String comment, final MentorCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("mentorId", mentorId);
            requestBody.put("rating", rating);
            requestBody.put("comment", comment);

            networkHelper.postRequest(ApiConfig.ENDPOINT_REVIEWS, requestBody, new ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onReviewSubmitted(response);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error creating review request", e);
            callback.onError("Error creating request");
        }
    }

    /**
     * Callback interface for mentor operations
     */
    public interface MentorCallback {
        default void onMentorsLoaded(JSONArray mentors) {}
        default void onMentorLoaded(JSONObject mentor) {}
        default void onReviewsLoaded(JSONArray reviews) {}
        default void onReviewSubmitted(JSONObject review) {}
        void onError(String errorMessage);
    }
}
