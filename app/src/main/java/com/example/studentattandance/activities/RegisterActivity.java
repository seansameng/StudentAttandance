package com.example.studentattandance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.models.User;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.util.Date;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etFirstName;
    private EditText etLastName;
    private Spinner spinnerRole;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView tvBackToWelcome;
    
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        setupSpinner();
        setupClickListeners();
        
        dataRepository = new DataRepository(this);
        sessionManager = SessionManager.getInstance(this);
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        spinnerRole = findViewById(R.id.spinner_role);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
        tvBackToWelcome = findViewById(R.id.tv_back_to_welcome);
    }
    
    private void setupSpinner() {
        String[] roles = {"STUDENT", "TEACHER", "ADMIN"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        
        tvBackToWelcome.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
    
    private void attemptRegister() {
        // Reset errors
        etUsername.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        etFirstName.setError(null);
        etLastName.setError(null);
        
        // Get values from input fields
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        
        // Check for valid input
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return;
        }
        
        // Show progress and disable register button
        showProgress(true);
        
        // Use local database registration instead of API
        performLocalRegistration(username, email, password, firstName, lastName, role);
    }
    
    private void performLocalRegistration(String username, String email, String password, 
                                       String firstName, String lastName, String role) {
        try {
            // Create UserEntity for local database
            UserEntity userEntity = new UserEntity();
            userEntity.setId(UUID.randomUUID().toString());
            userEntity.setUsername(username);
            userEntity.setEmail(email);
            userEntity.setPassword(password);
            userEntity.setFirstName(firstName);
            userEntity.setLastName(lastName);
            userEntity.setRole(role);
            userEntity.setCreatedAt(new Date());
            userEntity.setLastLogin(new Date());
            
            // Insert user into local database
            dataRepository.insertUser(userEntity);
            
            // Create User model for session
            User user = new User();
            user.setId(userEntity.getId());
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            
            // Set role based on string value
            switch (role) {
                case "ADMIN":
                    user.setRole(User.UserRole.ADMIN);
                    break;
                case "TEACHER":
                    user.setRole(User.UserRole.TEACHER);
                    break;
                case "STUDENT":
                    user.setRole(User.UserRole.STUDENT);
                    break;
                default:
                    user.setRole(User.UserRole.STUDENT);
                    break;
            }
            user.setActive(true);
            
            // Create login session
            sessionManager.createLoginSession(
                "local_token_" + System.currentTimeMillis(),
                "local_refresh_token",
                user,
                3600
            );
            
            // Show success message
            showProgress(false);
            Toast.makeText(this, "Registration successful! Welcome " + firstName, Toast.LENGTH_LONG).show();
            
            // Navigate to main activity
            navigateToMain();
            
        } catch (Exception e) {
            showProgress(false);
            Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        etUsername.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
        etConfirmPassword.setEnabled(!show);
        etFirstName.setEnabled(!show);
        etLastName.setEnabled(!show);
        spinnerRole.setEnabled(!show);
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
