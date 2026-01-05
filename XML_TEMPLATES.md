# XML LAYOUT TEMPLATES - COPY & PASTE READY

## 📐 Complete XML Files (Copy Directly)

### 1. activity_splash.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/primary"
    android:gravity="center">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="MentorBridge"
        android:textColor="@android:color/white"
        android:textSize="32sp"
        android:textStyle="bold" />

    <ProgressBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="100dp"
        android:indeterminateTint="@android:color/white" />
</RelativeLayout>
```

### 2. activity_auth.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="24dp">

        <!-- Login View -->
        <LinearLayout
            android:id="@+id/loginView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Login"
                android:textSize="24sp"
                android:textStyle="bold"
                android:layout_marginBottom="24dp" />

            <EditText
                android:id="@+id/loginEmail"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Email"
                android:inputType="textEmailAddress"
                android:layout_marginBottom="16dp" />

            <EditText
                android:id="@+id/loginPassword"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Password"
                android:inputType="textPassword"
                android:layout_marginBottom="24dp" />

            <Button
                android:id="@+id/btnLogin"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Login"
                android:layout_marginBottom="16dp" />

            <TextView
                android:id="@+id/txtSwitchToRegister"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Don't have an account? Register"
                android:textColor="@color/primary"
                android:layout_gravity="center" />
        </LinearLayout>

        <!-- Register View -->
        <LinearLayout
            android:id="@+id/registerView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="gone">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Register"
                android:textSize="24sp"
                android:textStyle="bold"
                android:layout_marginBottom="24dp" />

            <EditText
                android:id="@+id/regFullName"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Full Name"
                android:inputType="textPersonName"
                android:layout_marginBottom="16dp" />

            <EditText
                android:id="@+id/regEmail"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Email"
                android:inputType="textEmailAddress"
                android:layout_marginBottom="16dp" />

            <EditText
                android:id="@+id/regPassword"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Password"
                android:inputType="textPassword"
                android:layout_marginBottom="16dp" />

            <EditText
                android:id="@+id/regConfirmPassword"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Confirm Password"
                android:inputType="textPassword"
                android:layout_marginBottom="16dp" />

            <RadioGroup
                android:id="@+id/roleRadioGroup"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_marginBottom="24dp">

                <RadioButton
                    android:id="@+id/radioMentor"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:text="Mentor" />

                <RadioButton
                    android:id="@+id/radioMentee"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:text="Mentee"
                    android:checked="true" />
            </RadioGroup>

            <Button
                android:id="@+id/btnRegister"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Register"
                android:layout_marginBottom="16dp" />

            <TextView
                android:id="@+id/txtSwitchToLogin"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Already have an account? Login"
                android:textColor="@color/primary"
                android:layout_gravity="center" />
        </LinearLayout>

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_marginTop="16dp"
            android:visibility="gone" />
    </LinearLayout>
</ScrollView>
```

### 3. activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawerLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="@color/primary"
            android:elevation="4dp"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar">

            <TextView
                android:id="@+id/toolbarTitle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Dashboard"
                android:textColor="@android:color/white"
                android:textSize="20sp"
                android:textStyle="bold" />
        </androidx.appcompat.widget.Toolbar>

        <FrameLayout
            android:id="@+id/fragmentContainer"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1" />

        <com.google.android.material.bottomnavigation.BottomNavigationView
            android:id="@+id/bottomNavigation"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@android:color/white"
            app:elevation="8dp" />
    </LinearLayout>

    <com.google.android.material.navigation.NavigationView
        android:id="@+id/navigationView"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start" />
</androidx.drawerlayout.widget.DrawerLayout>
```

### 4. nav_header.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="176dp"
    android:background="@color/primary"
    android:orientation="vertical"
    android:padding="16dp"
    android:gravity="bottom">

    <TextView
        android:id="@+id/navHeaderName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="User Name"
        android:textColor="@android:color/white"
        android:textSize="18sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/navHeaderEmail"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="user@example.com"
        android:textColor="@android:color/white"
        android:layout_marginTop="4dp" />
</LinearLayout>
```

### 5. Fragment Layout Template (Use for all fragments)
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/swipeRefresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:padding="16dp">

            <TextView
                android:id="@+id/txtWelcome"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Welcome"
                android:textSize="20sp"
                android:textStyle="bold"
                android:layout_marginBottom="16dp" />

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/recyclerView"
                android:layout_width="match_parent"
                android:layout_height="match_parent" />
        </LinearLayout>

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:visibility="gone" />
    </FrameLayout>
</androidx.swiperefreshLayout.widget.SwipeRefreshLayout>
```

### 6. item_mentor.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/cardView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/txtName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Mentor Name"
            android:textSize="18sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/txtCategories"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Categories"
            android:textSize="14sp"
            android:textColor="@color/primary"
            android:layout_marginTop="4dp" />

        <TextView
            android:id="@+id/txtBio"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Bio"
            android:maxLines="2"
            android:ellipsize="end"
            android:layout_marginTop="8dp" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginTop="8dp"
            android:gravity="center_vertical">

            <RatingBar
                android:id="@+id/ratingBar"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:numStars="5"
                android:stepSize="0.5"
                android:isIndicator="true"
                style="?android:attr/ratingBarStyleSmall" />

            <TextView
                android:id="@+id/txtReviews"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="(0)"
                android:layout_marginStart="8dp" />

            <View
                android:layout_width="0dp"
                android:layout_height="1dp"
                android:layout_weight="1" />

            <TextView
                android:id="@+id/txtRate"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="$50/hr"
                android:textStyle="bold"
                android:textColor="@color/primary" />
        </LinearLayout>
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

### 7. item_category.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/cardView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center_vertical">

        <TextView
            android:id="@+id/txtIcon"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:text="💻"
            android:textSize="24sp"
            android:gravity="center" />

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:layout_marginStart="16dp">

            <TextView
                android:id="@+id/txtName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Category Name"
                android:textSize="16sp"
                android:textStyle="bold" />

            <TextView
                android:id="@+id/txtDescription"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Description"
                android:maxLines="1"
                android:ellipsize="end"
                android:layout_marginTop="4dp" />

            <TextView
                android:id="@+id/txtCount"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="0 mentors"
                android:textSize="12sp"
                android:textColor="@color/primary"
                android:layout_marginTop="4dp" />
        </LinearLayout>
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

### 8. item_availability.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:gravity="center_vertical">

    <View
        android:id="@+id/viewIndicator"
        android:layout_width="12dp"
        android:layout_height="12dp"
        android:background="@android:color/holo_green_dark"
        android:layout_marginEnd="16dp" />

    <TextView
        android:id="@+id/txtDay"
        android:layout_width="100dp"
        android:layout_height="wrap_content"
        android:text="Monday"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/txtTime"
        android:layout_width="80dp"
        android:layout_height="wrap_content"
        android:text="09:00" />

    <TextView
        android:id="@+id/txtStatus"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Available"
        android:textColor="@android:color/holo_green_dark" />
</LinearLayout>
```

