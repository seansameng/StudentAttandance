package com.example.studentattandance.utils;

import android.content.Context;
import android.util.Log;

import com.example.studentattandance.database.AppDatabase;

public class DatabaseResetHelper {
    private static final String TAG = "DatabaseResetHelper";
    
    /**
     * Completely reset the database by clearing all data and recreating tables
     * This is useful when migration fails and you need a fresh start
     */
    public static void resetDatabase(Context context) {
        Log.d(TAG, "Starting complete database reset...");
        
        try {
            // Close the current database instance
            AppDatabase.closeDatabase();
            Log.d(TAG, "Database instance closed");
            
            // Clear the database file
            context.deleteDatabase("student_attendance_db");
            Log.d(TAG, "Database file deleted");
            
            // Reset the database instance
            AppDatabase.resetDatabase();
            Log.d(TAG, "Database instance reset");
            
            Log.d(TAG, "Database reset completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error during database reset", e);
            throw new RuntimeException("Failed to reset database", e);
        }
    }
    
    /**
     * Check if database needs to be reset due to migration issues
     */
    public static boolean needsReset(Context context) {
        try {
            // Try to get database instance
            AppDatabase database = AppDatabase.getInstance(context);
            
            // Try to access a simple DAO
            database.userDao();
            
            return false; // Database is working fine
            
        } catch (Exception e) {
            Log.w(TAG, "Database needs reset: " + e.getMessage());
            return true;
        }
    }
    
    /**
     * Safe database initialization with fallback to reset if needed
     */
    public static AppDatabase initializeDatabaseSafely(Context context) {
        try {
            Log.d(TAG, "Attempting safe database initialization...");
            
            // Check if database needs reset
            if (needsReset(context)) {
                Log.w(TAG, "Database needs reset, performing reset...");
                resetDatabase(context);
            }
            
            // Get fresh database instance
            AppDatabase database = AppDatabase.getInstance(context);
            Log.d(TAG, "Database initialized successfully");
            
            return database;
            
        } catch (Exception e) {
            Log.e(TAG, "Safe database initialization failed", e);
            
            // Last resort: force reset and try again
            try {
                Log.w(TAG, "Attempting forced reset as last resort...");
                resetDatabase(context);
                return AppDatabase.getInstance(context);
            } catch (Exception resetError) {
                Log.e(TAG, "Forced reset also failed", resetError);
                throw new RuntimeException("Database initialization completely failed", resetError);
            }
        }
    }
}
