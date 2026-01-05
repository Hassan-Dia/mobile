# MentorBridge Android App

## 📱 Project Overview
Native Android application (Java + XML) for the MentorBridge mentorship platform. This app connects with the existing PHP backend via REST APIs using Volley for HTTP requests.

## 🏗️ Architecture

### Tech Stack
- **Language**: Java
- **UI**: XML Layouts
- **Networking**: Volley
- **Navigation**: Fragments + Bottom Navigation + Navigation Drawer
- **Backend**: PHP + MySQL (existing)
- **API**: REST JSON endpoints

### Project Structure
```
MentorBridgeAndroid/
├── app/
│   ├── src/main/
│   │   ├── java/com/mentorbridge/app/
│   │   │   ├── activities/
│   │   │   │   ├── SplashActivity.java
│   │   │   │   ├── AuthActivity.java (Login/Register)
│   │   │   │   ├── MainActivity.java (Fragment Host)
│   │   │   │   ├── MentorDetailActivity.java
│   │   │   │   └── BookSessionActivity.java
│   │   │   ├── fragments/
│   │   │   │   ├── mentee/
│   │   │   │   │   ├── MenteeDashboardFragment.java
│   │   │   │   │   └── MentorListFragment.java
│   │   │   │   ├── mentor/
│   │   │   │   │   ├── MentorDashboardFragment.java
│   │   │   │   │   └── AvailabilityFragment.java
│   │   │   │   ├── admin/
│   │   │   │   │   ├── AdminDashboardFragment.java
│   │   │   │   │   └── AdminApprovalFragment.java
│   │   │   │   └── shared/
│   │   │   │       ├── MySessionsFragment.java
│   │   │   │       └── ProfileFragment.java
│   │   │   ├── adapters/
│   │   │   │   ├── MentorAdapter.java
│   │   │   │   ├── SessionAdapter.java
│   │   │   │   ├── CategoryAdapter.java
│   │   │   │   ├── AvailabilityAdapter.java
│   │   │   │   └── ReviewAdapter.java
│   │   │   ├── models/
│   │   │   │   ├── User.java
│   │   │   │   ├── Profile.java
│   │   │   │   ├── Mentor.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Session.java
│   │   │   │   ├── Availability.java
│   │   │   │   ├── Review.java
│   │   │   │   └── AdminStats.java
│   │   │   └── utils/
│   │   │       ├── SessionManager.java (SharedPreferences)
│   │   │       ├── ApiClient.java (Volley wrapper)
│   │   │       └── Utils.java (Helper functions)
│   │   ├── res/
│   │   │   ├── layout/ (All XML layouts)
│   │   │   ├── values/ (strings, colors, styles)
│   │   │   ├── drawable/ (icons, backgrounds)
│   │   │   └── menu/ (navigation menus)
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── api/ (PHP REST endpoints)
│   ├── auth.php
│   ├── mentors.php
│   ├── categories.php
│   ├── sessions.php
│   ├── availability.php
│   ├── feedback.php
│   └── admin.php
└── README.md (this file)
```

## 🚀 Setup Instructions

### Prerequisites
1. Android Studio (latest version)
2. JDK 8 or higher
3. XAMPP/WAMP (for PHP backend)
4. Android device or emulator (API 24+)

### Backend Setup
1. **Start XAMPP/WAMP**
   - Start Apache and MySQL services

2. **Import Database**
   ```bash
   - Open phpMyAdmin (http://localhost/phpmyadmin)
   - Create database 'mentorbridge'
   - Import database.sql file
   ```

3. **Configure PHP Backend**
   - Place the entire project in htdocs folder
   - Verify config.php database credentials match your setup
   - Test API endpoints: http://localhost/mentorbridge-php-project-main/api/categories.php

### Android App Setup

1. **Open Project in Android Studio**
   ```
   File → Open → Select MentorBridgeAndroid folder
   ```

2. **Configure API Base URL**
   - Open `ApiClient.java`
   - Update `BASE_URL` constant:
   
   **For Android Emulator:**
   ```java
   private static final String BASE_URL = "http://10.0.2.2/mentorbridge-php-project-main/api/";
   ```
   
   **For Physical Device (same network):**
   ```java
   private static final String BASE_URL = "http://YOUR_COMPUTER_IP/mentorbridge-php-project-main/api/";
   ```
   Example: `"http://192.168.1.100/mentorbridge-php-project-main/api/"`

3. **Sync Gradle**
   - Click "Sync Project with Gradle Files" button
   - Wait for dependencies to download

4. **Run the App**
   - Select device/emulator
   - Click Run button (green triangle)

## 👥 User Roles & Navigation

### Admin
- **Navigation**: Navigation Drawer (Hamburger menu)
- **Features**:
  - View platform statistics
  - Approve/reject mentor applications
  - Approve mentor profile updates
  - Manage users

### Mentor
- **Navigation**: Bottom Navigation
- **Features**:
  - Dashboard with session stats
  - Manage availability (4 states)
  - View sessions
  - Mark sessions as completed
  - Edit profile (triggers re-approval if has sessions)

