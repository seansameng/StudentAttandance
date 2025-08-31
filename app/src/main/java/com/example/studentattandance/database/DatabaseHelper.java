package com.example.studentattandance.database;

import android.content.Context;
import android.util.Log;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private final AppDatabase database;
    private final ExecutorService executorService;
    
    public DatabaseHelper(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newFixedThreadPool(4);
    }
    
    // Utility methods for generating IDs
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
    
    public static String generateShortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    // Database health check
    public boolean isDatabaseHealthy() {
        try {
            return database.isOpen();
        } catch (Exception e) {
            Log.e(TAG, "Database health check failed", e);
            return false;
        }
    }
    
    // Clear all data (for testing/reset purposes)
    public void clearAllData() {
        executorService.execute(() -> {
            try {
                database.clearAllTables();
                Log.i(TAG, "All database tables cleared successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to clear database tables", e);
            }
        });
    }
    
    // Get database statistics
    public void getDatabaseStats(DatabaseStatsCallback callback) {
        executorService.execute(() -> {
            try {
                int userCount = database.userDao().getAllUsers().getValue() != null ? 
                    database.userDao().getAllUsers().getValue().size() : 0;
                int classCount = database.classDao().getAllClasses().getValue() != null ? 
                    database.classDao().getAllClasses().getValue().size() : 0;
                int attendanceCount = database.attendanceDao().getAllAttendances().getValue() != null ? 
                    database.attendanceDao().getAllAttendances().getValue().size() : 0;
                int enrollmentCount = database.classEnrollmentDao().getAllEnrollments().getValue() != null ? 
                    database.classEnrollmentDao().getAllEnrollments().getValue().size() : 0;
                
                callback.onStatsReceived(userCount, classCount, attendanceCount, enrollmentCount);
            } catch (Exception e) {
                Log.e(TAG, "Failed to get database stats", e);
                callback.onError(e);
            }
        });
    }
    
    // Close database connection
    public void closeDatabase() {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
            AppDatabase.closeDatabase();
            Log.i(TAG, "Database connection closed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to close database", e);
        }
    }
    
    // Interface for database stats callback
    public interface DatabaseStatsCallback {
        void onStatsReceived(int userCount, int classCount, int attendanceCount, int enrollmentCount);
        void onError(Exception e);
    }
}
