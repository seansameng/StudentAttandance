package com.example.studentattandance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditUserActivity extends AppCompatActivity {
    
    private static final String TAG = "EditUserActivity";
    
    private EditText etUsername, etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private Button btnSave, btnCancel, btnEdit, btnDelete;
    private ProgressBar progressBar;
    private LinearLayout formLayout;
    private TextView tvCreatedDate, tvLastLogin;
    
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    
    private String userId;
    private String mode; // "view", "edit", or "create"
    private UserEntity currentUser;
    private boolean isEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        
        try {
            Log.d(TAG, "EditUserActivity onCreate started");
            
            // Get intent data
            userId = getIntent().getStringExtra("userId");
            mode = getIntent().getStringExtra("mode");
            
            if (mode == null) {
                mode = "view";
            }
            
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
            
            // Load user data if editing/viewing
            if ("view".equals(mode) || "edit".equals(mode)) {
                if (userId != null) {
                    loadUserData();
                } else {
                    Toast.makeText(this, "User ID not provided", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
            
            // Set initial mode
            setMode(mode);
            
            Log.d(TAG, "EditUserActivity onCreate completed successfully");
            
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
            btnEdit = findViewById(R.id.btn_edit);
            btnDelete = findViewById(R.id.btn_delete);
            progressBar = findViewById(R.id.progress_bar);
            formLayout = findViewById(R.id.form_layout);
            tvCreatedDate = findViewById(R.id.tv_created_date);
            tvLastLogin = findViewById(R.id.tv_last_login);
            
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
            btnEdit.setOnClickListener(v -> setEditMode());
            btnDelete.setOnClickListener(v -> showDeleteConfirmation());
            
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
    
    private void loadUserData() {
        try {
            showProgress(true);
            
            executorService.execute(() -> {
                try {
                    UserEntity user = dataRepository.getUserByIdSync(userId);
                    
                    if (user == null) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            Toast.makeText(EditUserActivity.this, 
                                "User not found", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                        return;
                    }
                    
                    currentUser = user;
                    
                    runOnUiThread(() -> {
                        showProgress(false);
                        populateUserData(user);
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error loading user data", e);
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(EditUserActivity.this, 
                            "Error loading user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in loadUserData", e);
            showProgress(false);
        }
    }
    
    private void populateUserData(UserEntity user) {
        try {
            if (user == null) return;
            
            etUsername.setText(user.getUsername());
            etFirstName.setText(user.getFirstName());
            etLastName.setText(user.getLastName());
            etEmail.setText(user.getEmail());
            
            // Set role spinner
            String role = user.getRole();
            if (role != null) {
                String[] roles = {"STUDENT", "TEACHER", "ADMIN"};
                for (int i = 0; i < roles.length; i++) {
                    if (roles[i].equals(role)) {
                        spinnerRole.setSelection(i);
                        break;
                    }
                }
            }
            
            // Set dates
            if (user.getCreatedAt() != null) {
                tvCreatedDate.setText("Created: " + user.getCreatedAt().toString());
            }
            if (user.getLastLogin() != null) {
                tvLastLogin.setText("Last Login: " + user.getLastLogin().toString());
            }
            
            Log.d(TAG, "User data populated successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error populating user data", e);
        }
    }
    
    private void setMode(String mode) {
        try {
            this.mode = mode;
            
            if ("view".equals(mode)) {
                setViewMode();
            } else if ("edit".equals(mode)) {
                setEditMode();
            }
            
            Log.d(TAG, "Mode set to: " + mode);
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting mode", e);
        }
    }
    
    private void setViewMode() {
        try {
            isEditMode = false;
            
            // Disable all input fields
            setInputFieldsEnabled(false);
            
            // Hide password fields
            etPassword.setVisibility(View.GONE);
            etConfirmPassword.setVisibility(View.GONE);
            
            // Show view buttons
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            
            // Update toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("View User");
            }
            
            Log.d(TAG, "View mode set successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting view mode", e);
        }
    }
    
    private void setEditMode() {
        try {
            isEditMode = true;
            
            // Enable all input fields
            setInputFieldsEnabled(true);
            
            // Show password fields
            etPassword.setVisibility(View.VISIBLE);
            etConfirmPassword.setVisibility(View.VISIBLE);
            
            // Hide view buttons
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            
            // Update toolbar title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit User");
            }
            
            Log.d(TAG, "Edit mode set successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting edit mode", e);
        }
    }
    
    private void setInputFieldsEnabled(boolean enabled) {
        try {
            etUsername.setEnabled(enabled);
            etFirstName.setEnabled(enabled);
            etLastName.setEnabled(enabled);
            etEmail.setEnabled(enabled);
            etPassword.setEnabled(enabled);
            etConfirmPassword.setEnabled(enabled);
            spinnerRole.setEnabled(enabled);
        } catch (Exception e) {
            Log.e(TAG, "Error setting input fields enabled state", e);
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
            
            // Only validate password if it's not empty (user wants to change it)
            if (!password.isEmpty()) {
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
                    // Check if username already exists (if changed)
                    if (!username.equals(currentUser.getUsername())) {
                        UserEntity existingUser = dataRepository.getUserByUsernameSync(username);
                        if (existingUser != null) {
                            runOnUiThread(() -> {
                                showProgress(false);
                                etUsername.setError("Username already exists");
                                etUsername.requestFocus();
                            });
                            return;
                        }
                    }
                    
                    // Update user data
                    currentUser.setUsername(username);
                    currentUser.setFirstName(firstName);
                    currentUser.setLastName(lastName);
                    currentUser.setEmail(email);
                    currentUser.setRole(role);
                    
                    // Update password only if provided
                    if (!password.isEmpty()) {
                        currentUser.setPassword(password);
                    }
                    
                    // Save to database
                    dataRepository.updateUser(currentUser);
                    
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(EditUserActivity.this, 
                            "User updated successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        setViewMode(); // Switch back to view mode
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error saving user", e);
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(EditUserActivity.this, 
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
    
    private void showDeleteConfirmation() {
        try {
            new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + currentUser.getFirstName() + " " + currentUser.getLastName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser())
                .setNegativeButton("Cancel", null)
                .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing delete confirmation", e);
        }
    }
    
    private void deleteUser() {
        try {
            showProgress(true);
            
            executorService.execute(() -> {
                try {
                    dataRepository.deleteUser(currentUser);
                    
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(EditUserActivity.this, 
                            "User deleted successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting user", e);
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(EditUserActivity.this, 
                            "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteUser", e);
            showProgress(false);
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
            if (btnEdit != null) {
                btnEdit.setEnabled(!show);
            }
            if (btnDelete != null) {
                btnDelete.setEnabled(!show);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding progress", e);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ("view".equals(mode)) {
            getMenuInflater().inflate(R.menu.menu_edit_user, menu);
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_edit) {
            setEditMode();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmation();
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
        Log.d(TAG, "EditUserActivity destroyed");
    }
}
