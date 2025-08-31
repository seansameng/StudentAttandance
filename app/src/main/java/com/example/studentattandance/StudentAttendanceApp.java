package com.example.studentattandance;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.studentattandance.database.AppDatabase;
import com.example.studentattandance.utils.SessionManager;

public class StudentAttendanceApp extends Application {
    
    private static final String TAG = "StudentAttendanceApp";
    private static Context appContext;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate started");
        
        try {
            // Store application context for global access
            appContext = getApplicationContext();
            
            // Initialize database instance to ensure it's created
            try {
                Log.d(TAG, "Initializing database instance...");
                AppDatabase database = AppDatabase.getInstance(this);
                Log.d(TAG, "Database instance initialized successfully");
                
                // Test database access
                if (database != null && database.isOpen()) {
                    Log.d(TAG, "Database is open and accessible");
                } else {
                    Log.w(TAG, "Database may not be fully initialized yet");
                }
                
            } catch (Exception dbError) {
                Log.e(TAG, "Error initializing database in Application", dbError);
                // Don't crash the app, just log the error
            }
            
            Log.d(TAG, "Application onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Critical error during application initialization", e);
            e.printStackTrace();
        }
    }
    
    public static Context getAppContext() {
        return appContext;
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "Application terminating");
        
        try {
            // Clear user session when app is closed
            clearUserSession();
            
            // Close database connection
            AppDatabase.closeDatabase();
            Log.d(TAG, "Database connection closed and session cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error during app termination", e);
        }
    }
    
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        
        // Clear session when app is trimmed (going to background)
        if (level >= TRIM_MEMORY_UI_HIDDEN) {
            Log.d(TAG, "App going to background, clearing session");
            clearUserSession();
        }
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "Low memory detected, clearing session");
        clearUserSession();
    }
    
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "Configuration changed");
        
        // Don't clear session on configuration change (rotation, etc.)
        // Only clear if it's a critical change
    }
    
    private void clearUserSession() {
        try {
            // Get SessionManager instance and clear the session
            SessionManager sessionManager = SessionManager.getInstance(this);
            if (sessionManager != null) {
                boolean cleared = sessionManager.performCompleteLogout();
                if (cleared) {
                    Log.d(TAG, "User session successfully cleared on app lifecycle event");
                } else {
                    Log.w(TAG, "Session clearing may have failed, attempting force clear");
                    sessionManager.forceClearSession();
                }
            } else {
                Log.d(TAG, "SessionManager was null, no session to clear");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing user session", e);
        }
    }
}
