package com.mentorbridge.app.models;

public class AdminStats {
    private int totalUsers;
    private int totalMentors;
    private int totalMentees;
    private int totalSessions;
    private int completedSessions;
    private int pendingMentors;
    private double totalRevenue;

    public AdminStats() {}

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalMentors() {
        return totalMentors;
    }

    public void setTotalMentors(int totalMentors) {
        this.totalMentors = totalMentors;
    }

    public int getTotalMentees() {
        return totalMentees;
    }

    public void setTotalMentees(int totalMentees) {
        this.totalMentees = totalMentees;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions;
    }

    public int getPendingMentors() {
        return pendingMentors;
    }

    public void setPendingMentors(int pendingMentors) {
        this.pendingMentors = pendingMentors;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
