package com.mentorbridge.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ✅ FULLY OFFLINE API CLIENT
 * No network, no server, no internet needed!
 * All data is stored and managed locally on the device.
 */
public class ApiClient {
    private static ApiClient instance;
    private Context context;
    private SharedPreferences prefs;
    private Handler handler;

    private ApiClient(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("MentorBridgeOffline", Context.MODE_PRIVATE);
        this.handler = new Handler(Looper.getMainLooper());
        initializeDemoData();
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    private void initializeDemoData() {
        // Initialize demo data on first run
        if (!prefs.getBoolean("data_initialized", false)) {
            prefs.edit().putBoolean("data_initialized", true).apply();
        }
    }

    // ============ AUTH ENDPOINTS ============
    
    public void login(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                String email = params.getString("email");
                String password = params.getString("password");
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Login successful");
                
                JSONObject data = new JSONObject();
                JSONObject profile = new JSONObject();
                
                // Check for default admin account
                if ("admin@mentorbridge.com".equals(email)) {
                    data.put("user_id", 100);
                    data.put("email", email);
                    data.put("role", "admin");
                    
                    profile.put("id", 100);
                    profile.put("full_name", "Admin User");
                    profile.put("bio", "Platform Administrator");
                    profile.put("phone", "+1234567890");
                }
                // Check for mentor account
                else if (email.contains("mentor") || "mentor".equals(email)) {
                    data.put("user_id", 2);
                    data.put("email", email);
                    data.put("role", "mentor");
                    
                    profile.put("id", 2);
                    profile.put("full_name", "Mentor User");
                    profile.put("bio", "Experienced mentor");
                    profile.put("phone", "+1234567890");
                }
                // Default to mentee
                else {
                    data.put("user_id", 1);
                    data.put("email", email);
                    data.put("role", "mentee");
                    
                    profile.put("id", 1);
                    profile.put("full_name", "Demo User");
                    profile.put("bio", "Mobile app enthusiast");
                    profile.put("phone", "+1234567890");
                }
                
                data.put("profile", profile);
                response.put("data", data);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Login failed: " + e.getMessage());
            }
        }, 500);
    }

    public void register(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                String email = params.getString("email");
                String fullName = params.getString("full_name");
                String role = params.getString("role");
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Registration successful");
                
                JSONObject data = new JSONObject();
                data.put("user_id", 1);
                data.put("email", email);
                data.put("role", role);
                
                JSONObject profile = new JSONObject();
                profile.put("id", 1);
                profile.put("full_name", fullName);
                data.put("profile", profile);
                
                response.put("data", data);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Registration failed: " + e.getMessage());
            }
        }, 500);
    }

    // ============ MENTOR ENDPOINTS ============
    
    public void getMentors(String queryParams, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONArray mentors = new JSONArray();
                
                // Demo mentors
                String[] names = {"Sarah Johnson", "Michael Chen", "Emily Rodriguez", "David Kim", "Jessica Taylor"};
                String[] categories = {"Java Development", "Web Development", "Mobile Development", "Data Science", "UI/UX Design"};
                String[] bios = {
                    "10+ years in enterprise Java development",
                    "Full-stack developer specializing in React and Node.js",
                    "Android & iOS expert with 50+ published apps",
                    "Machine Learning engineer at top tech company",
                    "Award-winning product designer"
                };
                String[] skills = {
                    "Java, Spring Boot, Microservices, SQL",
                    "React, Node.js, MongoDB, Express",
                    "Android, iOS, Flutter, React Native",
                    "Python, TensorFlow, PyTorch, Pandas",
                    "Figma, Adobe XD, Sketch, Prototyping"
                };
                double[] rates = {50.0, 45.0, 60.0, 75.0, 55.0};
                double[] ratings = {4.8, 4.9, 4.7, 4.6, 4.9};
                
                for (int i = 0; i < 5; i++) {
                    JSONObject mentor = new JSONObject();
                    mentor.put("id", i + 1);
                    mentor.put("full_name", names[i]);
                    mentor.put("bio", bios[i]);
                    mentor.put("skills", skills[i]);
                    mentor.put("categories", categories[i]);
                    mentor.put("hourly_rate", rates[i]);
                    mentor.put("average_rating", ratings[i]);
                    mentor.put("total_reviews", (i + 1) * 12);
                    mentors.put(mentor);
                }
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("data", mentors);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to load mentors: " + e.getMessage());
            }
        }, 300);
    }

    public void getMentorDetail(int mentorId, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject mentor = new JSONObject();
                mentor.put("id", mentorId);
                mentor.put("full_name", "Sarah Johnson");
                mentor.put("email", "sarah.j@example.com");
                mentor.put("bio", "Experienced Java developer with 10+ years in enterprise applications. Passionate about teaching and mentoring.");
                mentor.put("skills", "Java, Spring Boot, Microservices, SQL, Docker");
                mentor.put("experience", "10 years in enterprise development");
                mentor.put("hourly_rate", 50.0);
                mentor.put("phone", "+1234567890");
                mentor.put("categories", "Java Development");
                mentor.put("average_rating", 4.8);
                mentor.put("total_reviews", 47);
                
                // Availability slots
                JSONArray availability = new JSONArray();
                String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
                String[] times = {"09:00-10:00", "10:00-11:00", "14:00-15:00", "15:00-16:00"};
                
                for (int i = 0; i < 10; i++) {
                    JSONObject slot = new JSONObject();
                    slot.put("id", i + 1);
                    slot.put("day_of_week", days[i % 5]);
                    slot.put("time_slot", times[i % 4]);
                    slot.put("is_available", (i % 3 == 0) ? 0 : 1);
                    availability.put(slot);
                }
                mentor.put("availability", availability);
                
                // Reviews
                JSONArray reviews = new JSONArray();
                String[] reviewTexts = {
                    "Excellent mentor! Very patient and knowledgeable.",
                    "Helped me understand complex concepts easily.",
                    "Great session, learned a lot about Spring Boot."
                };
                
                for (int i = 0; i < 3; i++) {
                    JSONObject review = new JSONObject();
                    review.put("rating", (int)(4.5 + (i * 0.2)));
                    review.put("comment", reviewTexts[i]);
                    review.put("created_at", "2024-01-" + String.format("%02d", 15 + i));
                    review.put("mentee_name", "Student " + (i + 1));
                    reviews.put(review);
                }
                mentor.put("reviews", reviews);
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("data", mentor);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to load mentor details: " + e.getMessage());
            }
        }, 300);
    }

    public void updateMentorProfile(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Profile updated successfully");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to update profile: " + e.getMessage());
            }
        }, 400);
    }

    // ============ CATEGORIES ============
    
    public void getCategories(ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONArray categories = new JSONArray();
                
                String[] names = {
                    "Java Development", "Web Development", "Mobile Development",
                    "Data Science", "UI/UX Design", "DevOps", "Cloud Computing"
                };
                String[] descriptions = {
                    "Backend development with Java",
                    "Frontend and full-stack web",
                    "Android and iOS apps",
                    "ML and data analytics",
                    "User interface design",
                    "CI/CD and automation",
                    "AWS, Azure, Google Cloud"
                };
                
                for (int i = 0; i < names.length; i++) {
                    JSONObject category = new JSONObject();
                    category.put("id", i + 1);
                    category.put("name", names[i]);
                    category.put("description", descriptions[i]);
                    category.put("mentor_count", 5 + i * 2);
                    categories.put(category);
                }
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("data", categories);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to load categories: " + e.getMessage());
            }
        }, 200);
    }

    // ============ SESSIONS ============
    
    public void getSessions(int userId, String role, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                // Load sessions from SharedPreferences or use demo data
                String sessionsJson = prefs.getString("sessions_data", null);
                JSONArray sessions;
                
                if (sessionsJson != null) {
                    sessions = new JSONArray(sessionsJson);
                } else {
                    // Initialize with demo sessions
                    sessions = getDemoSessions();
                    prefs.edit().putString("sessions_data", sessions.toString()).apply();
                }
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("data", sessions);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to load sessions: " + e.getMessage());
            }
        }, 300);
    }
    
    private JSONArray getDemoSessions() throws Exception {
        JSONArray sessions = new JSONArray();
        
        String[] statuses = {"confirmed", "completed", "confirmed", "pending", "completed"};
        String[] paymentStatuses = {"paid", "paid", "pending", "pending", "paid"};
        String[] mentorNames = {"Sarah Johnson", "Michael Chen", "Emily Rodriguez", "David Kim", "Jessica Taylor"};
        
        for (int i = 0; i < 5; i++) {
            JSONObject session = new JSONObject();
            session.put("id", i + 1);
            session.put("mentor_name", mentorNames[i]);
            session.put("mentee_name", "Demo User");
            session.put("scheduled_at", "2024-02-" + String.format("%02d", 10 + i) + " " + (10 + i) + ":00:00");
            session.put("duration", 60);
            session.put("status", statuses[i]);
            session.put("payment_status", paymentStatuses[i]);
            session.put("amount", 50.0 + (i * 5));
            sessions.put(session);
        }
        
        return sessions;
    }

    public void bookSession(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                // Save booking locally
                String bookings = prefs.getString("bookings", "[]");
                JSONArray bookingsArray = new JSONArray(bookings);
                
                JSONObject newBooking = new JSONObject();
                newBooking.put("id", bookingsArray.length() + 1);
                newBooking.put("mentor_id", params.getInt("mentor_id"));
                newBooking.put("date", params.getString("date"));
                newBooking.put("time", params.getString("time"));
                newBooking.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                bookingsArray.put(newBooking);
                
                prefs.edit().putString("bookings", bookingsArray.toString()).apply();
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Session booked successfully");
                
                JSONObject data = new JSONObject();
                data.put("session_id", bookingsArray.length());
                response.put("data", data);
                
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to book session: " + e.getMessage());
            }
        }, 400);
    }

    public void completeSession(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                int sessionId = params.getInt("session_id");
                
                // Load sessions from SharedPreferences
                String sessionsJson = prefs.getString("sessions_data", null);
                JSONArray sessions;
                
                if (sessionsJson != null) {
                    sessions = new JSONArray(sessionsJson);
                } else {
                    // Initialize with demo sessions if not exists
                    sessions = getDemoSessions();
                }
                
                // Update the session status
                boolean updated = false;
                for (int i = 0; i < sessions.length(); i++) {
                    JSONObject session = sessions.getJSONObject(i);
                    if (session.getInt("id") == sessionId) {
                        session.put("status", "completed");
                        session.put("payment_status", "paid");
                        updated = true;
                        break;
                    }
                }
                
                // Save back to SharedPreferences
                prefs.edit().putString("sessions_data", sessions.toString()).apply();
                
                JSONObject response = new JSONObject();
                response.put("success", updated);
                response.put("message", updated ? "Session marked as complete" : "Session not found");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to complete session: " + e.getMessage());
            }
        }, 300);
    }

    public void paySession(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                int sessionId = params.getInt("session_id");
                
                // Load sessions from SharedPreferences
                String sessionsJson = prefs.getString("sessions_data", null);
                JSONArray sessions;
                
                if (sessionsJson != null) {
                    sessions = new JSONArray(sessionsJson);
                } else {
                    sessions = getDemoSessions();
                }
                
                // Update the payment status
                boolean updated = false;
                for (int i = 0; i < sessions.length(); i++) {
                    JSONObject session = sessions.getJSONObject(i);
                    if (session.getInt("id") == sessionId) {
                        session.put("payment_status", "paid");
                        updated = true;
                        break;
                    }
                }
                
                // Save back to SharedPreferences
                prefs.edit().putString("sessions_data", sessions.toString()).apply();
                
                JSONObject response = new JSONObject();
                response.put("success", updated);
                response.put("message", updated ? "Payment processed successfully" : "Session not found");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Payment failed: " + e.getMessage());
            }
        }, 500);
    }

    // ============ AVAILABILITY ============
    
    public void getAvailability(int userId, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONArray availability = new JSONArray();
                
                String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
                String[] times = {"09:00", "10:00", "14:00", "15:00"};
                
                for (int i = 0; i < 12; i++) {
                    JSONObject slot = new JSONObject();
                    slot.put("id", i + 1);
                    slot.put("day_of_week", days[i % 5]);
                    slot.put("start_time", times[i % 4]);
                    slot.put("end_time", (Integer.parseInt(times[i % 4].split(":")[0]) + 1) + ":00");
                    slot.put("is_booked", false);
                    availability.put(slot);
                }
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("data", availability);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to load availability: " + e.getMessage());
            }
        }, 200);
    }

    public void addAvailability(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Availability added");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to add availability: " + e.getMessage());
            }
        }, 300);
    }

    public void deleteAvailability(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Availability deleted");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to delete availability: " + e.getMessage());
            }
        }, 300);
    }

    public void toggleAvailability(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Availability updated");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to toggle availability: " + e.getMessage());
            }
        }, 300);
    }

    // ============ FEEDBACK ============
    
    public void submitFeedback(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Feedback submitted successfully");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to submit feedback: " + e.getMessage());
            }
        }, 400);
    }

    // ============ ADMIN ============
    
    public void getAdminStats(ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject stats = new JSONObject();
                stats.put("total_users", 127);
                stats.put("total_mentors", 45);
                stats.put("total_mentees", 82);
                stats.put("total_sessions", 234);
                stats.put("pending_approvals", 3);
                stats.put("revenue", 12450.00);
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("data", stats);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to load stats: " + e.getMessage());
            }
        }, 300);
    }

    public void getPendingMentors(ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONArray pending = new JSONArray();
                
                for (int i = 0; i < 3; i++) {
                    JSONObject mentor = new JSONObject();
                    mentor.put("id", i + 10);
                    mentor.put("full_name", "Pending Mentor " + (i + 1));
                    mentor.put("email", "pending" + (i + 1) + "@example.com");
                    mentor.put("category", "Development");
                    mentor.put("experience_years", 3 + i);
                    pending.put(mentor);
                }
                
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("data", pending);
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to load pending mentors: " + e.getMessage());
            }
        }, 300);
    }

    public void approveMentor(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Mentor approved");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to approve mentor: " + e.getMessage());
            }
        }, 400);
    }

    public void rejectMentor(JSONObject params, ApiResponseListener listener) {
        handler.postDelayed(() -> {
            try {
                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("message", "Mentor rejected");
                listener.onSuccess(response);
            } catch (Exception e) {
                listener.onError("Failed to reject mentor: " + e.getMessage());
            }
        }, 400);
    }

    // Response listener interface
    public interface ApiResponseListener {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
}
