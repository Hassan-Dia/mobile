package com.mentorbridge.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mentorbridge.app.models.Profile;
import com.mentorbridge.app.models.User;

public class SessionManager {
    private static final String PREF_NAME = "MentorBridgeSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PROFILE_ID = "profile_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());
        
        if (user.getProfile() != null) {
            editor.putInt(KEY_PROFILE_ID, user.getProfile().getId());
            editor.putString(KEY_FULL_NAME, user.getProfile().getFullName());
        }
        
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, 0);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    public int getProfileId() {
        return prefs.getInt(KEY_PROFILE_ID, 0);
    }

    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, "User");
    }

    public boolean isMentor() {
        return "mentor".equals(getRole());
    }

    public boolean isMentee() {
        return "mentee".equals(getRole());
    }

    public boolean isAdmin() {
        return "admin".equals(getRole());
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public void updateProfile(Profile profile) {
        if (profile != null) {
            editor.putInt(KEY_PROFILE_ID, profile.getId());
            editor.putString(KEY_FULL_NAME, profile.getFullName());
            editor.apply();
        }
    }
}