### 9. item_session.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/cardView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/txtName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Name"
            android:textSize="16sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/txtDate"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Date"
            android:layout_marginTop="4dp" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginTop="8dp">

            <TextView
                android:id="@+id/txtAmount"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="$60.00"
                android:textStyle="bold" />

            <TextView
                android:id="@+id/txtStatus"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Pending"
                android:textColor="@color/primary" />
        </LinearLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginTop="12dp">

            <Button
                android:id="@+id/btnPay"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Pay"
                android:visibility="gone" />

            <Button
                android:id="@+id/btnComplete"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Complete"
                android:visibility="gone" />

            <Button
                android:id="@+id/btnFeedback"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Feedback"
                android:visibility="gone" />
        </LinearLayout>
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

### 10. item_review.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical">

        <TextView
            android:id="@+id/txtMenteeName"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Mentee Name"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/txtDate"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Date"
            android:textSize="12sp" />
    </LinearLayout>

    <RatingBar
        android:id="@+id/ratingBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:numStars="5"
        android:stepSize="1"
        android:isIndicator="true"
        android:layout_marginTop="4dp"
        style="?android:attr/ratingBarStyleSmall" />

    <TextView
        android:id="@+id/txtComment"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Comment"
        android:layout_marginTop="4dp" />

    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:background="@android:color/darker_gray"
        android:layout_marginTop="12dp" />
</LinearLayout>
```

### 11. res/values/strings.xml
```xml
<resources>
    <string name="app_name">MentorBridge</string>
    <string name="navigation_drawer_open">Open navigation drawer</string>
    <string name="navigation_drawer_close">Close navigation drawer</string>
</resources>
```

### 12. res/values/colors.xml
```xml
<resources>
    <color name="primary">#6366F1</color>
    <color name="primary_dark">#4F46E5</color>
    <color name="accent">#A78BFA</color>
    <color name="background">#F8F9FA</color>
    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
    <color name="text_primary">#1F2937</color>
    <color name="text_secondary">#6B7280</color>
</resources>
```

### 13. res/values/themes.xml
```xml
<resources>
    <style name="Theme.MentorBridge" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryDark">@color/primary_dark</item>
        <item name="colorAccent">@color/accent</item>
    </style>

    <style name="Theme.MentorBridge.Splash" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="android:windowBackground">@color/primary</item>
    </style>
</resources>
```

### 14. res/menu/mentor_bottom_menu.xml
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_mentor_dashboard"
        android:icon="@android:drawable/ic_menu_view"
        android:title="Dashboard" />
    <item
        android:id="@+id/nav_mentor_sessions"
        android:icon="@android:drawable/ic_menu_my_calendar"
        android:title="Sessions" />
    <item
        android:id="@+id/nav_mentor_availability"
        android:icon="@android:drawable/ic_menu_recent_history"
        android:title="Availability" />
    <item
        android:id="@+id/nav_mentor_profile"
        android:icon="@android:drawable/ic_menu_preferences"
        android:title="Profile" />
</menu>
```

### 15. res/menu/mentee_bottom_menu.xml
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_mentee_dashboard"
        android:icon="@android:drawable/ic_menu_view"
        android:title="Dashboard" />
    <item
        android:id="@+id/nav_mentee_mentors"
        android:icon="@android:drawable/ic_menu_search"
        android:title="Find Mentors" />
    <item
        android:id="@+id/nav_mentee_sessions"
        android:icon="@android:drawable/ic_menu_my_calendar"
        android:title="Sessions" />
    <item
        android:id="@+id/nav_mentee_profile"
        android:icon="@android:drawable/ic_menu_preferences"
        android:title="Profile" />
</menu>
```

### 16. res/menu/admin_drawer_menu.xml
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_admin_dashboard"
        android:icon="@android:drawable/ic_menu_view"
        android:title="Dashboard" />
    <item
        android:id="@+id/nav_admin_approvals"
        android:icon="@android:drawable/ic_menu_agenda"
        android:title="Mentor Approvals" />
    <item
        android:id="@+id/nav_admin_logout"
        android:icon="@android:drawable/ic_menu_close_clear_cancel"
        android:title="Logout" />
</menu>
```

---

## 🚀 How to Use These Templates

1. **In Android Studio**: Right-click `res/layout` → New → Layout Resource File
2. **Name it exactly** as needed (e.g., `activity_splash`)
3. **Copy the corresponding XML** from above
4. **Paste and save**

**That's it! Repeat for all layout files.**

Total time needed: **30-45 minutes** to create all XML files.
