package com.example.studentattandance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.widget.LinearLayout;
import android.util.Log;
import android.content.Intent;

import com.example.studentattandance.R;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.adapters.ClassDashboardAdapter;

// import com.example.studentattandance.activities.ClassManagementActivity;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {
    
    private static final String TAG = "DashboardFragment";
    private SessionManager sessionManager;
    private DataRepository dataRepository;
    private ExecutorService executorService;
    private RecyclerView upcomingClassesRecycler;
    private ClassDashboardAdapter classAdapter;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Creating dashboard view...");
            
            // Initialize SessionManager first
            try {
                sessionManager = SessionManager.getInstance(requireContext());
                if (sessionManager == null) {
                    Log.e(TAG, "SessionManager is null, using fallback view");
                    return createFallbackView("Session manager not available");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing SessionManager", e);
                return createFallbackView("Error initializing session: " + e.getMessage());
            }
            
            // Initialize DataRepository with error handling
            try {
                dataRepository = new DataRepository(requireContext());
                Log.d(TAG, "DataRepository initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing DataRepository", e);
                dataRepository = null; // Continue without database functionality
            }
            
            // Initialize ExecutorService
            try {
                executorService = Executors.newSingleThreadExecutor();
            } catch (Exception e) {
                Log.e(TAG, "Error initializing ExecutorService", e);
                executorService = null;
            }
            
            // Use the existing fragment_dashboard.xml layout
            View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
            
            // Initialize views and set data based on user role
            setupDashboardContent(view);
            
            // Load and display classes (only if database is available)
            if (dataRepository != null && executorService != null) {
                loadClassesForDashboard();
            } else {
                Log.w(TAG, "Database not available, loading sample data instead");
                // Load sample data when database is not available
                loadSampleClasses();
            }
            
            Log.d(TAG, "Dashboard view created successfully");
            return view;
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating dashboard view", e);
            e.printStackTrace();
            return createFallbackView("Error loading dashboard: " + e.getMessage());
        }
    }
    
    private TextView createFallbackView(String message) {
        TextView fallbackView = new TextView(requireContext());
        fallbackView.setText("Dashboard - " + message + "\n\nPlease restart the app or contact support.");
        fallbackView.setTextSize(16);
        fallbackView.setTextColor(android.graphics.Color.RED);
        fallbackView.setGravity(android.view.Gravity.CENTER);
        fallbackView.setPadding(50, 100, 50, 100);
        return fallbackView;
    }
    
    private void setupDashboardContent(View view) {
        try {
            // Get user info
            if (sessionManager != null && sessionManager.isLoggedIn()) {
                String username = sessionManager.getUsername();
                String role = sessionManager.getUserRole();
                
                // Update welcome message
                TextView welcomeText = view.findViewById(R.id.tv_welcome);
                if (welcomeText != null) {
                    if (username != null && role != null) {
                        welcomeText.setText("Welcome, " + username + " (" + role + ")");
                    } else if (username != null) {
                        welcomeText.setText("Welcome, " + username);
                    }
                }
                
                // Initialize RecyclerView for upcoming classes
                upcomingClassesRecycler = view.findViewById(R.id.upcoming_classes_recycler);
                if (upcomingClassesRecycler != null) {
                    upcomingClassesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                    classAdapter = new ClassDashboardAdapter();
                    upcomingClassesRecycler.setAdapter(classAdapter);
                }
                

                
                // Update statistics based on user role
                updateStatistics(view, role);
                
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up dashboard content", e);
        }
    }
    
    private void loadClassesForDashboard() {
        try {
            if (sessionManager == null || !sessionManager.isLoggedIn()) {
                Log.w(TAG, "User not logged in, cannot load classes");
                return;
            }
            
            final String userId = sessionManager.getUserId();
            final String userRole = sessionManager.getUserRole();
            
            if (userId == null || userRole == null) {
                Log.w(TAG, "User ID or role is null, cannot load classes");
                return;
            }
            
            Log.d(TAG, "Loading classes for user: " + userId + " with role: " + userRole);
            
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<ClassEntity> classes = null;
                        
                        switch (userRole.toUpperCase()) {
                            case "TEACHER":
                                // Load classes taught by this teacher
                                classes = dataRepository.getClassesByTeacherSync(userId);
                                break;
                            case "STUDENT":
                                // Load classes where this student is enrolled
                                classes = dataRepository.getEnrolledClasses(userId);
                                break;
                            case "ADMIN":
                            case "ADMINISTRATOR":
                                // Load all classes in the system
                                classes = dataRepository.getAllClassesSync();
                                break;
                            default:
                                Log.w(TAG, "Unknown user role: " + userRole);
                                break;
                        }
                        
                        if (classes != null && !classes.isEmpty()) {
                            Log.d(TAG, "Loaded " + classes.size() + " classes for dashboard");
                            
                            // Update UI on main thread
                            if (getActivity() != null) {
                                List<ClassEntity> finalClasses = classes;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (classAdapter != null) {
                                            classAdapter.updateClasses(finalClasses);
                                        }
                                        
                                        // Update statistics with real data
                                        updateStatisticsWithRealData(finalClasses, userRole);
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "No classes found for user role: " + userRole + ", loading sample data");
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Load sample data when no real data is available
                                        loadSampleClasses();
                                    }
                                });
                            }
                        }
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading classes for dashboard", e);
                    }
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in loadClassesForDashboard", e);
        }
    }
    
    private void updateStatisticsWithRealData(List<ClassEntity> classes, String userRole) {
        try {
            if (classes == null || classes.isEmpty()) {
                Log.d(TAG, "No classes available to calculate statistics from Room database");
                return;
            }
            
            View currentView = getView();
            if (currentView == null) {
                Log.w(TAG, "Fragment view is null, cannot update statistics from Room database");
                return;
            }
            
            // Update total classes count from Room database
            TextView totalClassesText = currentView.findViewById(R.id.tv_total_classes);
            if (totalClassesText != null) {
                totalClassesText.setText(String.valueOf(classes.size()));
                Log.d(TAG, "Updated total classes count from Room database: " + classes.size());
            }
            
            // Calculate total students from Room database enrollments
            executorService.execute(() -> {
                try {
                    int totalStudents = 0;
                    
                    // For each class, get the enrollment count from Room database
                    for (ClassEntity classEntity : classes) {
                        try {
                            List<com.example.studentattandance.database.entities.ClassEnrollmentEntity> enrollments = 
                                dataRepository.getEnrollmentsByClassSync(classEntity.getId());
                            if (enrollments != null) {
                                totalStudents += enrollments.size();
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Error getting enrollments for class: " + classEntity.getClassName(), e);
                        }
                    }
                    
                    final int finalTotalStudents = totalStudents;
                    
                    // Update UI on main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            TextView totalStudentsText = getView().findViewById(R.id.tv_total_students);
                            if (totalStudentsText != null) {
                                totalStudentsText.setText(String.valueOf(finalTotalStudents));
                                Log.d(TAG, "Updated total students count from Room database: " + finalTotalStudents);
                            }
                        });
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error calculating total students from Room database", e);
                }
            });
            
            // Update attendance rate from Room database
            executorService.execute(() -> {
                try {
                    double attendanceRate = 0.0;
                    int totalAttendanceRecords = 0;
                    int presentRecords = 0;
                    
                    // Calculate attendance from Room database
                    for (ClassEntity classEntity : classes) {
                        try {
                            // Get all attendance records and filter by class
                            List<com.example.studentattandance.database.entities.AttendanceEntity> allAttendanceRecords = 
                                dataRepository.getAllAttendancesSync();
                            if (allAttendanceRecords != null) {
                                for (com.example.studentattandance.database.entities.AttendanceEntity record : allAttendanceRecords) {
                                    // Check if this attendance record belongs to the current class
                                    if (classEntity.getId().equals(record.getClassId())) {
                                        totalAttendanceRecords++;
                                        if ("PRESENT".equalsIgnoreCase(record.getStatus())) {
                                            presentRecords++;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Error getting attendance for class: " + classEntity.getClassName(), e);
                        }
                    }
                    
                    if (totalAttendanceRecords > 0) {
                        attendanceRate = (double) presentRecords / totalAttendanceRecords * 100;
                    }
                    
                    final double finalAttendanceRate = attendanceRate;
                    
                    // Update UI on main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            TextView attendanceRateText = getView().findViewById(R.id.tv_average_attendance);
                            if (attendanceRateText != null) {
                                attendanceRateText.setText(String.format("%.1f%%", finalAttendanceRate));
                                Log.d(TAG, "Updated attendance rate from Room database: " + String.format("%.1f%%", finalAttendanceRate));
                            }
                        });
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error calculating attendance rate from Room database", e);
                }
            });
            
            // Update today's attendance from Room database
            executorService.execute(() -> {
                try {
                    int todayAttendance = 0;
                    java.util.Date today = new java.util.Date();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    String todayString = sdf.format(today);
                    
                    // Count today's attendance records from Room database
                    for (ClassEntity classEntity : classes) {
                        try {
                            // Get all attendance records and filter by class and date
                            List<com.example.studentattandance.database.entities.AttendanceEntity> allAttendanceRecords = 
                                dataRepository.getAllAttendancesSync();
                            if (allAttendanceRecords != null) {
                                for (com.example.studentattandance.database.entities.AttendanceEntity record : allAttendanceRecords) {
                                    // Check if this attendance record belongs to the current class and is from today
                                    if (classEntity.getId().equals(record.getClassId()) && 
                                        todayString.equals(record.getDate())) {
                                        todayAttendance++;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Error getting today's attendance for class: " + classEntity.getClassName(), e);
                        }
                    }
                    
                    final int finalTodayAttendance = todayAttendance;
                    
                    // Update UI on main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            TextView todayAttendanceText = getView().findViewById(R.id.tv_today_attendance);
                            if (todayAttendanceText != null) {
                                todayAttendanceText.setText(String.valueOf(finalTodayAttendance));
                                Log.d(TAG, "Updated today's attendance from Room database: " + finalTodayAttendance);
                            }
                        });
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error calculating today's attendance from Room database", e);
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating statistics with real data from Room database", e);
        }
    }
    
    private void loadSampleClasses() {
        try {
            Log.d(TAG, "Loading sample classes for dashboard");
            
            // Create sample class data
            List<ClassEntity> sampleClasses = new ArrayList<>();
            
            // Sample class 1
            ClassEntity class1 = new ClassEntity();
            class1.setClassName("Mathematics 101");
            class1.setSubject("Mathematics");
            class1.setSchedule("Monday, Wednesday, Friday 9:00 AM - 10:30 AM");
            class1.setRoom("Room 201");
            class1.setTeacherId("T001");
            sampleClasses.add(class1);
            
            // Sample class 2
            ClassEntity class2 = new ClassEntity();
            class2.setClassName("Physics 201");
            class2.setSubject("Physics");
            class2.setSchedule("Tuesday, Thursday 2:00 PM - 3:30 PM");
            class2.setRoom("Room 305");
            class2.setTeacherId("T002");
            sampleClasses.add(class2);
            
            // Sample class 3
            ClassEntity class3 = new ClassEntity();
            class3.setClassName("Computer Science 101");
            class3.setSubject("Computer Science");
            class3.setSchedule("Monday, Wednesday 1:00 PM - 2:30 PM");
            class3.setRoom("Lab 101");
            class3.setTeacherId("T003");
            sampleClasses.add(class3);
            
            // Update the adapter with sample data
            if (classAdapter != null) {
                classAdapter.updateClasses(sampleClasses);
                Log.d(TAG, "Sample classes loaded: " + sampleClasses.size());
            }
            
            // Update statistics with sample data
            updateStatisticsWithRealData(sampleClasses, "STUDENT");
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading sample classes", e);
        }
    }
    
    private void updateStatistics(View view, String role) {
        try {
            // Update total classes
            TextView totalClassesText = view.findViewById(R.id.tv_total_classes);
            if (totalClassesText != null) {
                if ("ADMIN".equals(role) || "Administrator".equals(role)) {
                    totalClassesText.setText("35"); // System total
                } else if ("TEACHER".equals(role) || "Teacher".equals(role)) {
                    totalClassesText.setText("3"); // Teacher's classes
                } else if ("STUDENT".equals(role) || "Student".equals(role)) {
                    totalClassesText.setText("4"); // Student's enrolled classes
                } else {
                    totalClassesText.setText("0");
                }
            }
            
            // Update total students (if this view exists)
            TextView totalStudentsText = view.findViewById(R.id.tv_total_students);
            if (totalStudentsText != null) {
                if ("ADMIN".equals(role) || "Administrator".equals(role)) {
                    totalStudentsText.setText("450"); // System total
                } else if ("TEACHER".equals(role) || "Teacher".equals(role)) {
                    totalStudentsText.setText("65"); // Total students in teacher's classes
                } else {
                    totalStudentsText.setText("N/A");
                }
            }
            
            // Update attendance rate (if this view exists)
            TextView attendanceRateText = view.findViewById(R.id.tv_average_attendance);
            if (attendanceRateText != null) {
                if ("ADMIN".equals(role) || "Administrator".equals(role)) {
                    attendanceRateText.setText("87%"); // System average
                } else if ("TEACHER".equals(role) || "Teacher".equals(role)) {
                    attendanceRateText.setText("92%"); // Teacher's class average
                } else if ("STUDENT".equals(role) || "Student".equals(role)) {
                    attendanceRateText.setText("85%"); // Student's personal attendance
                } else {
                    attendanceRateText.setText("N/A");
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating statistics", e);
        }
    }
    

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
