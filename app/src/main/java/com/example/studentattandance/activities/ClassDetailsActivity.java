package com.example.studentattandance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.adapters.StudentListAdapter;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassDetailsActivity extends AppCompatActivity {
    
    private static final String TAG = "ClassDetailsActivity";
    
    // UI Components
    private TextView tvClassName, tvSubject, tvSchedule, tvRoom, tvTeacher, tvCapacity;
    private RecyclerView rvStudents;
    
    // Data
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private String classId;
    private ClassEntity classEntity;
    private StudentListAdapter studentAdapter;
    private List<UserEntity> studentList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);
        
        try {
            // Initialize components
            initViews();
            setupToolbar();
            initializeData();
            loadClassData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing class details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void initViews() {
        tvClassName = findViewById(R.id.tv_class_name);
        tvSubject = findViewById(R.id.tv_subject);
        tvSchedule = findViewById(R.id.tv_schedule);
        tvRoom = findViewById(R.id.tv_room);
        tvTeacher = findViewById(R.id.tv_teacher);
        tvCapacity = findViewById(R.id.tv_capacity);
        rvStudents = findViewById(R.id.rv_students);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Class Details");
            }
        }
    }
    
    private void initializeData() {
        try {
            sessionManager = SessionManager.getInstance(this);
            dataRepository = new DataRepository(this);
            executorService = Executors.newSingleThreadExecutor();
            
            // Get class ID from intent
            classId = getIntent().getStringExtra("classId");
            if (classId == null) {
                Toast.makeText(this, "Class ID not provided", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            // Setup RecyclerView for students
            studentList = new ArrayList<>();
            studentAdapter = new StudentListAdapter(studentList);
            rvStudents.setLayoutManager(new LinearLayoutManager(this));
            rvStudents.setAdapter(studentAdapter);
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing data", e);
            Toast.makeText(this, "Error initializing class details", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadClassData() {
        executorService.execute(() -> {
            try {
                // Load class details
                classEntity = dataRepository.getClassByIdSync(classId);
                
                if (classEntity != null) {
                    runOnUiThread(() -> {
                        populateClassDetails();
                        loadEnrolledStudents();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ClassDetailsActivity.this, 
                                     "Class not found", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading class data", e);
                runOnUiThread(() -> {
                    Toast.makeText(ClassDetailsActivity.this, 
                                 "Error loading class data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void populateClassDetails() {
        if (classEntity != null) {
            tvClassName.setText(classEntity.getClassName());
            tvSubject.setText(classEntity.getSubject());
            
            if (classEntity.getSchedule() != null && !classEntity.getSchedule().isEmpty()) {
                tvSchedule.setText(classEntity.getSchedule());
            } else {
                tvSchedule.setText("Not set");
            }
            
            if (classEntity.getRoom() != null && !classEntity.getRoom().isEmpty()) {
                tvRoom.setText(classEntity.getRoom());
            } else {
                tvRoom.setText("Not set");
            }
            
            if (classEntity.getTeacherId() != null && !classEntity.getTeacherId().isEmpty()) {
                // Load teacher name
                UserEntity teacher = dataRepository.getUserByIdSync(classEntity.getTeacherId());
                if (teacher != null) {
                    tvTeacher.setText(teacher.getFirstName() + " " + teacher.getLastName());
                } else {
                    tvTeacher.setText("Not assigned");
                }
            } else {
                tvTeacher.setText("Not assigned");
            }
            
            tvCapacity.setText("30"); // Default capacity
        }
    }
    
    private void loadEnrolledStudents() {
        executorService.execute(() -> {
            try {
                // Get actual enrolled students for this class
                List<UserEntity> students = dataRepository.getStudentsByClassSync(classId);
                
                runOnUiThread(() -> {
                    studentList.clear();
                    if (students != null && !students.isEmpty()) {
                        studentList.addAll(students);
                    }
                    studentAdapter.notifyDataSetChanged();
                    
                    // Update the enrolled count display
                    updateEnrolledCount(students != null ? students.size() : 0);
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading enrolled students", e);
                runOnUiThread(() -> {
                    Toast.makeText(ClassDetailsActivity.this, 
                                 "Error loading students: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void updateEnrolledCount(int count) {
        // Update the capacity display to show enrolled vs total capacity
        String capacityText = count + " / 30 enrolled";
        tvCapacity.setText(capacityText);
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
