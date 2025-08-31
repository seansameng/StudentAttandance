package com.example.studentattandance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.studentattandance.R;
import com.example.studentattandance.adapters.AttendanceViewAdapter;
import com.example.studentattandance.database.entities.ClassEnrollmentEntity;
import com.example.studentattandance.database.entities.AttendanceEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewAttendanceActivity extends AppCompatActivity {
    
    private static final String TAG = "ViewAttendanceActivity";
    
    private TextView tvTitle, tvSummary, tvNoData;
    private RecyclerView rvAttendance;
    private Button btnBack;
    
    private String viewType; // "student", "class", "individual"
    private String targetId; // student ID or class ID
    private List<AttendanceEntity> attendanceRecords;
    private AttendanceViewAdapter adapter;
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        
        try {
            // Get intent data
            viewType = getIntent().getStringExtra("view_type");
            targetId = getIntent().getStringExtra("target_id");
            
            if (viewType == null || targetId == null) {
                Toast.makeText(this, "Error: Missing view parameters", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            // Initialize components
            initViews();
            setupToolbar();
            initializeData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvSummary = findViewById(R.id.tv_summary);
        tvNoData = findViewById(R.id.tv_no_data);
        rvAttendance = findViewById(R.id.rv_attendance);
        btnBack = findViewById(R.id.btn_back);
        
        // Setup RecyclerView
        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        
        // Setup click listeners
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("View Attendance");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }
    
    private void initializeData() {
        try {
            // Initialize repositories
            dataRepository = new DataRepository(this);
            sessionManager = SessionManager.getInstance(this);
            executorService = Executors.newSingleThreadExecutor();
            
            // Load attendance data based on view type
            loadAttendanceData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing data", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadAttendanceData() {
        executorService.execute(() -> {
            try {
                switch (viewType) {
                    case "student":
                        loadStudentAttendance();
                        break;
                    case "class":
                        loadClassAttendance();
                        break;
                    case "individual":
                        loadIndividualAttendance();
                        break;
                    default:
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Invalid view type", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                        return;
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading attendance data", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void loadStudentAttendance() {
        try {
            // Load attendance records for the student
            List<AttendanceEntity> records = dataRepository.getAttendanceHistoryByStudentAndClass(targetId, null).getValue();
            
            if (records == null) {
                records = new ArrayList<>();
            }
            
            // Sort by date (most recent first)
            records.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            
            attendanceRecords = records;
            
            runOnUiThread(() -> {
                updateStudentUI();
                setupAdapter();
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading student attendance", e);
        }
    }
    
    private void loadClassAttendance() {
        try {
            // Load attendance records for the class
            List<AttendanceEntity> records = dataRepository.getAttendancesByClass(targetId).getValue();
            
            if (records == null) {
                records = new ArrayList<>();
            }
            
            // Sort by date (most recent first)
            records.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            
            attendanceRecords = records;
            
            runOnUiThread(() -> {
                updateClassUI();
                setupAdapter();
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading class attendance", e);
        }
    }
    
    private void loadIndividualAttendance() {
        try {
            // Load attendance records for individual student in specific class
            String[] ids = targetId.split("_");
            if (ids.length == 2) {
                String studentId = ids[0];
                String classId = ids[1];
                
                List<AttendanceEntity> records = dataRepository.getAttendanceHistoryByStudentAndClass(studentId, classId).getValue();
                
                if (records == null) {
                    records = new ArrayList<>();
                }
                
                // Sort by date (most recent first)
                records.sort((a, b) -> b.getDate().compareTo(a.getDate()));
                
                attendanceRecords = records;
                
                runOnUiThread(() -> {
                    updateIndividualUI(studentId, classId);
                    setupAdapter();
                });
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading individual attendance", e);
        }
    }
    
    private void updateStudentUI() {
        tvTitle.setText("My Attendance Records");
        
        if (attendanceRecords.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
            tvSummary.setVisibility(View.GONE);
            rvAttendance.setVisibility(View.GONE);
            return;
        }
        
        // Calculate attendance statistics
        int totalRecords = attendanceRecords.size();
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;
        
        for (AttendanceEntity record : attendanceRecords) {
            switch (record.getStatus()) {
                case "PRESENT":
                    presentCount++;
                    break;
                case "ABSENT":
                    absentCount++;
                    break;
                case "LATE":
                    lateCount++;
                    break;
            }
        }
        
        double attendancePercentage = totalRecords > 0 ? (double) presentCount / totalRecords * 100 : 0;
        
        String summary = String.format("Total Records: %d\nPresent: %d | Absent: %d | Late: %d\nAttendance Rate: %.1f%%",
                totalRecords, presentCount, absentCount, lateCount, attendancePercentage);
        
        tvSummary.setText(summary);
        tvSummary.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);
        rvAttendance.setVisibility(View.VISIBLE);
    }
    
    private void updateClassUI() {
        tvTitle.setText("Class Attendance Report");
        
        if (attendanceRecords.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
            tvSummary.setVisibility(View.GONE);
            rvAttendance.setVisibility(View.GONE);
            return;
        }
        
        // Calculate class attendance statistics
        int totalRecords = attendanceRecords.size();
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;
        
        for (AttendanceEntity record : attendanceRecords) {
            switch (record.getStatus()) {
                case "PRESENT":
                    presentCount++;
                    break;
                case "ABSENT":
                    absentCount++;
                    break;
                case "LATE":
                    lateCount++;
                    break;
            }
        }
        
        double attendancePercentage = totalRecords > 0 ? (double) presentCount / totalRecords * 100 : 0;
        
        String summary = String.format("Total Records: %d\nPresent: %d | Absent: %d | Late: %d\nClass Attendance Rate: %.1f%%",
                totalRecords, presentCount, absentCount, lateCount, attendancePercentage);
        
        tvSummary.setText(summary);
        tvSummary.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);
        rvAttendance.setVisibility(View.VISIBLE);
    }
    
    private void updateIndividualUI(String studentId, String classId) {
        tvTitle.setText("Student Attendance Report");
        
        if (attendanceRecords.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
            tvSummary.setVisibility(View.GONE);
            rvAttendance.setVisibility(View.GONE);
            return;
        }
        
        // Calculate individual attendance statistics
        int totalRecords = attendanceRecords.size();
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;
        
        for (AttendanceEntity record : attendanceRecords) {
            switch (record.getStatus()) {
                case "PRESENT":
                    presentCount++;
                    break;
                case "ABSENT":
                    absentCount++;
                    break;
                case "LATE":
                    lateCount++;
                    break;
            }
        }
        
        double attendancePercentage = totalRecords > 0 ? (double) presentCount / totalRecords * 100 : 0;
        
        String summary = String.format("Student ID: %s\nClass ID: %s\nTotal Records: %d\nPresent: %d | Absent: %d | Late: %d\nAttendance Rate: %.1f%%",
                studentId, classId, totalRecords, presentCount, absentCount, lateCount, attendancePercentage);
        
        tvSummary.setText(summary);
        tvSummary.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);
        rvAttendance.setVisibility(View.VISIBLE);
    }
    
    private void setupAdapter() {
        adapter = new AttendanceViewAdapter(attendanceRecords);
        rvAttendance.setAdapter(adapter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
