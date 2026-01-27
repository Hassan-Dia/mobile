package com.mentorbridge.app.network;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service class for authentication-related API calls
 */
public class AuthService {
    private static final String TAG = "AuthService";
    private NetworkHelper networkHelper;

    public AuthService(Context context) {
        this.networkHelper = new NetworkHelper(context);
    }

    /**
     * Login user
     */
    public void login(String email, String password, final AuthCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("password", password);

            networkHelper.postRequest(ApiConfig.ENDPOINT_LOGIN, requestBody, new ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        String token = response.optString("token", "");
                        String userId = response.optString("userId", "");
                        
                        if (!token.isEmpty()) {
                            // Save token for future requests
                            networkHelper.setAuthToken(token);
                        }
                        
                        callback.onLoginSuccess(token, userId, response);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing login response", e);
                        callback.onError("Error parsing response");
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error creating login request", e);
            callback.onError("Error creating request");
        }
    }

    /**
     * Register new user
     */
    public void register(String name, String email, String password, final AuthCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", name);
            requestBody.put("email", email);
            requestBody.put("password", password);

            networkHelper.postRequest(ApiConfig.ENDPOINT_REGISTER, requestBody, new ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        String token = response.optString("token", "");
                        String userId = response.optString("userId", "");
                        
                        if (!token.isEmpty()) {
                            networkHelper.setAuthToken(token);
                        }
                        
                        callback.onLoginSuccess(token, userId, response);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing register response", e);
                        callback.onError("Error parsing response");
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error creating register request", e);
            callback.onError("Error creating request");
        }
    }

    /**
     * Logout user
     */
    public void logout() {
        networkHelper.clearAuthToken();
    }

    /**
     * Callback interface for authentication operations
     */
    public interface AuthCallback {
        void onLoginSuccess(String token, String userId, JSONObject userData);
        void onError(String errorMessage);
    }
}
