package com.example.studentattandance.fragments;

import android.content.Intent;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;

import com.example.studentattandance.activities.MarkAttendanceActivity;

import com.example.studentattandance.R;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.adapters.ClassForAttendanceAdapter;
import com.example.studentattandance.models.ClassForAttendance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AttendanceFragment extends Fragment implements ClassForAttendanceAdapter.OnClassActionListener {
    
    private static final String TAG = "AttendanceFragment";
    private SessionManager sessionManager;
    private TextView emptyStateText;
    private FloatingActionButton addAttendanceFab;
    private RecyclerView attendanceRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ClassForAttendanceAdapter classAdapter;
    private List<ClassForAttendance> classesList;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Creating attendance view...");
            
            sessionManager = SessionManager.getInstance(requireContext());
            
            // Use the existing fragment_attendance.xml layout
            View view = inflater.inflate(R.layout.fragment_attendance, container, false);
            
            // Initialize views
            initViews(view);
            
            // Setup RecyclerView
            setupRecyclerView();
            
            // Setup functionality based on user role
            setupRoleBasedFunctionality();
            
            Log.d(TAG, "Attendance view created successfully");
            return view;
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating attendance view", e);
            e.printStackTrace();
            
            // Fallback to simple text view if layout fails
            android.widget.TextView fallbackView = new android.widget.TextView(requireContext());
            fallbackView.setText("Attendance - Error loading view\n\n" + e.getMessage());
            fallbackView.setTextSize(16);
            fallbackView.setTextColor(android.graphics.Color.RED);
            fallbackView.setGravity(android.view.Gravity.CENTER);
            fallbackView.setPadding(50, 100, 50, 100);
            return fallbackView;
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "AttendanceFragment onResume - refreshing data from Room database");
        
        // Refresh attendance data when returning from activities
        if (sessionManager != null && sessionManager.isLoggedIn()) {
            // Add a small delay to ensure database is ready
            new android.os.Handler().postDelayed(() -> {
                loadAttendanceData();
            }, 500); // 500ms delay
        } else {
            Log.w(TAG, "SessionManager is null or user not logged in in onResume");
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AttendanceFragment destroyed");
    }
    
    private void initViews(View view) {
        try {
            emptyStateText = view.findViewById(R.id.empty_state_text);
            addAttendanceFab = view.findViewById(R.id.add_attendance_fab);
            attendanceRecycler = view.findViewById(R.id.attendance_recycler);
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }
    
    private void setupRecyclerView() {
        try {
            if (attendanceRecycler != null) {
                attendanceRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                classesList = new ArrayList<>();
                classAdapter = new ClassForAttendanceAdapter(classesList, this);
                attendanceRecycler.setAdapter(classAdapter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
        }
    }
    
    private void setupRoleBasedFunctionality() {
        try {
            if (sessionManager != null && sessionManager.isLoggedIn()) {
                String role = sessionManager.getUserRole();
                
                if ("TEACHER".equals(role) || "Teacher".equals(role)) {
                    // Teachers can mark attendance for their classes
                    setupTeacherFunctionality();
                } else if ("ADMIN".equals(role) || "Administrator".equals(role)) {
                    // Admins can view all attendance data
                    setupAdminFunctionality();
                } else if ("STUDENT".equals(role) || "Student".equals(role)) {
                    // Students can view their own attendance
                    setupStudentFunctionality();
                }
                
                // Load attendance data from Room database
                loadAttendanceData();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up role-based functionality", e);
        }
    }
    
    private void loadAttendanceData() {
        try {
            if (sessionManager == null || !sessionManager.isLoggedIn()) {
                Log.w(TAG, "User not logged in, cannot load attendance data from Room database");
                return;
            }
            
            String userRole = sessionManager.getUserRole();
            String userId = sessionManager.getUserId();
            Log.d(TAG, "Loading attendance data from Room database for user role: " + userRole + ", userId: " + userId);
            
            // For now, we'll load classes that the user can manage attendance for
            // This will be expanded to show actual attendance records
            if ("TEACHER".equals(userRole) || "Teacher".equals(userRole)) {
                // Load classes taught by this teacher from Room database
                loadTeacherClasses(userId);
            } else if ("STUDENT".equals(userRole) || "Student".equals(userRole)) {
                // Load classes where this student is enrolled from Room database
                loadStudentClasses(userId);
            } else if ("ADMIN".equals(userRole) || "Administrator".equals(userRole)) {
                // Load all classes from Room database
                loadAllClasses();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in loadAttendanceData", e);
        }
    }
    
    private void loadTeacherClasses(String teacherId) {
        try {
            Log.d(TAG, "Loading classes for teacher from Room database: " + teacherId);
            
            // This would need to be implemented in DataRepository
            // For now, we'll show a message
            Toast.makeText(requireContext(), 
                "Loading teacher classes from Room database...", Toast.LENGTH_SHORT).show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error loading teacher classes from Room database", e);
        }
    }
    
    private void loadStudentClasses(String studentId) {
        try {
            Log.d(TAG, "Loading classes for student from Room database: " + studentId);
            
            // This would need to be implemented in DataRepository
            // For now, we'll show a message
            Toast.makeText(requireContext(), 
                "Loading student classes from Room database...", Toast.LENGTH_SHORT).show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error loading student classes from Room database", e);
        }
    }
    
    private void loadAllClasses() {
        try {
            Log.d(TAG, "Loading all classes from Room database for admin");
            
            // This would need to be implemented in DataRepository
            // For now, we'll show a message
            Toast.makeText(requireContext(), 
                "Loading all classes from Room database...", Toast.LENGTH_SHORT).show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error loading all classes from Room database", e);
        }
    }
    
    private void setupTeacherFunctionality() {
        try {
            Log.d(TAG, "Setting up teacher functionality");
            
            // Show the add attendance FAB for teachers
            if (addAttendanceFab != null) {
                addAttendanceFab.setVisibility(View.VISIBLE);
                addAttendanceFab.setOnClickListener(v -> {
                    Log.d(TAG, "Teacher clicked add attendance FAB");
                    // Launch MarkAttendanceActivity for general attendance marking
                    launchMarkAttendanceActivity(null);
                });
            }
            
            // Show teacher's classes for attendance marking
            showTeacherClassesForAttendance();
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up teacher functionality", e);
        }
    }
    
    private void setupAdminFunctionality() {
        try {
            Log.d(TAG, "Setting up admin functionality");
            
            // Show the add attendance FAB for admins
            if (addAttendanceFab != null) {
                addAttendanceFab.setVisibility(View.VISIBLE);
                addAttendanceFab.setOnClickListener(v -> {
                    Log.d(TAG, "Admin clicked add attendance FAB");
                    // Launch MarkAttendanceActivity for general attendance marking
                    launchMarkAttendanceActivity(null);
                });
            }
            
            // Show all attendance data
            showAllAttendanceData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up admin functionality", e);
        }
    }
    
    private void setupStudentFunctionality() {
        try {
            Log.d(TAG, "Setting up student functionality");
            
            // Hide the add attendance FAB for students
            if (addAttendanceFab != null) {
                addAttendanceFab.setVisibility(View.GONE);
            }
            
            // Show student's attendance record
            showStudentAttendanceRecord();
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up student functionality", e);
        }
    }
    
    private void showTeacherClassesForAttendance() {
        try {
            // Create sample classes for demonstration
            List<ClassForAttendance> classes = new ArrayList<>();
            classes.add(new ClassForAttendance("1", "Mathematics 101", "Mathematics", "Mon, Wed, Fri 9:00 AM", "201", 25, "Today 9:00 AM", true));
            classes.add(new ClassForAttendance("2", "Physics 201", "Physics", "Tue, Thu 10:30 AM", "305", 18, "Yesterday 10:30 AM", true));
            classes.add(new ClassForAttendance("3", "Chemistry 101", "Chemistry", "Mon, Wed 2:00 PM", "102", 22, "Today 2:00 PM", true));
            
            // Update the adapter
            if (classAdapter != null) {
                classAdapter.updateData(classes);
                showClassesList();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing teacher classes for attendance", e);
            showEmptyState("Error loading classes: " + e.getMessage());
        }
    }
    
    private void showAllAttendanceData() {
        try {
            // Create sample classes for admin view
            List<ClassForAttendance> classes = new ArrayList<>();
            classes.add(new ClassForAttendance("1", "Mathematics 101", "Mathematics", "Mon, Wed, Fri 9:00 AM", "201", 25, "Today 9:00 AM", true));
            classes.add(new ClassForAttendance("2", "Physics 201", "Physics", "Tue, Thu 10:30 AM", "305", 18, "Yesterday 10:30 AM", true));
            classes.add(new ClassForAttendance("3", "Chemistry 101", "Chemistry", "Mon, Wed 2:00 PM", "102", 22, "Today 2:00 PM", true));
            classes.add(new ClassForAttendance("4", "English Literature", "English", "Tue, Thu 2:00 PM", "105", 20, "Today 2:00 PM", true));
            classes.add(new ClassForAttendance("5", "Computer Science", "Computer Science", "Mon, Wed, Fri 11:00 AM", "301", 30, "Yesterday 11:00 AM", true));
            
            // Update the adapter
            if (classAdapter != null) {
                classAdapter.updateData(classes);
                showClassesList();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing all attendance data", e);
            showEmptyState("Error loading attendance data: " + e.getMessage());
        }
    }
    
    private void showStudentAttendanceRecord() {
        try {
            if (emptyStateText != null) {
                emptyStateText.setText("Your Attendance Record:\n\n" +
                    "📊 Overall Performance:\n" +
                    "• Overall Attendance: 85%\n" +
                    "• Classes Attended: 17/20\n" +
                    "• Classes Missed: 3\n\n" +
                    
                    "📚 Class-wise Attendance:\n" +
                    "• Computer Science 101: 90% (18/20)\n" +
                    "• Mathematics 201: 85% (17/20)\n" +
                    "• English Literature: 80% (16/20)\n" +
                    "• History 101: 85% (17/20)\n\n" +
                    
                    "📅 Recent Attendance:\n" +
                    "• Today: Present in all classes\n" +
                    "• Yesterday: Absent in English Literature\n" +
                    "• Last Week: Present in 4/5 classes");
                emptyStateText.setVisibility(View.VISIBLE);
                
                // Hide the RecyclerView for students
                if (attendanceRecycler != null) {
                    attendanceRecycler.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing student attendance record", e);
        }
    }
    
    private void showClassesList() {
        try {
            if (attendanceRecycler != null) {
                attendanceRecycler.setVisibility(View.VISIBLE);
            }
            if (emptyStateText != null) {
                emptyStateText.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing classes list", e);
        }
    }
    
    private void showEmptyState(String message) {
        try {
            if (emptyStateText != null) {
                emptyStateText.setText(message);
                emptyStateText.setVisibility(View.VISIBLE);
            }
            if (attendanceRecycler != null) {
                attendanceRecycler.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing empty state", e);
        }
    }
    
    // Implementation of OnClassActionListener
    @Override
    public void onMarkAttendance(ClassForAttendance classItem) {
        try {
            Log.d(TAG, "Marking attendance for class: " + classItem.getClassName());
            
            // Launch MarkAttendanceActivity directly
            Intent intent = new Intent(requireContext(), MarkAttendanceActivity.class);
            intent.putExtra("class_id", classItem.getId());
            intent.putExtra("class_name", classItem.getClassName());
            intent.putExtra("subject", classItem.getSubject());
            intent.putExtra("schedule", classItem.getSchedule());
            intent.putExtra("room", classItem.getRoom());
            intent.putExtra("student_count", classItem.getStudentCount());
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error launching MarkAttendanceActivity", e);
            Toast.makeText(requireContext(), "Error opening attendance marking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onViewAttendanceHistory(ClassForAttendance classItem) {
        try {
            Log.d(TAG, "Viewing attendance history for class: " + classItem.getClassName());
            
            Toast.makeText(requireContext(), "Viewing attendance history for " + classItem.getClassName(), Toast.LENGTH_SHORT).show();
            // TODO: Launch AttendanceHistoryActivity with selected class
            // Intent intent = new Intent(requireContext(), AttendanceHistoryActivity.class);
            // intent.putExtra("class_id", classItem.getId());
            // intent.putExtra("class_name", classItem.getClassName());
            // startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling view attendance history", e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void launchMarkAttendanceActivity(ClassForAttendance classItem) {
        try {
            Intent intent = new Intent(requireContext(), MarkAttendanceActivity.class);
            
            if (classItem != null) {
                // Pass specific class data
                intent.putExtra("class_id", classItem.getId());
                intent.putExtra("class_name", classItem.getClassName());
                intent.putExtra("subject", classItem.getSubject());
                intent.putExtra("schedule", classItem.getSchedule());
                intent.putExtra("room", classItem.getRoom());
                intent.putExtra("student_count", classItem.getStudentCount());
            }
            // If classItem is null, no extras are passed (general attendance marking)
            
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error launching MarkAttendanceActivity", e);
            Toast.makeText(requireContext(), "Error opening attendance marking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
