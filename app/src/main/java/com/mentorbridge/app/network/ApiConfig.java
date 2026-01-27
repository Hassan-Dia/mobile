package com.mentorbridge.app.network;

/**
 * API Configuration class
 * Store all API endpoints and base URLs here
 */
public class ApiConfig {
    // XAMPP Local Server - For Android Emulator
    public static final String BASE_URL = "http://10.0.2.2/mentorbridge/api/";
    
    // XAMPP Local Server - For Real Device (Replace with your computer's IP)
    // public static final String BASE_URL = "http://192.168.1.100/mentorbridge/api/";
    
    // Production Server (when deploying)
    // public static final String BASE_URL = "https://your-domain.com/mentorbridge/api/";

    // API Endpoints - PHP Files
    public static final String ENDPOINT_LOGIN = BASE_URL + "login.php";
    public static final String ENDPOINT_REGISTER = BASE_URL + "register.php";
    public static final String ENDPOINT_MENTORS = BASE_URL + "get_mentors.php";
    public static final String ENDPOINT_SESSIONS = BASE_URL + "sessions.php";
    public static final String ENDPOINT_USERS = BASE_URL + "users.php";
    public static final String ENDPOINT_REVIEWS = BASE_URL + "reviews.php";
    public static final String ENDPOINT_ADMIN_STATS = BASE_URL + "admin_stats.php";

    // Request timeout (in milliseconds)
    public static final int TIMEOUT_MS = 15000; // 15 seconds

    // Request headers
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * Get mentor by ID endpoint
     */
    public static String getMentorById(int mentorId) {
        return BASE_URL + "get_mentor.php?id=" + mentorId;
    }

    /**
     * Get sessions for user endpoint
     */
    public static String getUserSessions(int userId) {
        return BASE_URL + "get_sessions.php?userId=" + userId;
    }

    /**
     * Get reviews for mentor endpoint
     */
    public static String getMentorReviews(int mentorId) {
        return BASE_URL + "get_reviews.php?mentorId=" + mentorId;
    }
}
