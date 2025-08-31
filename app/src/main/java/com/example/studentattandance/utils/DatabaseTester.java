package com.example.studentattandance.utils;

import android.content.Context;
import android.util.Log;

import com.example.studentattandance.database.AppDatabase;
import com.example.studentattandance.database.dao.UserDao;

import java.util.List;

public class DatabaseTester {
    private static final String TAG = "DatabaseTester";
    
    public static void testDatabaseConnection(Context context) {
        Log.d(TAG, "=== DATABASE CONNECTION TEST START ===");
        
        try {
            // Test 1: Basic database instance creation
            Log.d(TAG, "Test 1: Creating database instance...");
            AppDatabase database = AppDatabase.getInstance(context);
            
            if (database == null) {
                Log.e(TAG, "❌ FAILED: Database instance is null");
                return;
            }
            Log.d(TAG, "✅ PASSED: Database instance created");
            
            // Test 2: Check if database is open
            Log.d(TAG, "Test 2: Checking if database is open...");
            boolean isOpen = database.isOpen();
            Log.d(TAG, "Database is open: " + isOpen);
            
            if (!isOpen) {
                Log.e(TAG, "❌ FAILED: Database is not open");
                return;
            }
            Log.d(TAG, "✅ PASSED: Database is open");
            
            // Test 3: Test DAO access
            Log.d(TAG, "Test 3: Testing DAO access...");
            UserDao userDao = database.userDao();
            
            if (userDao == null) {
                Log.e(TAG, "❌ FAILED: UserDao is null");
                return;
            }
            Log.d(TAG, "✅ PASSED: UserDao accessed successfully");
            
            // Test 4: Test simple query
            Log.d(TAG, "Test 4: Testing simple query...");
            try {
                List<?> users = userDao.getAllUsersSync();
                int count = users != null ? users.size() : 0;
                Log.d(TAG, "✅ PASSED: Query executed successfully - User count: " + count);
            } catch (Exception e) {
                Log.e(TAG, "❌ FAILED: Query execution failed - " + e.getMessage());
                e.printStackTrace();
            }
            
            // Test 5: Database path
            try {
                String dbPath = database.getOpenHelper().getDatabaseName();
                Log.d(TAG, "Database path: " + dbPath);
            } catch (Exception e) {
                Log.e(TAG, "Could not get database path: " + e.getMessage());
            }
            
            Log.d(TAG, "=== DATABASE CONNECTION TEST END ===");
            
        } catch (Exception e) {
            Log.e(TAG, "=== DATABASE CONNECTION TEST FAILED ===", e);
            Log.e(TAG, "Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static boolean isDatabaseWorking(Context context) {
        try {
            AppDatabase database = AppDatabase.getInstance(context);
            if (database != null && database.isOpen()) {
                UserDao userDao = database.userDao();
                if (userDao != null) {
                    // Try a simple query
                    List<?> users = userDao.getAllUsersSync();
                    Log.d(TAG, "Database is working - User count: " + (users != null ? users.size() : 0));
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Database check failed", e);
            return false;
        }
    }
    
    public static void resetDatabase(Context context) {
        try {
            Log.d(TAG, "Resetting database...");
            AppDatabase.resetDatabase();
            Log.d(TAG, "Database reset completed");
        } catch (Exception e) {
            Log.e(TAG, "Error resetting database", e);
        }
    }
}
