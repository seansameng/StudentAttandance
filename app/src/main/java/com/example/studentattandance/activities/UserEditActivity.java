package com.example.studentattandance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
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

public class UserEditActivity extends AppCompatActivity {
    
    private static final String TAG = "UserEditActivity";
    
    // UI Components
    private EditText etUsername, etPassword, etFirstName, etLastName, etEmail;
    private Spinner spinnerRole;
    private Button btnSave, btnCancel;
    
    // Data
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private String userId;
    private String mode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        
        try {
            // Initialize components
            initViews();
            setupToolbar();
            initializeData();
            setupClickListeners();
            loadUserData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing user edit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        spinnerRole = findViewById(R.id.spinner_role);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }
    
    private void initializeData() {
        try {
            sessionManager = SessionManager.getInstance(this);
            dataRepository = new DataRepository(this);
            executorService = Executors.newSingleThreadExecutor();
            
            // Verify admin access
            if (sessionManager == null || !sessionManager.isAdmin()) {
                Toast.makeText(this, "Access denied. Admin privileges required.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            // Get mode and user ID
            mode = getIntent().getStringExtra("mode");
            userId = getIntent().getStringExtra("userId");
            
            if (mode == null) {
                mode = "create";
            }
            
            // Set title based on mode
            if (getSupportActionBar() != null) {
                if ("create".equals(mode)) {
                    getSupportActionBar().setTitle("Add New User");
                } else {
                    getSupportActionBar().setTitle("Edit User");
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing data", e);
            Toast.makeText(this, "Error initializing user edit", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveUser());
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void loadUserData() {
        if ("edit".equals(mode) && userId != null) {
            executorService.execute(() -> {
                try {
                    UserEntity userEntity = dataRepository.getUserByIdSync(userId);
                    
                    if (userEntity != null) {
                        runOnUiThread(() -> {
                            populateForm(userEntity);
                        });
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error loading user data", e);
                    runOnUiThread(() -> {
                        Toast.makeText(UserEditActivity.this, 
                                     "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
    
    private void populateForm(UserEntity userEntity) {
        if (userEntity.getUsername() != null) {
            etUsername.setText(userEntity.getUsername());
        }
        
        if (userEntity.getFirstName() != null) {
            etFirstName.setText(userEntity.getFirstName());
        }
        
        if (userEntity.getLastName() != null) {
            etLastName.setText(userEntity.getLastName());
        }
        
        if (userEntity.getEmail() != null) {
            etEmail.setText(userEntity.getEmail());
        }
        
        // Note: Don't populate password for security
        // Role will be handled by spinner
    }
    
    private void saveUser() {
        // Validate input
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            return;
        }
        
        if ("create".equals(mode) && password.isEmpty()) {
            etPassword.setError("Password is required for new users");
            return;
        }
        
        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            return;
        }
        
        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            return;
        }
        
        // Create or update user
        executorService.execute(() -> {
            try {
                UserEntity userEntity;
                
                if ("create".equals(mode)) {
                    // Create new user
                    userEntity = new UserEntity();
                    userEntity.setId(UUID.randomUUID().toString());
                    userEntity.setCreatedAt(new Date(System.currentTimeMillis()));
                    userEntity.setPassword(password); // In production, hash this
                } else {
                    // Edit existing user
                    userEntity = dataRepository.getUserByIdSync(userId);
                    if (userEntity == null) {
                        throw new Exception("User not found");
                    }
                    // Only update password if provided
                    if (!password.isEmpty()) {
                        userEntity.setPassword(password); // In production, hash this
                    }
                }
                
                // Update fields
                userEntity.setUsername(username);
                userEntity.setFirstName(firstName);
                userEntity.setLastName(lastName);
                userEntity.setEmail(email);
                userEntity.setRole(role);
                // Note: UserEntity doesn't have updatedAt field, only createdAt and lastLogin
                
                // Save to database
                if ("create".equals(mode)) {
                    dataRepository.insertUser(userEntity);
                } else {
                    dataRepository.updateUser(userEntity);
                }
                
                runOnUiThread(() -> {
                    String message = "create".equals(mode) ? "User created successfully" : "User updated successfully";
                    Toast.makeText(UserEditActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error saving user", e);
                runOnUiThread(() -> {
                    Toast.makeText(UserEditActivity.this, 
                                 "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
