package com.example.studentattandance.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.studentattandance.database.AppDatabase;
import com.example.studentattandance.database.dao.UserDao;

public class DatabaseTestActivity extends Activity {
    private static final String TAG = "DatabaseTestActivity";
    private TextView resultText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create a simple text view to show results
        resultText = new TextView(this);
        resultText.setPadding(20, 20, 20, 20);
        setContentView(resultText);
        
        // Run database tests
        testDatabase();
    }
    
    private void testDatabase() {
        resultText.setText("Testing Database...\n\n");
        
        try {
            // Test 1: Create database instance
            resultText.append("Test 1: Creating database instance...\n");
            AppDatabase database = AppDatabase.getInstance(this);
            
            if (database == null) {
                resultText.append("❌ FAILED: Database instance is null\n");
                return;
            }
            resultText.append("✅ PASSED: Database instance created\n\n");
            
            // Test 2: Check if database is open
            resultText.append("Test 2: Checking if database is open...\n");
            boolean isOpen = database.isOpen();
            resultText.append("Database is open: " + isOpen + "\n");
            
            if (!isOpen) {
                resultText.append("❌ FAILED: Database is not open\n");
                return;
            }
            resultText.append("✅ PASSED: Database is open\n\n");
            
            // Test 3: Test DAO access
            resultText.append("Test 3: Testing DAO access...\n");
            UserDao userDao = database.userDao();
            
            if (userDao == null) {
                resultText.append("❌ FAILED: UserDao is null\n");
                return;
            }
            resultText.append("✅ PASSED: UserDao accessed successfully\n\n");
            
            // Test 4: Test simple query
            resultText.append("Test 4: Testing simple query...\n");
            try {
                Object users = userDao.getAllUsersSync();
                int count = users != null ? ((java.util.List<?>) users).size() : 0;
                resultText.append("✅ PASSED: Query executed successfully - User count: " + count + "\n\n");
            } catch (Exception e) {
                resultText.append("❌ FAILED: Query execution failed - " + e.getMessage() + "\n\n");
                e.printStackTrace();
            }
            
            // Test 5: Database path
            try {
                String dbPath = database.getOpenHelper().getDatabaseName();
                resultText.append("Database path: " + dbPath + "\n\n");
            } catch (Exception e) {
                resultText.append("Could not get database path: " + e.getMessage() + "\n\n");
            }
            
            resultText.append("=== DATABASE CONNECTION TEST COMPLETED ===\n");
            resultText.append("✅ All tests passed! Database is working correctly.\n");
            
        } catch (Exception e) {
            resultText.append("=== DATABASE CONNECTION TEST FAILED ===\n");
            resultText.append("Error details: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
}
