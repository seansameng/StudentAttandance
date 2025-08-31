package com.example.studentattandance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddUserActivity extends AppCompatActivity {
    
    private static final String TAG = "AddUserActivity";
    
    private EditText etUsername, etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private LinearLayout formLayout;
    
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        
        try {
            Log.d(TAG, "AddUserActivity onCreate started");
            
            // Initialize components
            initViews();
            setupToolbar();
            setupSpinners();
            setupClickListeners();
            
            // Initialize data
            dataRepository = new DataRepository(this);
            sessionManager = SessionManager.getInstance(this);
            executorService = Executors.newSingleThreadExecutor();
            
            // Check admin access
            if (!isAdminUser()) {
                Toast.makeText(this, "Access denied. Admin only.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            Log.d(TAG, "AddUserActivity onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initViews() {
        try {
            etUsername = findViewById(R.id.et_username);
            etFirstName = findViewById(R.id.et_first_name);
            etLastName = findViewById(R.id.et_last_name);
            etEmail = findViewById(R.id.et_email);
            etPassword = findViewById(R.id.et_password);
            etConfirmPassword = findViewById(R.id.et_confirm_password);
            spinnerRole = findViewById(R.id.spinner_role);
            btnSave = findViewById(R.id.btn_save);
            btnCancel = findViewById(R.id.btn_cancel);
            progressBar = findViewById(R.id.progress_bar);
            formLayout = findViewById(R.id.form_layout);
            
            Log.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw e;
        }
    }
    
    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Add New User");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar", e);
        }
    }
    
    private void setupSpinners() {
        try {
            String[] roles = {"STUDENT", "TEACHER", "ADMIN"};
            ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, roles);
            roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRole.setAdapter(roleAdapter);
            
            Log.d(TAG, "Spinners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up spinners", e);
        }
    }
    
    private void setupClickListeners() {
        try {
            btnSave.setOnClickListener(v -> validateAndSaveUser());
            btnCancel.setOnClickListener(v -> finish());
            
            Log.d(TAG, "Click listeners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
        }
    }
    
    private boolean isAdminUser() {
        try {
            if (sessionManager == null || !sessionManager.isLoggedIn()) {
                return false;
            }
            
            String role = sessionManager.getUserRole();
            return "ADMIN".equals(role) || "Administrator".equals(role);
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking admin status", e);
            return false;
        }
    }
    
    private void validateAndSaveUser() {
        try {
            // Get input values
            String username = etUsername.getText().toString().trim();
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();
            
            // Validate inputs
            if (username.isEmpty()) {
                etUsername.setError("Username is required");
                etUsername.requestFocus();
                return;
            }
            
            if (firstName.isEmpty()) {
                etFirstName.setError("First name is required");
                etFirstName.requestFocus();
                return;
            }
            
            if (lastName.isEmpty()) {
                etLastName.setError("Last name is required");
                etLastName.requestFocus();
                return;
            }
            
            if (password.isEmpty()) {
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
            
            // Show progress and save user
            showProgress(true);
            saveUser(username, firstName, lastName, email, password, role);
            
        } catch (Exception e) {
            Log.e(TAG, "Error validating user input", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveUser(String username, String firstName, String lastName, String email, String password, String role) {
        try {
            executorService.execute(() -> {
                try {
                    // Check if username already exists
                    UserEntity existingUser = dataRepository.getUserByUsernameSync(username);
                    if (existingUser != null) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            etUsername.setError("Username already exists");
                            etUsername.requestFocus();
                        });
                        return;
                    }
                    
                    // Create new user
                    UserEntity newUser = new UserEntity();
                    newUser.setId(UUID.randomUUID().toString());
                    newUser.setUsername(username);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setRole(role);
                    newUser.setCreatedAt(new Date());
                    newUser.setLastLogin(new Date());
                    
                    // Save to database
                    dataRepository.insertUser(newUser);
                    
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(AddUserActivity.this, 
                            "User added successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error saving user", e);
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(AddUserActivity.this, 
                            "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in saveUser", e);
            showProgress(false);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showProgress(boolean show) {
        try {
            if (progressBar != null) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            if (formLayout != null) {
                formLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
            if (btnSave != null) {
                btnSave.setEnabled(!show);
            }
            if (btnCancel != null) {
                btnCancel.setEnabled(!show);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding progress", e);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.d(TAG, "AddUserActivity destroyed");
    }
}
