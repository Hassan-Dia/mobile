package com.mentorbridge.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.mentorbridge.app.R;
import com.mentorbridge.app.models.Profile;
import com.mentorbridge.app.models.User;
import com.mentorbridge.app.utils.ApiClient;
import com.mentorbridge.app.utils.SessionManager;
import com.mentorbridge.app.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    private boolean isLoginMode = true;
    
    // Login views
    private View loginView;
    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView txtSwitchToRegister;
    
    // Register views
    private View registerView;
    private EditText regFullName, regEmail, regPassword, regConfirmPassword;
    private RadioGroup roleRadioGroup;
    private Button btnRegister;
    private TextView txtSwitchToLogin;
    
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private SessionManager sessionManager;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        sessionManager = new SessionManager(this);
        apiClient = ApiClient.getInstance(this);

        initViews();
        setupListeners();
        showLoginView();
    }

    private void initViews() {
        // Login views
        loginView = findViewById(R.id.loginView);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSwitchToRegister = findViewById(R.id.txtSwitchToRegister);
        
        // Register views
        registerView = findViewById(R.id.registerView);
        regFullName = findViewById(R.id.regFullName);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regConfirmPassword = findViewById(R.id.regConfirmPassword);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        btnRegister = findViewById(R.id.btnRegister);
        txtSwitchToLogin = findViewById(R.id.txtSwitchToLogin);
        
        progressBar = findViewById(R.id.progressBar);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
        txtSwitchToRegister.setOnClickListener(v -> showRegisterView());
        txtSwitchToLogin.setOnClickListener(v -> showLoginView());
        
        // Setup TabLayout listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showLoginView();
                } else {
                    showRegisterView();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showLoginView() {
        isLoginMode = true;
        loginView.setVisibility(View.VISIBLE);
        registerView.setVisibility(View.GONE);
        if (tabLayout.getSelectedTabPosition() != 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null) tab.select();
        }
    }

    private void showRegisterView() {
        isLoginMode = false;
        loginView.setVisibility(View.GONE);
        registerView.setVisibility(View.VISIBLE);
        if (tabLayout.getSelectedTabPosition() != 1) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            if (tab != null) tab.select();
        }
    }

    private void handleLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        try {
            JSONObject params = new JSONObject();
            params.put("action", "login");
            params.put("email", email);
            params.put("password", password);

            apiClient.login(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    showLoading(false);
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            
                            User user = new User();
                            user.setUserId(data.getInt("user_id"));
                            user.setEmail(data.getString("email"));
                            user.setRole(data.getString("role"));
                            
                            if (data.has("profile") && !data.isNull("profile")) {
                                JSONObject profileData = data.getJSONObject("profile");
                                Profile profile = new Profile();
                                profile.setId(profileData.getInt("id"));
                                profile.setFullName(profileData.getString("full_name"));
                                user.setProfile(profile);
                            }
                            
                            sessionManager.createSession(user);
                            
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AuthActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Toast.makeText(AuthActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            showLoading(false);
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRegister() {
        String fullName = regFullName.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String confirmPassword = regConfirmPassword.getText().toString().trim();
        
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        String role = selectedRoleId == R.id.radioMentor ? "mentor" : "mentee";

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || 
            TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        try {
            JSONObject params = new JSONObject();
            params.put("action", "register");
            params.put("full_name", fullName);
            params.put("email", email);
            params.put("password", password);
            params.put("role", role);

            apiClient.register(params, new ApiClient.ApiResponseListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    showLoading(false);
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            
                            User user = new User();
                            user.setUserId(data.getInt("user_id"));
                            user.setEmail(data.getString("email"));
                            user.setRole(data.getString("role"));
                            
                            if (data.has("profile") && !data.isNull("profile")) {
                                JSONObject profileData = data.getJSONObject("profile");
                                Profile profile = new Profile();
                                profile.setId(profileData.getInt("id"));
                                profile.setFullName(profileData.getString("full_name"));
                                user.setProfile(profile);
                            }
                            
                            sessionManager.createSession(user);
                            
                            Toast.makeText(AuthActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AuthActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    Toast.makeText(AuthActivity.this, "Registration failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            showLoading(false);
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnRegister.setEnabled(!show);
    }
}
