package com.example.studentattandance.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentattandance.R;

public class AddClassActivity extends Activity {
    
    private static final String TAG = "AddClassActivity";
    
    // Input field references
    private EditText etClassName, etSubject, etSchedule, etRoomNumber, etMaxStudents;
    
    // Button references
    private Button btnAddClass, btnCancel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "AddClassActivity onCreate started");
        super.onCreate(savedInstanceState);
        
        // Set the layout
        setContentView(R.layout.activity_add_class);
        
        // Initialize views
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
        
        Log.d(TAG, "AddClassActivity onCreate completed");
    }
    
    private void initializeViews() {
        try {
            Log.d(TAG, "Starting view initialization...");
            
            // Initialize input fields
            etClassName = findViewById(R.id.etClassName);
            etSubject = findViewById(R.id.etSubject);
            etSchedule = findViewById(R.id.etSchedule);
            etRoomNumber = findViewById(R.id.etRoomNumber);
            etMaxStudents = findViewById(R.id.etMaxStudents);
            
            // Initialize buttons
            btnAddClass = findViewById(R.id.btnAddClass);
            btnCancel = findViewById(R.id.btnCancel);
            
            // Log successful view initialization with IDs
            if (etClassName != null) {
                Log.d(TAG, "etClassName found successfully - ID: " + etClassName.getId());
            } else {
                Log.e(TAG, "etClassName is NULL - EditText not found in layout!");
            }
            
            if (etSubject != null) {
                Log.d(TAG, "etSubject found successfully - ID: " + etSubject.getId());
            } else {
                Log.e(TAG, "etSubject is NULL - EditText not found in layout!");
            }
            
            if (etSchedule != null) {
                Log.d(TAG, "etSchedule found successfully - ID: " + etSchedule.getId());
            } else {
                Log.e(TAG, "etSchedule is NULL - EditText not found in layout!");
            }
            
            if (etRoomNumber != null) {
                Log.d(TAG, "etRoomNumber found successfully - ID: " + etRoomNumber.getId());
            } else {
                Log.e(TAG, "etRoomNumber is NULL - EditText not found in layout!");
            }
            
            if (etMaxStudents != null) {
                Log.d(TAG, "etMaxStudents found successfully - ID: " + etMaxStudents.getId());
            } else {
                Log.e(TAG, "etMaxStudents is NULL - EditText not found in layout!");
            }
            
            if (btnAddClass != null) {
                Log.d(TAG, "btnAddClass found successfully - ID: " + btnAddClass.getId());
            } else {
                Log.e(TAG, "btnAddClass is NULL - Button not found in layout!");
            }
            
            if (btnCancel != null) {
                Log.d(TAG, "btnCancel found successfully - ID: " + btnCancel.getId());
            } else {
                Log.e(TAG, "btnCancel is NULL - Button not found in layout!");
            }
            
            // Check if any critical views are missing
            int missingViews = 0;
            if (etClassName == null) missingViews++;
            if (etSubject == null) missingViews++;
            if (etSchedule == null) missingViews++;
            if (btnAddClass == null) missingViews++;
            if (btnCancel == null) missingViews++;
            
            if (missingViews > 0) {
                Log.e(TAG, "WARNING: " + missingViews + " critical views are missing!");
                Toast.makeText(this, "Error: Some form elements could not be loaded", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "All critical views initialized successfully!");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error initializing form: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void setupClickListeners() {
        // Add Class button
        if (btnAddClass != null) {
            btnAddClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Add Class button clicked");
                    handleAddClass();
                }
            });
            Log.d(TAG, "Click listener set for btnAddClass");
        } else {
            Log.e(TAG, "btnAddClass is null - cannot set click listener");
        }
        
        // Cancel button
        if (btnCancel != null) {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Cancel button clicked");
                    finish(); // Close the activity
                }
            });
            Log.d(TAG, "Click listener set for btnCancel");
        } else {
            Log.e(TAG, "btnCancel is null - cannot set click listener");
        }
    }
    
    private void handleAddClass() {
        try {
            // Check if views are properly initialized
            if (etClassName == null || etSubject == null || etSchedule == null) {
                Toast.makeText(this, "Error: Form not properly loaded", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Critical views are null - cannot process form");
                return;
            }
            
            // Get input values
            String className = etClassName.getText().toString().trim();
            String subject = etSubject.getText().toString().trim();
            String schedule = etSchedule.getText().toString().trim();
            String roomNumber = etRoomNumber != null ? etRoomNumber.getText().toString().trim() : "";
            String maxStudentsStr = etMaxStudents != null ? etMaxStudents.getText().toString().trim() : "";
            
            // Validate required fields
            if (TextUtils.isEmpty(className)) {
                etClassName.setError("Class name is required");
                etClassName.requestFocus();
                return;
            }
            
            if (TextUtils.isEmpty(subject)) {
                etSubject.setError("Subject is required");
                etSubject.requestFocus();
                return;
            }
            
            if (TextUtils.isEmpty(schedule)) {
                etSchedule.setError("Schedule is required");
                etSchedule.requestFocus();
                return;
            }
            
            // Additional validation for class name length
            if (className.length() < 3) {
                etClassName.setError("Class name must be at least 3 characters");
                etClassName.requestFocus();
                return;
            }
            
            if (className.length() > 100) {
                etClassName.setError("Class name is too long (max 100 characters)");
                etClassName.requestFocus();
                return;
            }
            
            // Validate max students (if provided)
            int maxStudents = 0;
            if (!TextUtils.isEmpty(maxStudentsStr)) {
                try {
                    maxStudents = Integer.parseInt(maxStudentsStr);
                    if (maxStudents <= 0) {
                        etMaxStudents.setError("Maximum students must be greater than 0");
                        etMaxStudents.requestFocus();
                        return;
                    }
                    if (maxStudents > 1000) {
                        etMaxStudents.setError("Maximum students cannot exceed 1000");
                        etMaxStudents.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    etMaxStudents.setError("Please enter a valid number");
                    etMaxStudents.requestFocus();
                    return;
                }
            }
            
            // All validation passed - create the class
            createClass(className, subject, schedule, roomNumber, maxStudents);
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling add class: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void createClass(String className, String subject, String schedule, String roomNumber, int maxStudents) {
        try {
            Log.d(TAG, "Creating class: " + className);
            
            // TODO: Implement actual class creation logic
            // This is where you would:
            // 1. Save to database
            // 2. Make API call
            // 3. Update local storage
            
            // For now, just show success message
            Toast.makeText(this, "Class '" + className + "' created successfully!", Toast.LENGTH_LONG).show();
            
            // Return to previous screen
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating class: " + e.getMessage());
            Toast.makeText(this, "Error creating class: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "AddClassActivity onResume called");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "AddClassActivity onPause called");
    }
}
