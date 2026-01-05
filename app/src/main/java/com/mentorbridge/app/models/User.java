package com.mentorbridge.app.models;

public class User {
    private int userId;
    private String email;
    private String role;
    private Profile profile;

    public User() {}

    public User(int userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public boolean isMentor() {
        return "mentor".equals(role);
    }

    public boolean isMentee() {
        return "mentee".equals(role);
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }
}
