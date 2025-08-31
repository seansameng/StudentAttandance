package com.example.studentattandance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.studentattandance.R;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.utils.NavigationHelper;
import com.example.studentattandance.MainActivity;
import com.example.studentattandance.database.DatabaseConnectionManager;


public class WelcomeActivity extends AppCompatActivity {
    
    private ImageView ivLogo;
    private TextView tvWelcomeTitle;
    private TextView tvWelcomeSubtitle;
    private CardView cvLogin;
    private CardView cvRegister;
    private Button btnLogin;
    private Button btnRegister;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d("WelcomeActivity", "Starting onCreate...");
            
            // TEMPORARILY DISABLED: Always show welcome screen for testing
            // Check session BEFORE setting content view
            /*
            SessionManager sessionManager = SessionManager.getInstance(this);
            Log.d("WelcomeActivity", "SessionManager created: " + (sessionManager != null));
            
            if (sessionManager != null && sessionManager.isLoggedIn()) {
                Log.d("WelcomeActivity", "User already logged in, navigating to MainActivity");
                navigateToMain();
                return;
            }
            */
            
            Log.d("WelcomeActivity", "Showing welcome screen (session check disabled)");
            
            // Always show welcome screen for now
            setContentView(R.layout.activity_welcome);
            Log.d("WelcomeActivity", "Content view set successfully");
            
            initViews();
            Log.d("WelcomeActivity", "Views initialized successfully");
            
            setupAnimations();
            Log.d("WelcomeActivity", "Animations setup successfully");
            
            setupClickListeners();
            Log.d("WelcomeActivity", "Click listeners setup successfully");
            
            // Initialize database connection and sample data
            initializeDatabase();
            
