package com.mentorbridge.app.network;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Generic callback interface for API responses
 */
public interface ApiCallback {
    /**
     * Called when request is successful
     */
    void onSuccess(JSONObject response);

    /**
     * Called when request fails
     */
    void onError(String errorMessage);

    /**
     * Callback for JSON Array responses
     */
    interface ArrayCallback {
        void onSuccess(JSONArray response);
        void onError(String errorMessage);
    }

    /**
     * Callback for String responses
     */
    interface StringCallback {
        void onSuccess(String response);
        void onError(String errorMessage);
    }
}
