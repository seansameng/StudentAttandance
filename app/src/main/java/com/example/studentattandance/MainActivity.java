package com.example.studentattandance;

import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.studentattandance.fragments.DashboardFragment;
import com.example.studentattandance.fragments.AttendanceFragment;
import com.example.studentattandance.fragments.ClassesFragment;
import com.example.studentattandance.fragments.ReportsFragment;
import com.example.studentattandance.fragments.AdminFragment;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.activities.WelcomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    
    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "MainActivity onCreate started");
            
            // Set the layout
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout set successfully");
            
            // Initialize SessionManager
            sessionManager = SessionManager.getInstance(this);
            if (sessionManager == null) {
                Log.e(TAG, "SessionManager is null, redirecting to welcome");
                redirectToWelcome();
                return;
            }
            
            // Check if user is logged in
            if (!sessionManager.isLoggedIn()) {
                Log.w(TAG, "User not logged in, redirecting to welcome");
                redirectToWelcome();
                return;
            }
            
            // Initialize views
            initViews();
            
            // Setup bottom navigation
            setupBottomNavigation();
            
            // Load default fragment
            loadDefaultFragment();
            
            Log.d(TAG, "MainActivity onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing MainActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
            redirectToWelcome();
        }
    }
    
    private void initViews() {
        try {
            Log.d(TAG, "Initializing views");
            
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            if (bottomNavigationView == null) {
                Log.e(TAG, "Bottom navigation view not found");
                throw new RuntimeException("Bottom navigation view not found");
            }
            
            Log.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw e;
        }
    }
    
    private void setupBottomNavigation() {
        try {
            Log.d(TAG, "Setting up bottom navigation");
            
            bottomNavigationView.setOnItemSelectedListener(this);
            bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
            
            Log.d(TAG, "Bottom navigation setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation", e);
            throw e;
        }
    }
    
    private void loadDefaultFragment() {
        try {
            Log.d(TAG, "Loading default fragment");
            
            // Load DashboardFragment by default
            Fragment fragment = new DashboardFragment();
            loadFragment(fragment);
            
            Log.d(TAG, "Default fragment loaded successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading default fragment", e);
            // Show error message
            Toast.makeText(this, "Error loading dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void loadFragment(Fragment fragment) {
        try {
            Log.d(TAG, "Loading fragment: " + fragment.getClass().getSimpleName());
            
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager == null) {
                Log.e(TAG, "Fragment manager is null");
                return;
            }
            
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            
            Log.d(TAG, "Fragment loaded successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment", e);
            Toast.makeText(this, "Error loading fragment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            int itemId = item.getItemId();
            Fragment fragment = null;
            
            if (itemId == R.id.nav_dashboard) {
                Log.d(TAG, "Dashboard selected");
                fragment = new DashboardFragment();
            } else if (itemId == R.id.nav_attendance) {
                Log.d(TAG, "Attendance selected");
                fragment = new AttendanceFragment();
            } else if (itemId == R.id.nav_reports) {
                Log.d(TAG, "Reports selected");
                fragment = new ReportsFragment();
            } else if (itemId == R.id.nav_admin) {
                Log.d(TAG, "Admin selected");
                fragment = new AdminFragment();
            }
            
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "Error in navigation item selection", e);
            Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        
        // Don't clear session on pause - this can cause crashes
        // Only clear session when explicitly logging out
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        
        // Don't clear session on destroy - this can cause crashes
        // Only clear session when explicitly logging out
    }
    
    private void redirectToWelcome() {
        try {
            Log.d(TAG, "Redirecting to WelcomeActivity");
            
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "Error redirecting to welcome", e);
            finish();
        }
    }
}
