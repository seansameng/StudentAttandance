package com.example.studentattandance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentattandance.R;
import com.example.studentattandance.MainActivity;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.AppDatabase;
import com.example.studentattandance.database.dao.UserDao;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.DatabaseSeeder;
import com.example.studentattandance.database.DatabaseInitializer;
import com.example.studentattandance.utils.DatabaseTester;
import com.example.studentattandance.utils.DatabaseResetHelper;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.utils.NavigationHelper;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.example.studentattandance.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvForgotPassword;
    private TextView tvBackToWelcome;
    
    private DataRepository dataRepository;
    private DatabaseSeeder databaseSeeder;
    private SessionManager sessionManager;
    
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LoginActivity onCreate started");
        setContentView(R.layout.activity_login);
        
        try {
            // Initialize ExecutorService for background operations
            executorService = Executors.newSingleThreadExecutor();
            
            initViews();
            setupClickListeners();
            
            // Initialize SessionManager first (most critical)
            try {
                sessionManager = SessionManager.getInstance(this);
                Log.d(TAG, "SessionManager initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize SessionManager", e);
                Toast.makeText(this, "Critical error: Cannot initialize session manager", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Initialize DataRepository (less critical)
            try {
                dataRepository = new DataRepository(this);
                Log.d(TAG, "DataRepository initialized successfully");
                
                // Test database connection first
                testDatabaseConnection();
                
                // Quick database status check
                new Thread(() -> {
                    boolean dbWorking = isDatabaseWorking();
                    runOnUiThread(() -> {
                        if (dbWorking) {
                            Log.d(TAG, "✅ Database is working and ready");
                        } else {
                            Log.e(TAG, "❌ Database is NOT working");
                            handleDatabaseFailure();
                        }
                    });
                }).start();
                
                // Ensure database is ready by checking if users exist
                ensureDatabaseReady();
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize DataRepository", e);
                // Continue without database functionality
            }
            
            // Initialize DatabaseSeeder (least critical)
            try {
                databaseSeeder = new DatabaseSeeder(this);
                Log.d(TAG, "DatabaseSeeder initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize DatabaseSeeder", e);
                // Continue without seeding functionality
            }
            
            Log.d(TAG, "LoginActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Critical error in LoginActivity onCreate", e);
            Toast.makeText(this, "Critical error initializing login screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initViews() {
        Log.d(TAG, "Initializing views");
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvBackToWelcome = findViewById(R.id.tv_back_to_welcome);
        
        // Check if views were found successfully
        if (etUsername == null) Log.e(TAG, "etUsername not found!");
        if (etPassword == null) Log.e(TAG, "etPassword not found!");
        if (btnLogin == null) Log.e(TAG, "btnLogin not found!");
        if (progressBar == null) Log.e(TAG, "progressBar not found!");
        
        Log.d(TAG, "Views initialized successfully");
    }
    
    private void setupClickListeners() {
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                Log.d(TAG, "Login button clicked");
                attemptLogin();
            });
        } else {
            Log.e(TAG, "Login button is null - cannot set click listener");
        }
        
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                // TODO: Implement forgot password functionality
                Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }
        
        if (tvBackToWelcome != null) {
            tvBackToWelcome.setOnClickListener(v -> {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        }
    }
    
    private void attemptLogin() {
        try {
            Log.d(TAG, "Login attempt started");
            
            // Check if views are properly initialized
            if (etUsername == null || etPassword == null || btnLogin == null) {
                Log.e(TAG, "Login views not properly initialized");
                Toast.makeText(this, "Login form error. Please restart the app.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Reset errors
            etUsername.setError(null);
            etPassword.setError(null);
            
            // Get values from input fields
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            Log.d(TAG, "Username: " + username + ", Password length: " + password.length());
            
            // Check for valid input
            if (TextUtils.isEmpty(username)) {
                Log.d(TAG, "Username is empty");
                etUsername.setError("Username is required");
                etUsername.requestFocus();
                return;
            }
            
            if (TextUtils.isEmpty(password)) {
                Log.d(TAG, "Password is empty");
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }
            
            if (password.length() < 6) {
                Log.d(TAG, "Password too short");
                etPassword.setError("Password must be at least 6 characters");
                etPassword.requestFocus();
                return;
            }
            
            // Show progress and disable login button
            Log.d(TAG, "Showing progress and starting login");
            showProgress(true);
            
            // Try database login first, fallback to simple login if database is not ready
            performDatabaseLogin(username, password);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in attemptLogin", e);
            showProgress(false);
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void showProgress(boolean show) {
        try {
            if (progressBar != null) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            if (btnLogin != null) {
                btnLogin.setEnabled(!show);
            }
            if (etUsername != null) {
                etUsername.setEnabled(!show);
            }
            if (etPassword != null) {
                etPassword.setEnabled(!show);
            }
            Log.d(TAG, "Progress state set to: " + show);
        } catch (Exception e) {
            Log.e(TAG, "Error in showProgress", e);
        }
    }
    
    private void hideLoginForm() {
        try {
            // Hide the login form to prevent any further interaction
            View loginForm = findViewById(R.id.cv_login_form);
            if (loginForm != null) {
                loginForm.setVisibility(View.GONE);
                Log.d(TAG, "Login form hidden successfully");
            } else {
                Log.w(TAG, "Login form view not found");
            }
            
            // Show progress bar
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "Progress bar shown");
            } else {
                Log.w(TAG, "Progress bar is null");
            }
            
            // Show a loading message in the welcome text
            TextView welcomeText = findViewById(R.id.tv_welcome);
            if (welcomeText != null) {
                welcomeText.setText("Logging you in...");
                Log.d(TAG, "Welcome text updated");
            } else {
                Log.w(TAG, "Welcome text view not found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in hideLoginForm", e);
        }
    }
    
    private void navigateToMain() {
        Log.d(TAG, "Navigating to MainActivity");
        try {
            NavigationHelper navigationHelper = NavigationHelper.getInstance(this);
            navigationHelper.goToMainAfterLogin();
            Log.d(TAG, "MainActivity started successfully");
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to navigate to MainActivity", e);
            Toast.makeText(this, "Failed to navigate to main screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void testDatabaseConnection() {
        Log.d(TAG, "=== DATABASE CONNECTION TEST START ===");
        
        new Thread(() -> {
            try {
                // Test if we can access the database
                Log.d(TAG, "1. Creating database instance...");
                AppDatabase database = AppDatabase.getInstance(this);
                Log.d(TAG, "   Database instance obtained: " + (database != null ? "SUCCESS" : "FAILED"));
                
                if (database != null) {
                    Log.d(TAG, "2. Checking database status...");
                    boolean isOpen = database.isOpen();
                    Log.d(TAG, "   Database is open: " + isOpen);
                    
                    // Get database path and details
                    try {
                        String dbPath = database.getOpenHelper().getDatabaseName();
                        Log.d(TAG, "   Database name: " + dbPath);
                    } catch (Exception e) {
                        Log.e(TAG, "   Error getting database path: " + e.getMessage());
                    }
                    
                    // Try to access a DAO
                    Log.d(TAG, "3. Testing DAO access...");
                    try {
                        UserDao userDao = database.userDao();
                        Log.d(TAG, "   UserDao accessed successfully: " + (userDao != null ? "SUCCESS" : "FAILED"));
                        
                        if (userDao != null) {
                            // Test a simple query
                            try {
                                List<UserEntity> users = userDao.getAllUsersSync();
                                int userCount = users != null ? users.size() : 0;
                                Log.d(TAG, "   User count query: SUCCESS - Count: " + userCount);
                            } catch (Exception e) {
                                Log.e(TAG, "   User count query: FAILED - " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "   Error accessing UserDao: " + e.getMessage());
                    }
                    
                    // Test SQLite database access
                    Log.d(TAG, "4. Testing SQLite access...");
                    try {
                        androidx.sqlite.db.SupportSQLiteDatabase sqliteDb = database.getOpenHelper().getWritableDatabase();
                        if (sqliteDb != null) {
                            Log.d(TAG, "   SQLite database access: SUCCESS");
                            Log.d(TAG, "   SQLite version: " + sqliteDb.getVersion());
                            Log.d(TAG, "   SQLite path: " + sqliteDb.getPath());
                            Log.d(TAG, "   SQLite is read-only: " + sqliteDb.isReadOnly());
                        } else {
                            Log.e(TAG, "   SQLite database access: FAILED - null database");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "   SQLite database access: FAILED - " + e.getMessage());
                    }
                    
                    // Final status
                    Log.d(TAG, "5. Final database status:");
                    Log.d(TAG, "   Database instance: " + (database != null ? "EXISTS" : "NULL"));
                    Log.d(TAG, "   Database open: " + database.isOpen());
                    Log.d(TAG, "   Database ready: " + (database.isOpen() ? "YES" : "NO"));
                    
                } else {
                    Log.d(TAG, "2. Database instance is NULL - cannot proceed with tests");
                }
                
                Log.d(TAG, "=== DATABASE CONNECTION TEST END ===");
                
            } catch (Exception e) {
                Log.e(TAG, "=== DATABASE CONNECTION TEST FAILED ===", e);
                Log.e(TAG, "Error details: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
    
    // Quick database status check
    private boolean isDatabaseWorking() {
        // Use the new DatabaseTester utility
        return DatabaseTester.isDatabaseWorking(this);
    }
    
    private void ensureDatabaseReady() {
        try {
            Log.d(TAG, "Ensuring database is ready...");
            
            executorService.execute(() -> {
                try {
                    // Check if database has data
                    List<UserEntity> existingUsers = dataRepository.getAllUsersSync();
                    List<ClassEntity> existingClasses = dataRepository.getAllClassesSync();
                    
                    Log.d(TAG, "Database check - Users: " + (existingUsers != null ? existingUsers.size() : 0) + 
                          ", Classes: " + (existingClasses != null ? existingClasses.size() : 0));
                    
                    if ((existingUsers == null || existingUsers.isEmpty()) && 
                        (existingClasses == null || existingClasses.isEmpty())) {
                        
                        Log.d(TAG, "Database is empty, seeding with comprehensive data...");
                        
                        if (databaseSeeder != null) {
                            Log.d(TAG, "Using DatabaseSeeder to populate database...");
                            databaseSeeder.seedAllData();
                            
                            // Wait a bit for seeding to complete
                            Thread.sleep(1000);
                            
                            // Verify seeding was successful
                            List<UserEntity> seededUsers = dataRepository.getAllUsersSync();
                            List<ClassEntity> seededClasses = dataRepository.getAllClassesSync();
                            
                            Log.d(TAG, "After seeding - Users: " + (seededUsers != null ? seededUsers.size() : 0) + 
                                  ", Classes: " + (seededClasses != null ? seededClasses.size() : 0));
                            
                            runOnUiThread(() -> {
                                if (seededUsers != null && !seededUsers.isEmpty()) {
                                    Toast.makeText(this, 
                                        "Database seeded successfully with " + seededUsers.size() + " users and " + 
                                        (seededClasses != null ? seededClasses.size() : 0) + " classes", 
                                        Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, 
                                        "Database seeding may not have completed. Please try again.", 
                                        Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Log.w(TAG, "DatabaseSeeder is null, falling back to basic sample data...");
                            createSampleUsers();
                        }
                    } else {
                        Log.d(TAG, "Database already has data, no seeding needed");
                        runOnUiThread(() -> {
                            Toast.makeText(this, 
                                "Database ready with existing data", Toast.LENGTH_SHORT).show();
                        });
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error ensuring database is ready", e);
                    runOnUiThread(() -> {
                        Toast.makeText(this, 
                            "Error preparing database: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in ensureDatabaseReady", e);
        }
    }
    
    private void createSampleUsers() {
        Log.d(TAG, "Creating sample users for login...");
        
        try {
            // Create admin user
            UserEntity adminUser = new UserEntity();
            adminUser.setId(UUID.randomUUID().toString());
            adminUser.setUsername("admin");
            adminUser.setPassword("123456");
            adminUser.setEmail("admin@test.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setRole("ADMIN");
            adminUser.setCreatedAt(new Date());
            adminUser.setLastLogin(new Date());
            dataRepository.insertUser(adminUser);
            Log.d(TAG, "Created admin user: admin/123456");
            
            // Create teacher user
            UserEntity teacherUser = new UserEntity();
            teacherUser.setId(UUID.randomUUID().toString());
            teacherUser.setUsername("teacher");
            teacherUser.setPassword("123456");
            teacherUser.setEmail("teacher@test.com");
            teacherUser.setFirstName("Teacher");
            teacherUser.setLastName("User");
            teacherUser.setRole("TEACHER");
            teacherUser.setCreatedAt(new Date());
            teacherUser.setLastLogin(new Date());
            dataRepository.insertUser(teacherUser);
            Log.d(TAG, "Created teacher user: teacher/123456");
            
            // Create student user
            UserEntity studentUser = new UserEntity();
            studentUser.setId(UUID.randomUUID().toString());
            studentUser.setUsername("student");
            studentUser.setPassword("123456");
            studentUser.setEmail("student@test.com");
            studentUser.setFirstName("Student");
            studentUser.setLastName("User");
            studentUser.setRole("STUDENT");
            studentUser.setCreatedAt(new Date());
            studentUser.setLastLogin(new Date());
            dataRepository.insertUser(studentUser);
            Log.d(TAG, "Created student user: student/123456");
            
            // Create sample class
            createSampleClass();
            
            Log.d(TAG, "Sample users and class created successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating sample users", e);
        }
    }
    
    private void createSampleClass() {
        try {
            Log.d(TAG, "Creating sample class...");
            
            // Get the teacher user ID first
            UserEntity teacher = dataRepository.getUserByUsernameSync("teacher");
            if (teacher != null) {
                ClassEntity sampleClass = new ClassEntity();
                sampleClass.setId(UUID.randomUUID().toString());
                sampleClass.setClassName("Mathematics 101");
                sampleClass.setSubject("Mathematics");
                sampleClass.setTeacherId(teacher.getId());
                sampleClass.setSchedule("Monday, Wednesday, Friday 9:00 AM");
                sampleClass.setRoom("Room 101");
                sampleClass.setCreatedAt(new Date());
                
                dataRepository.insertClass(sampleClass);
                Log.d(TAG, "Created sample class: Mathematics 101");
            } else {
                Log.w(TAG, "Teacher user not found, cannot create sample class");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating sample class", e);
        }
    }
    
    private void createDemoUser() {
        // Create a demo user for testing purposes
        UserEntity demoUser = new UserEntity();
        demoUser.setId(UUID.randomUUID().toString());
        demoUser.setUsername("demo");
        demoUser.setEmail("demo@example.com");
        demoUser.setFirstName("Demo");
        demoUser.setLastName("User");
        demoUser.setRole("STUDENT");
        demoUser.setCreatedAt(new Date());
        demoUser.setLastLogin(new Date());
        
        dataRepository.insertUser(demoUser);
    }
    
    // Try database login first, fallback to simple login if database is not ready
    private void performDatabaseLogin(String username, String password) {
        Log.d(TAG, "Attempting database login for: " + username);
        
        if (dataRepository == null) {
            Log.w(TAG, "DataRepository not available, falling back to simple login");
            performSimpleLogin(username, password);
            return;
        }
        
        // Execute database operation on background thread
        new Thread(() -> {
            try {
                UserEntity user = dataRepository.authenticateUser(username, password);
                
                // Switch back to main thread for UI updates
                runOnUiThread(() -> {
                    if (user != null) {
                        Log.d(TAG, "Database login successful for: " + username);
                        handleSuccessfulLogin(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
                    } else {
                        Log.d(TAG, "Database login failed for: " + username + ", trying simple login");
                        performSimpleLogin(username, password);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Database login error, falling back to simple login", e);
                runOnUiThread(() -> performSimpleLogin(username, password));
            }
        }).start();
    }
    
    // Simple test login method that bypasses database operations
    private void performSimpleLogin(String username, String password) {
        Log.d(TAG, "Performing local database login for: " + username);
        
        // Check for hardcoded test credentials first
        if ("admin".equals(username) && "123456".equals(password)) {
            handleSuccessfulLogin("admin", "admin@test.com", "Admin", "User", "ADMIN");
            return;
        }
        
        if ("teacher".equals(username) && "123456".equals(password)) {
            handleSuccessfulLogin("teacher", "teacher@test.com", "Teacher", "User", "TEACHER");
            return;
        }
        
        if ("student".equals(username) && "123456".equals(password)) {
            handleSuccessfulLogin("student", "student@test.com", "Student", "User", "STUDENT");
            return;
        }
        
        // If we get here, login failed
        showProgress(false);
        Toast.makeText(this, "Login failed. Use admin/123456, teacher/123456, or student/123456 for testing.", Toast.LENGTH_LONG).show();
    }
    
    private void handleSuccessfulLogin(String username, String email, String firstName, String lastName, String role) {
        Log.d(TAG, "Handling successful login for: " + username + " with role: " + role);
        showProgress(false);
        
        try {
            // Create user object with proper role
            User user = new User();
            user.setId("test_" + username + "_id");
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
            
            // Create local database session
            sessionManager.createLocalLoginSession(user);
            Log.d(TAG, "Session created successfully for role: " + role);
            
            // Debug: Check session immediately after creation
            Log.d(TAG, "DEBUG: Session isLoggedIn: " + sessionManager.isLoggedIn());
            Log.d(TAG, "DEBUG: Session getUserId: " + sessionManager.getUserId());
            Log.d(TAG, "DEBUG: Session getUserRole: " + sessionManager.getUserRole());
            Log.d(TAG, "DEBUG: Session getUser: " + (sessionManager.getUser() != null ? sessionManager.getUser().getUsername() : "null"));
            
            // Verify and proceed
            if (sessionManager.isLoggedIn()) {
                hideLoginForm();
                Toast.makeText(this, "Login successful! Welcome " + username + " (" + role + ")", Toast.LENGTH_SHORT).show();
                
                Log.d(TAG, "DEBUG: About to navigate to MainActivity");
                
                // Simple navigation without complex flags
                try {
                    Log.d(TAG, "DEBUG: Using simple navigation");
                    Intent intent = new Intent(this, MainActivity.class);
                    
                    Log.d(TAG, "DEBUG: Starting MainActivity");
                    startActivity(intent);
                    Log.d(TAG, "DEBUG: Navigation successful");
                    
                    // Finish this activity
                    finish();
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to MainActivity", e);
                    Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e(TAG, "Session creation failed - user not logged in");
                Toast.makeText(this, "Login failed: Session not created", Toast.LENGTH_LONG).show();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in handleSuccessfulLogin", e);
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void navigateBasedOnRole(String role) {
        Log.d(TAG, "Navigating based on role: " + role + " - All users now go to MainActivity");
        
        try {
            // All users now go to MainActivity where they see role-based content
            // The DashboardFragment will handle showing appropriate content based on user role
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to MainActivity", e);
            // Fallback to MainActivity
            navigateToMain();
        }
    }
    
    private void seedSampleData() {
        // Seed the database with sample data in the background
        new Thread(() -> {
            databaseSeeder.seedAllData();
        }).start();
    }

    private void handleDatabaseFailure() {
        Log.w(TAG, "Database is not working, showing fallback message");
        runOnUiThread(() -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Database Connection Failed")
                .setMessage("The database connection failed. You can:\n\n" +
                           "1. Use simple login (admin/123456, teacher/123456, student/123456)\n" +
                           "2. Try to reset the database\n\n" +
                           "What would you like to do?")
                .setPositiveButton("Use Simple Login", (dialog, which) -> {
                    Toast.makeText(this, "Use admin/123456, teacher/123456, or student/123456 to login", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Reset Database", (dialog, which) -> {
                    resetDatabaseSafely();
                })
                .setNeutralButton("Cancel", null)
                .show();
        });
    }
    
    private void resetDatabaseSafely() {
        Log.d(TAG, "Attempting to reset database safely...");
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Reset Database")
            .setMessage("This will reset the database and may clear all data. Continue?")
            .setPositiveButton("Yes, Reset", (dialog, which) -> {
                executorService.execute(() -> {
                    try {
                        // Close existing database
                        if (dataRepository != null) {
                            dataRepository = null;
                        }
                        
                        // Reset database instance
                        AppDatabase.resetDatabase();
                        
                        // Wait a bit
                        Thread.sleep(1000);
                        
                        // Try to recreate
                        try {
                            dataRepository = new DataRepository(this);
                            Log.d(TAG, "Database reset and recreation successful");
                            
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Database reset successful. Please restart the app.", Toast.LENGTH_LONG).show();
                            });
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Database recreation failed", e);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Database reset failed: " + e.getMessage() + "\nSimple login will work.", Toast.LENGTH_LONG).show();
                            });
                        }
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error during database reset", e);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Database reset error: " + e.getMessage() + "\nSimple login will work.", Toast.LENGTH_LONG).show();
                        });
                    }
                });
            })
            .setNegativeButton("No, Cancel", null)
            .show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LoginActivity destroyed");
        
        // Clean up executor service
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
//    @Override
//    public void onBackPressed() {
//        finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }
}
 