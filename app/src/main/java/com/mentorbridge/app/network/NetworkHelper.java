package com.mentorbridge.app.network;

import android.content.Context;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for making network requests using Volley
 */
public class NetworkHelper {
    private static final String TAG = "NetworkHelper";
    private Context context;
    private String authToken = null;

    public NetworkHelper(Context context) {
        this.context = context;
    }

    /**
     * Set authorization token for authenticated requests
     */
    public void setAuthToken(String token) {
        this.authToken = token;
    }

    /**
     * Clear authorization token
     */
    public void clearAuthToken() {
        this.authToken = null;
    }

    /**
     * GET request returning JSONObject
     */
    public void getRequest(String url, final ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "GET Success: " + url);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = parseError(error);
                        Log.e(TAG, "GET Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeaders();
            }
        };

        addRequestToQueue(request);
    }

    /**
     * GET request returning JSONArray
     */
    public void getArrayRequest(String url, final ApiCallback.ArrayCallback callback) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "GET Array Success: " + url);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = parseError(error);
                        Log.e(TAG, "GET Array Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeaders();
            }
        };

        addRequestToQueue(request);
    }

    /**
     * POST request with JSONObject body
     */
    public void postRequest(String url, JSONObject requestBody, final ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "POST Success: " + url);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = parseError(error);
                        Log.e(TAG, "POST Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeaders();
            }
        };

        addRequestToQueue(request);
    }

    /**
     * PUT request with JSONObject body
     */
    public void putRequest(String url, JSONObject requestBody, final ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "PUT Success: " + url);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = parseError(error);
                        Log.e(TAG, "PUT Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeaders();
            }
        };

        addRequestToQueue(request);
    }

    /**
     * DELETE request
     */
    public void deleteRequest(String url, final ApiCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "DELETE Success: " + url);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = parseError(error);
                        Log.e(TAG, "DELETE Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeaders();
            }
        };

        addRequestToQueue(request);
    }

    /**
     * Add request to Volley queue with retry policy
     */
    private void addRequestToQueue(Request<?> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                ApiConfig.TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Get default headers including auth token
     */
    private Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(ApiConfig.HEADER_CONTENT_TYPE, ApiConfig.CONTENT_TYPE_JSON);
        
        if (authToken != null && !authToken.isEmpty()) {
            headers.put(ApiConfig.HEADER_AUTHORIZATION, "Bearer " + authToken);
        }
        
        return headers;
    }

    /**
     * Parse Volley error to user-friendly message
     */
    private String parseError(VolleyError error) {
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String responseData = new String(error.networkResponse.data);
            
            switch (statusCode) {
                case 400:
                    return "Bad request: " + responseData;
                case 401:
                    return "Unauthorized. Please login again.";
                case 403:
                    return "Forbidden. You don't have permission.";
                case 404:
                    return "Resource not found.";
                case 500:
                    return "Server error. Please try again later.";
                default:
                    return "Error " + statusCode + ": " + responseData;
            }
        } else if (error.getMessage() != null) {
            return error.getMessage();
        } else {
            return "Network error. Please check your connection.";
        }
    }
}
