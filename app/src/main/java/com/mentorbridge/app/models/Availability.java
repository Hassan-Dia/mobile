package com.mentorbridge.app.models;

public class Availability {
    private int id;
    private String dayOfWeek;
    private String sessionDate;
    private String timeSlot;
    private boolean isAvailable;
    private int hasBooking;
    private int waitingFeedback;
    private String slotStatus;

    public Availability() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getHasBooking() {
        return hasBooking;
    }

    public void setHasBooking(int hasBooking) {
        this.hasBooking = hasBooking;
    }

    public int getWaitingFeedback() {
        return waitingFeedback;
    }

    public void setWaitingFeedback(int waitingFeedback) {
        this.waitingFeedback = waitingFeedback;
    }

    public String getSlotStatus() {
        return slotStatus;
    }

    public void setSlotStatus(String slotStatus) {
        this.slotStatus = slotStatus;
    }

    // Status constants
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_BOOKED = "booked";
    public static final String STATUS_WAITING_FEEDBACK = "waiting_feedback";
    public static final String STATUS_DISABLED = "disabled";
}