            Log.d("WelcomeActivity", "onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e("WelcomeActivity", "Critical error in onCreate", e);
            e.printStackTrace();
            
            try {
                // Show welcome screen anyway
                setContentView(R.layout.activity_welcome);
                initViews();
                setupAnimations();
                setupClickListeners();
            } catch (Exception fallbackError) {
                Log.e("WelcomeActivity", "Fallback also failed", fallbackError);
                fallbackError.printStackTrace();
                
                // Last resort - show simple text view
                TextView fallbackView = new TextView(this);
                fallbackView.setText("Welcome to Student Attendance");
                fallbackView.setTextSize(24);
                fallbackView.setGravity(android.view.Gravity.CENTER);
                setContentView(fallbackView);
            }
        }
    }
    
    private void initViews() {
        ivLogo = findViewById(R.id.iv_logo);
        tvWelcomeTitle = findViewById(R.id.tv_welcome_title);
        tvWelcomeSubtitle = findViewById(R.id.tv_welcome_subtitle);
        cvLogin = findViewById(R.id.cv_login);
        cvRegister = findViewById(R.id.cv_register);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }
    
    private void setupAnimations() {
        // Logo animation
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        ivLogo.startAnimation(logoAnimation);
        
        // Title animation
        Animation titleAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        titleAnimation.setStartOffset(300);
        tvWelcomeTitle.startAnimation(titleAnimation);
        
        // Subtitle animation
        Animation subtitleAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        subtitleAnimation.setStartOffset(600);
        tvWelcomeSubtitle.startAnimation(subtitleAnimation);
        
        // Cards animation
        Animation cardsAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        cardsAnimation.setStartOffset(900);
        cvLogin.startAnimation(cardsAnimation);
        cvRegister.startAnimation(cardsAnimation);
    }
    
    private void setupClickListeners() {
        NavigationHelper navigationHelper = NavigationHelper.getInstance(this);
        
        btnLogin.setOnClickListener(v -> {
            navigationHelper.goToLogin();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        btnRegister.setOnClickListener(v -> {
            navigationHelper.goToRegister();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        

        
        // Add double tap on logo to clear session (for testing)
        ivLogo.setOnTouchListener(new View.OnTouchListener() {
            private long lastTouchTime = 0;
            private static final long DOUBLE_TAP_TIME_DELTA = 300;
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTouchTime < DOUBLE_TAP_TIME_DELTA) {
                        // Double tap detected - clear session and database
                        clearSession();
                        clearDatabase();
                        Toast.makeText(WelcomeActivity.this, "Session and database cleared!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    lastTouchTime = currentTime;
                }
                return false;
            }
        });
    }
    
    private void clearSession() {
        try {
            SessionManager sessionManager = SessionManager.getInstance(this);
            if (sessionManager != null) {
                sessionManager.logout();
                Log.d("WelcomeActivity", "Session cleared successfully");
            }
        } catch (Exception e) {
            Log.e("WelcomeActivity", "Error clearing session", e);
        }
    }
    
    private void clearDatabase() {
        try {
            DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance(this);
            dbManager.clearAllData(new DatabaseConnectionManager.DatabaseClearCallback() {
                @Override
                public void onClearSuccess(String message) {
                    Log.d("WelcomeActivity", "Database cleared: " + message);
                    runOnUiThread(() -> {
                        Toast.makeText(WelcomeActivity.this, "Database cleared successfully", Toast.LENGTH_SHORT).show();
                    });
                }
                
                @Override
                public void onClearFailed(String error) {
                    Log.e("WelcomeActivity", "Failed to clear database: " + error);
                    runOnUiThread(() -> {
                        Toast.makeText(WelcomeActivity.this, "Failed to clear database: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Log.e("WelcomeActivity", "Error clearing database", e);
        }
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    

    
    /**
     * Initialize database connection and sample data
     */
    private void initializeDatabase() {
        try {
            Log.d("WelcomeActivity", "Initializing database...");
            
            DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance(this);
            
            // First test the database connection
            dbManager.testDatabaseConnection(new DatabaseConnectionManager.DatabaseConnectionCallback() {
                @Override
                public void onConnectionSuccess(String message) {
                    Log.d("WelcomeActivity", "Database connection successful: " + message);
                    
                    // Now initialize with sample data if needed
                    dbManager.initializeDatabaseIfNeeded(new DatabaseConnectionManager.DatabaseInitCallback() {
                        @Override
                        public void onInitSuccess(String message) {
                            Log.d("WelcomeActivity", "Database initialization successful: " + message);
                            
                            // Get database stats to confirm data is there
                            dbManager.getDatabaseStats(new DatabaseConnectionManager.DatabaseStatsCallback() {
                                @Override
                                public void onStatsReceived(int userCount, int classCount, int attendanceCount, int enrollmentCount) {
                                    Log.d("WelcomeActivity", String.format("Database stats - Users: %d, Classes: %d, Attendance: %d, Enrollments: %d", 
                                        userCount, classCount, attendanceCount, enrollmentCount));
                                    
                                    runOnUiThread(() -> {
                                        Toast.makeText(WelcomeActivity.this, 
                                            "Database ready with " + userCount + " users, " + classCount + " classes", 
                                            Toast.LENGTH_SHORT).show();
                                    });
                                    

                                }
                                
                                @Override
                                public void onStatsError(String error) {
                                    Log.e("WelcomeActivity", "Error getting database stats: " + error);
                                }
                            });
                        }
                        
                        @Override
                        public void onInitFailed(String error) {
                            Log.e("WelcomeActivity", "Database initialization failed: " + error);
                            runOnUiThread(() -> {
                                Toast.makeText(WelcomeActivity.this, "Database initialization failed: " + error, Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                }
                
                @Override
                public void onConnectionFailed(String error) {
                    Log.e("WelcomeActivity", "Database connection failed: " + error);
                    runOnUiThread(() -> {
                        Toast.makeText(WelcomeActivity.this, "Database connection failed: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e("WelcomeActivity", "Error initializing database", e);
        }
    }
    

    
//    @Override
//    public void onBackPressed() {
//        // Exit app when back is pressed from welcome screen
//        finishAffinity();
//    }
}
