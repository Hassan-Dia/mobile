# MentorBridge PHP Backend for XAMPP

This is the backend API for the MentorBridge Android app, designed to run on XAMPP.

## 📋 Prerequisites

- **XAMPP** (Apache + MySQL + PHP)
- Download from: https://www.apachefriends.org/

## 🚀 Installation Steps

### 1. Install XAMPP

1. Download and install XAMPP for Windows
2. Install to default location: `C:\xampp`

### 2. Copy Backend Files

Copy the entire `api` and `config` folders to:
```
C:\xampp\htdocs\mentorbridge\
```

Your folder structure should look like:
```
C:\xampp\htdocs\mentorbridge\
├── api/
│   ├── login.php
│   ├── register.php
│   ├── get_mentors.php
│   ├── get_mentor.php
│   ├── get_sessions.php
│   ├── sessions.php
│   ├── reviews.php
│   └── get_reviews.php
├── config/
│   ├── database.php
│   └── cors.php
└── database/
    └── schema.sql
```

### 3. Setup Database

1. **Start XAMPP**
   - Open XAMPP Control Panel
   - Click "Start" for Apache
   - Click "Start" for MySQL

2. **Create Database**
   - Open browser and go to: http://localhost/phpmyadmin
   - Click "SQL" tab
   - Copy and paste the contents of `database/schema.sql`
   - Click "Go" to execute
   - This will create the database and sample data

### 4. Configure Android App

The Android app is already configured to use XAMPP:

**For Android Emulator:**
- Already set to: `http://10.0.2.2/mentorbridge/api/`
- No changes needed!

**For Real Android Device:**
1. Find your computer's local IP address:
   - Open Command Prompt
   - Type: `ipconfig`
   - Look for "IPv4 Address" (e.g., 192.168.1.100)

2. Edit `ApiConfig.java`:
   ```java
   // Comment out emulator URL
   // public static final String BASE_URL = "http://10.0.2.2/mentorbridge/api/";
   
   // Uncomment and use your IP
   public static final String BASE_URL = "http://192.168.1.100/mentorbridge/api/";
   ```

3. Make sure your phone and computer are on the same WiFi network

## 🧪 Testing the API

### Test in Browser

1. **Get All Mentors:**
   ```
   http://localhost/mentorbridge/api/get_mentors.php
   ```

2. **Get Single Mentor:**
   ```
   http://localhost/mentorbridge/api/get_mentor.php?id=1
   ```

3. **Get Sessions:**
   ```
   http://localhost/mentorbridge/api/get_sessions.php?userId=1
   ```

### Test with Postman

1. **Login (POST)**
   - URL: `http://localhost/mentorbridge/api/login.php`
   - Method: POST
   - Body (raw JSON):
   ```json
   {
     "email": "john@example.com",
     "password": "password"
   }
   ```

2. **Register (POST)**
   - URL: `http://localhost/mentorbridge/api/register.php`
   - Method: POST
   - Body (raw JSON):
   ```json
   {
     "name": "Test User",
     "email": "test@example.com",
     "password": "test123"
   }
   ```

3. **Book Session (POST)**
   - URL: `http://localhost/mentorbridge/api/sessions.php`
   - Method: POST
   - Body (raw JSON):
   ```json
   {
     "mentorId": 1,
     "userId": 1,
     "date": "2026-02-10",
     "time": "14:00",
     "topic": "Android Development"
   }
   ```

## 📊 Sample Data

The database comes with sample data:

**Users:**
- Email: `john@example.com`, Password: `password`
- Email: `jane@example.com`, Password: `password`

**Mentors:**
- Sarah Chen (Software Development)
- Michael Rodriguez (Data Science)
- Emma Thompson (UI/UX Design)
- David Kim (Cloud Architecture)
- Lisa Anderson (Career Coaching)

## 🔧 Troubleshooting

### Apache won't start
- Port 80 might be in use
- Stop IIS or other web servers
- Or change Apache port in XAMPP config

### MySQL won't start
- Port 3306 might be in use
- Stop other MySQL services
- Or change MySQL port in XAMPP config

### Database connection error
- Check if MySQL is running in XAMPP
- Verify database name in `config/database.php`
- Default credentials: username=`root`, password=`` (empty)

### CORS errors
- Make sure `cors.php` is included in all API files
- Check browser console for specific errors

### Can't access from Android device
- Make sure phone and computer are on same WiFi
- Check Windows Firewall allows connections on port 80
- Temporarily disable firewall to test

## 📁 File Descriptions

### API Endpoints

| File | Method | Description |
|------|--------|-------------|
| `login.php` | POST | User login with email/password |
| `register.php` | POST | Register new user |
| `get_mentors.php` | GET | Get all mentors (with optional expertise filter) |
| `get_mentor.php` | GET | Get single mentor by ID |
| `sessions.php` | POST/PUT/DELETE | Book, update, or cancel sessions |
| `get_sessions.php` | GET | Get user's sessions |
| `reviews.php` | POST | Submit review |
| `get_reviews.php` | GET | Get mentor reviews |

### Configuration

- **`config/database.php`** - Database connection settings
- **`config/cors.php`** - CORS headers for mobile app access
- **`database/schema.sql`** - Database structure and sample data

## 🔐 Security Notes

⚠️ **This is a development setup!** For production:

1. Use proper JWT tokens (not simple random tokens)
2. Add HTTPS encryption
3. Implement rate limiting
4. Validate and sanitize all inputs
5. Use prepared statements (already implemented)
6. Set strong MySQL password
7. Restrict CORS to your domain only
8. Add proper authentication middleware

## 📱 Using with Android App

The Android app automatically uses these endpoints. Just:

1. Start XAMPP (Apache + MySQL)
2. Build and run your Android app
3. The app will connect to `http://10.0.2.2/mentorbridge/api/`

All service classes (`AuthService`, `MentorService`, `SessionService`) are already configured!

## 🎯 Next Steps

1. ✅ Install XAMPP
2. ✅ Copy files to htdocs
3. ✅ Setup database
4. ✅ Test API endpoints
5. ✅ Run Android app
6. 🎉 Start developing!

## 📞 Support

If you encounter issues:
1. Check XAMPP Control Panel - both Apache and MySQL should be green
2. Check error logs in `C:\xampp\apache\logs\error.log`
3. Check PHP errors in `C:\xampp\php\logs\php_error_log`
4. Test endpoints in browser first before testing in app
