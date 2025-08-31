package com.example.studentattandance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassEditActivity extends AppCompatActivity {
    
    private static final String TAG = "ClassEditActivity";
    
    // UI Components
    private EditText etClassName, etSubject, etSchedule, etRoom, etTeacherId;
    private Spinner spinnerCapacity;
    private Button btnSave, btnCancel;
    
    // Data
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private String classId;
    private String mode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_edit);
        
        try {
            // Initialize components
            initViews();
            setupToolbar();
            initializeData();
            setupClickListeners();
            loadClassData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing class edit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void initViews() {
        etClassName = findViewById(R.id.et_class_name);
        etSubject = findViewById(R.id.et_subject);
        etSchedule = findViewById(R.id.et_schedule);
        etRoom = findViewById(R.id.et_room);
        etTeacherId = findViewById(R.id.et_teacher_id);
        spinnerCapacity = findViewById(R.id.spinner_capacity);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }
    
    private void initializeData() {
        try {
            sessionManager = SessionManager.getInstance(this);
            dataRepository = new DataRepository(this);
            executorService = Executors.newSingleThreadExecutor();
            
            // Verify admin or teacher access
            if (sessionManager == null || (!sessionManager.isAdmin() && !sessionManager.isTeacher())) {
                Toast.makeText(this, "Access denied. Admin or Teacher privileges required.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            // Get mode and class ID
            mode = getIntent().getStringExtra("mode");
            classId = getIntent().getStringExtra("classId");
            
            if (mode == null) {
                mode = "create";
            }
            
            // Set title based on mode
            if (getSupportActionBar() != null) {
                if ("create".equals(mode)) {
                    getSupportActionBar().setTitle("Add New Class");
                } else {
                    getSupportActionBar().setTitle("Edit Class");
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing data", e);
            Toast.makeText(this, "Error initializing class edit", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveClass());
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void loadClassData() {
        if ("edit".equals(mode) && classId != null) {
            executorService.execute(() -> {
                try {
                    ClassEntity classEntity = dataRepository.getClassByIdSync(classId);
                    
                    if (classEntity != null) {
                        runOnUiThread(() -> {
                            populateForm(classEntity);
                        });
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error loading class data", e);
                    runOnUiThread(() -> {
                        Toast.makeText(ClassEditActivity.this, 
                                     "Error loading class data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
    
    private void populateForm(ClassEntity classEntity) {
        if (classEntity.getClassName() != null) {
            etClassName.setText(classEntity.getClassName());
        }
        
        if (classEntity.getSubject() != null) {
            etSubject.setText(classEntity.getSubject());
        }
        
        if (classEntity.getSchedule() != null) {
            etSchedule.setText(classEntity.getSchedule());
        }
        
        if (classEntity.getRoom() != null) {
            etRoom.setText(classEntity.getRoom());
        }
        
        if (classEntity.getTeacherId() != null) {
            etTeacherId.setText(classEntity.getTeacherId());
        }
    }
    
    private void saveClass() {
        // Validate input
        String className = etClassName.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String schedule = etSchedule.getText().toString().trim();
        String room = etRoom.getText().toString().trim();
        String teacherId = etTeacherId.getText().toString().trim();
        
        if (className.isEmpty()) {
            etClassName.setError("Class name is required");
            return;
        }
        
        if (subject.isEmpty()) {
            etSubject.setError("Subject is required");
            return;
        }
        
        // Create or update class
        executorService.execute(() -> {
            try {
                ClassEntity classEntity;
                
                if ("create".equals(mode)) {
                    // Create new class
                    classEntity = new ClassEntity();
                    classEntity.setId(UUID.randomUUID().toString());
                    classEntity.setCreatedAt(new Date(System.currentTimeMillis()));
                } else {
                    // Edit existing class
                    classEntity = dataRepository.getClassByIdSync(classId);
                    if (classEntity == null) {
                        throw new Exception("Class not found");
                    }
                }
                
                // Update fields
                classEntity.setClassName(className);
                classEntity.setSubject(subject);
                classEntity.setSchedule(schedule);
                classEntity.setRoom(room);
                classEntity.setTeacherId(teacherId);
                // Note: ClassEntity doesn't have updatedAt field, only createdAt
                
                // Save to database
                if ("create".equals(mode)) {
                    dataRepository.insertClass(classEntity);
                } else {
                    dataRepository.updateClass(classEntity);
                }
                
                runOnUiThread(() -> {
                    String message = "create".equals(mode) ? "Class created successfully" : "Class updated successfully";
                    Toast.makeText(ClassEditActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error saving class", e);
                runOnUiThread(() -> {
                    Toast.makeText(ClassEditActivity.this, 
                                 "Error saving class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