### Mentee
- **Navigation**: Bottom Navigation
- **Features**:
  - Browse mentors by category
  - Search mentors
  - View mentor details
  - Book sessions
  - View session history
  - Leave feedback after completed sessions

## 🎨 UI Components

### Navigation Patterns
- **Admin**: NavigationDrawer with menu items
- **Mentor/Mentee**: BottomNavigationView (4 tabs)
- **Fragment**: All screens use Fragments in MainActivity

### Availability States (Visual Indicators)
1. **Available** 🟢 - Green indicator
2. **Booked** 🟠 - Orange indicator
3. **Waiting for Feedback** 🔵 - Blue indicator
4. **Disabled** ⚫ - Gray indicator

### RecyclerViews
- Mentor list
- Category list
- Session list
- Availability slots
- Reviews
- Pending mentors (admin)

## 🔐 Authentication Flow

1. **SplashActivity** → Check session
2. If logged in → **MainActivity**
3. If not → **AuthActivity** (Login/Register)
4. After login/register → **MainActivity**

## 📡 API Endpoints

All endpoints return JSON:
```json
{
  "success": true/false,
  "message": "...",
  "data": {...}
}
```

### Authentication
- `POST api/auth.php?action=login`
- `POST api/auth.php?action=register`

### Mentors
- `GET api/mentors.php?action=list&category_id=X&search=Y`
- `GET api/mentors.php?action=detail&id=X`
- `POST api/mentors.php?action=update_profile`

### Sessions
- `GET api/sessions.php?action=list&user_id=X&role=Y`
- `POST api/sessions.php?action=book`
- `POST api/sessions.php?action=complete`
- `POST api/sessions.php?action=pay`

### Availability
- `GET api/availability.php?action=list&user_id=X`
- `POST api/availability.php?action=add`
- `POST api/availability.php?action=delete`
- `POST api/availability.php?action=toggle`

### Feedback
- `POST api/feedback.php?action=submit`

### Admin
- `GET api/admin.php?action=stats`
- `GET api/admin.php?action=pending_mentors`
- `POST api/admin.php?action=approve`
- `POST api/admin.php?action=reject`

## 🔨 Implementation Highlights

### Explicit Intents
```java
// Activity navigation with data
Intent intent = new Intent(this, MentorDetailActivity.class);
intent.putExtra("mentor_id", mentorId);
startActivity(intent);
```

### Implicit Intents
```java
// Email
Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
emailIntent.setData(Uri.parse("mailto:" + email));

// Phone
Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
phoneIntent.setData(Uri.parse("tel:" + phone));

// Browser
Intent browserIntent = new Intent(Intent.ACTION_VIEW);
browserIntent.setData(Uri.parse(url));
```

### Session Management (SharedPreferences)
```java
SessionManager session = new SessionManager(context);
session.createSession(user);
boolean isLoggedIn = session.isLoggedIn();
String role = session.getRole();
session.logout();
```

### Volley Networking
```java
ApiClient api = ApiClient.getInstance(context);
api.getMentors("action=list", new ApiClient.ApiResponseListener() {
    @Override
    public void onSuccess(JSONObject response) {
        // Handle success
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

## 📋 Default Login Credentials

From database.sql:

**Admin:**
- Email: admin@mentorbridge.com
- Password: admin123

**Test Mentor:**
- Email: john.mentor@example.com
- Password: admin123

**Test Mentee:**
- Email: jane.student@example.com
- Password: admin123

## 🐛 Troubleshooting

### "Network Error" or "Connection Failed"
1. Verify XAMPP Apache is running
2. Check API URL in ApiClient.java
3. For emulator: Use 10.0.2.2
4. For device: Use computer's IP (same WiFi network)
5. Test API in browser first: http://YOUR_IP/path/api/categories.php

### "No data showing"
1. Check database has been imported
2. Verify default data exists (categories, test users)
3. Check Logcat for JSON parsing errors

### Build Errors
1. File → Invalidate Caches / Restart
2. Clean Project
3. Rebuild Project
4. Sync Gradle again

## 📦 Dependencies

All dependencies are defined in `app/build.gradle`:
- Volley (networking)
- Material Components (UI)
- RecyclerView & CardView
- Fragment & Navigation
- SwipeRefreshLayout

## 🎯 Key Features

✅ Role-based authentication
✅ Fragment-based navigation
✅ Bottom Navigation (Mentor/Mentee)
✅ Navigation Drawer (Admin)
✅ RecyclerView with adapters
✅ Volley HTTP requests
✅ JSON parsing
✅ Session management
✅ 4-state availability system
✅ Explicit & Implicit Intents
✅ Material Design UI
✅ Network error handling
✅ Loading states
✅ SwipeRefresh

## 📝 License

Educational project for university submission.

## 👨‍💻 Development Notes

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build Tools**: 34.0.0
- **Gradle**: 8.1.0

---

**Note**: This is a complete, production-ready Android application that mirrors all functionality from the PHP web version while following Android development best practices.
