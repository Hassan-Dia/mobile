package com.mentorbridge.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mentorbridge.app.R;
import com.mentorbridge.app.fragments.admin.AdminApprovalFragment;
import com.mentorbridge.app.fragments.admin.AdminDashboardFragment;
import com.mentorbridge.app.fragments.mentee.MenteeDashboardFragment;
import com.mentorbridge.app.fragments.mentee.MentorListFragment;
import com.mentorbridge.app.fragments.mentor.AvailabilityFragment;
import com.mentorbridge.app.fragments.mentor.MentorDashboardFragment;
import com.mentorbridge.app.fragments.shared.MySessionsFragment;
import com.mentorbridge.app.fragments.shared.ProfileFragment;
import com.mentorbridge.app.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            redirectToAuth();
            return;
        }

        initViews();
        setupNavigation();
        loadInitialFragment();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        setSupportActionBar(toolbar);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Update navigation header
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
        TextView navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);
        navHeaderName.setText(sessionManager.getFullName());
        navHeaderEmail.setText(sessionManager.getEmail());
    }

    private void setupNavigation() {
        if (sessionManager.isAdmin()) {
            setupAdminNavigation();
        } else {
            setupUserNavigation();
        }
    }

    private void setupAdminNavigation() {
        // Admin uses Navigation Drawer
        bottomNavigation.setVisibility(View.GONE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 
                R.string.navigation_drawer_open, 
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            selectDrawerItem(item);
            return true;
        });

        // Set admin menu
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.admin_drawer_menu);
    }

    private void setupUserNavigation() {
        // Mentors and Mentees use Bottom Navigation
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        bottomNavigation.setVisibility(View.VISIBLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            selectBottomNavItem(item);
            return true;
        });

        // Set menu based on role
        bottomNavigation.getMenu().clear();
        if (sessionManager.isMentor()) {
            bottomNavigation.inflateMenu(R.menu.mentor_bottom_menu);
        } else {
            bottomNavigation.inflateMenu(R.menu.mentee_bottom_menu);
        }
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_admin_dashboard) {
            fragment = new AdminDashboardFragment();
            toolbarTitle.setText("Dashboard");
        } else if (itemId == R.id.nav_admin_approvals) {
            fragment = new AdminApprovalFragment();
            toolbarTitle.setText("Mentor Approvals");
        } else if (itemId == R.id.nav_admin_logout) {
            handleLogout();
            return;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private boolean selectBottomNavItem(MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (sessionManager.isMentor()) {
            if (itemId == R.id.nav_mentor_dashboard) {
                fragment = new MentorDashboardFragment();
                toolbarTitle.setText("Dashboard");
            } else if (itemId == R.id.nav_mentor_sessions) {
                fragment = new MySessionsFragment();
                toolbarTitle.setText("My Sessions");
            } else if (itemId == R.id.nav_mentor_availability) {
                fragment = new AvailabilityFragment();
                toolbarTitle.setText("Availability");
            } else if (itemId == R.id.nav_mentor_profile) {
                fragment = new ProfileFragment();
                toolbarTitle.setText("Profile");
            }
        } else {
            if (itemId == R.id.nav_mentee_dashboard) {
                fragment = new MenteeDashboardFragment();
                toolbarTitle.setText("Dashboard");
            } else if (itemId == R.id.nav_mentee_mentors) {
                fragment = new MentorListFragment();
                toolbarTitle.setText("Find Mentors");
            } else if (itemId == R.id.nav_mentee_sessions) {
                fragment = new MySessionsFragment();
                toolbarTitle.setText("My Sessions");
            } else if (itemId == R.id.nav_mentee_profile) {
                fragment = new ProfileFragment();
                toolbarTitle.setText("Profile");
            }
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }

        return false;
    }

    private void loadInitialFragment() {
        if (sessionManager.isAdmin()) {
            toolbarTitle.setText("Dashboard");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AdminDashboardFragment())
                    .commit();
        } else if (sessionManager.isMentor()) {
            toolbarTitle.setText("Dashboard");
            bottomNavigation.setSelectedItemId(R.id.nav_mentor_dashboard);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new MentorDashboardFragment())
                    .commit();
        } else {
            toolbarTitle.setText("Dashboard");
            bottomNavigation.setSelectedItemId(R.id.nav_mentee_dashboard);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new MenteeDashboardFragment())
                    .commit();
        }
    }

    private void handleLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sessionManager.logout();
                    redirectToAuth();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void redirectToAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public void showLogoutOption() {
        handleLogout();
    }
}
