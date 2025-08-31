package com.example.studentattandance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditClassActivity extends AppCompatActivity {
    
    private static final String TAG = "EditClassActivity";
    
    private EditText etClassName, etSubject, etSchedule, etRoom;
    private Spinner spinnerTeacher;
    private TextView tvCreatedDate, tvStatus;
    private Button btnEdit, btnDelete, btnCancel, btnSave;
    private ProgressBar progressBar;
    private LinearLayout formLayout;
    
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    
    private String classId;
    private String mode; // "view" or "edit"
    private ClassEntity currentClass;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        
        // Get intent data
        classId = getIntent().getStringExtra("classId");
        mode = getIntent().getStringExtra("mode");
        
        if (classId == null) {
            Toast.makeText(this, "Error: No class ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize components
        initViews();
        setupToolbar();
        setupSpinners();
        setupClickListeners();
        
        // Initialize data
        dataRepository = new DataRepository(this);
        sessionManager = SessionManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Check admin access
        if (!isAdminUser()) {
            showAccessDenied();
            return;
        }
        
        // Load class data
        loadClassData();
    }
    
    private void initViews() {
        etClassName = findViewById(R.id.et_class_name);
        etSubject = findViewById(R.id.et_subject);
        etSchedule = findViewById(R.id.et_schedule);
        etRoom = findViewById(R.id.et_room);
        spinnerTeacher = findViewById(R.id.spinner_teacher);
        tvCreatedDate = findViewById(R.id.tv_created_date);
        tvStatus = findViewById(R.id.tv_status);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
        formLayout = findViewById(R.id.form_layout);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Class");
        }
    }
    
    private void setupSpinners() {
        // Setup teacher spinner with available teachers
        String[] teacherOptions = {"Select Teacher", "Teacher 1", "Teacher 2", "Teacher 3"}; // TODO: Load from database
        android.widget.ArrayAdapter<String> teacherAdapter = new android.widget.ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, teacherOptions);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);
    }
    
    private void setupClickListeners() {
        btnEdit.setOnClickListener(v -> setEditMode());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        btnCancel.setOnClickListener(v -> setViewMode());
        btnSave.setOnClickListener(v -> validateAndSaveClass());
    }
    
    private boolean isAdminUser() {
        try {
            if (sessionManager == null || !sessionManager.isLoggedIn()) {
                return false;
            }
            
            String role = sessionManager.getUserRole();
            return "ADMIN".equals(role) || "Administrator".equals(role);
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking admin status", e);
            return false;
        }
    }
    
    private void showAccessDenied() {
        Toast.makeText(this, "Access Denied: Only administrators can edit classes", Toast.LENGTH_LONG).show();
        finish();
    }
    
    private void loadClassData() {
        showProgress(true);
        
        executorService.execute(() -> {
            try {
                ClassEntity classEntity = dataRepository.getClassByIdSync(classId);
                
                if (classEntity == null) {
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(EditClassActivity.this, "Class not found", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }
                
                currentClass = classEntity;
                
                runOnUiThread(() -> {
                    showProgress(false);
                    populateClassData(classEntity);
                    setMode(mode);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading class data", e);
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(EditClassActivity.this, "Error loading class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void populateClassData(ClassEntity classEntity) {
        try {
            if (etClassName != null) etClassName.setText(classEntity.getClassName());
            if (etSubject != null) etSubject.setText(classEntity.getSubject());
            if (etSchedule != null) etSchedule.setText(classEntity.getSchedule());
            if (etRoom != null) etRoom.setText(classEntity.getRoom());
            
            // Set teacher spinner (TODO: Find teacher position)
            if (spinnerTeacher != null) {
                // For now, set to first position
                spinnerTeacher.setSelection(0);
            }
            
            // Format and display dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
            
            if (tvCreatedDate != null && classEntity.getCreatedAt() != null) {
                tvCreatedDate.setText("Created: " + dateFormat.format(classEntity.getCreatedAt()));
            } else if (tvCreatedDate != null) {
                tvCreatedDate.setText("Created: Unknown");
            }
            
            if (tvStatus != null) {
                tvStatus.setText("Status: Active"); // ClassEntity doesn't have isActive field
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error populating class data", e);
        }
    }
    
    private void setMode(String mode) {
        if ("view".equals(mode)) {
            setViewMode();
        } else if ("edit".equals(mode)) {
            setEditMode();
        }
    }
    
    private void setViewMode() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("View Class");
        }
        
        setInputFieldsEnabled(false);
        btnEdit.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
    }
    
    private void setEditMode() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Class");
        }
        
        setInputFieldsEnabled(true);
        btnEdit.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
    }
    
    private void setInputFieldsEnabled(boolean enabled) {
        if (etClassName != null) etClassName.setEnabled(enabled);
        if (etSubject != null) etSubject.setEnabled(enabled);
        if (etSchedule != null) etSchedule.setEnabled(enabled);
        if (etRoom != null) etRoom.setEnabled(enabled);
        if (spinnerTeacher != null) spinnerTeacher.setEnabled(enabled);
    }
    
    private void validateAndSaveClass() {
        try {
            String className = etClassName.getText().toString().trim();
            String subject = etSubject.getText().toString().trim();
            String schedule = etSchedule.getText().toString().trim();
            String room = etRoom.getText().toString().trim();
            String teacher = spinnerTeacher.getSelectedItem().toString();
            
            // Validation
            if (className.isEmpty()) {
                etClassName.setError("Class name is required");
                return;
            }
            
            if (subject.isEmpty()) {
                etSubject.setError("Subject is required");
                return;
            }
            
            if ("Select Teacher".equals(teacher)) {
                Toast.makeText(this, "Please select a teacher", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Update class
            if (currentClass != null) {
                currentClass.setClassName(className);
                currentClass.setSubject(subject);
                currentClass.setSchedule(schedule);
                currentClass.setRoom(room);
                currentClass.setTeacherId(teacher); // TODO: Use actual teacher ID
                
                saveClass(currentClass);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error validating class data", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveClass(ClassEntity classEntity) {
        showProgress(true);
        
        executorService.execute(() -> {
            try {
                dataRepository.updateClass(classEntity);
                
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(EditClassActivity.this, 
                        "Class updated successfully", Toast.LENGTH_SHORT).show();
                    setViewMode();
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error saving class", e);
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(EditClassActivity.this, 
                        "Error updating class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void showDeleteConfirmation() {
        if (currentClass == null) {
            Toast.makeText(this, "No class data available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Class")
            .setMessage("Are you sure you want to delete " + currentClass.getClassName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> deleteClass())
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void deleteClass() {
        if (currentClass == null) {
            Toast.makeText(this, "No class data available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showProgress(true);
        
        executorService.execute(() -> {
            try {
                dataRepository.deleteClass(currentClass);
                
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(EditClassActivity.this, 
                        "Class deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error deleting class", e);
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(EditClassActivity.this, 
                        "Error deleting class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (formLayout != null) {
            formLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_class, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
