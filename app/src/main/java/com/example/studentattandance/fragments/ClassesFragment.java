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
import android.util.Log;
import android.content.Intent;

import com.example.studentattandance.R;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.adapters.ClassesAdapter;
import com.example.studentattandance.activities.MarkAttendanceActivity;
import com.example.studentattandance.activities.ViewAttendanceActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassesFragment extends Fragment {
    
    private static final String TAG = "ClassesFragment";
    private SessionManager sessionManager;
    private DataRepository dataRepository;
    private TextView emptyStateText;
    private FloatingActionButton addClassFab;
    private RecyclerView classesRecycler;
    private ClassesAdapter classesAdapter;
    private ExecutorService executorService;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Creating classes view...");
            
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
            
            // Use the existing fragment_classes.xml layout
            View view = inflater.inflate(R.layout.fragment_classes, container, false);
            
            // Initialize views
            initViews(view);
            
            // Setup RecyclerView
            setupRecyclerView();
            
            // Setup functionality based on user role
            setupRoleBasedFunctionality();
            
            // Load classes data (only if database is available)
            if (dataRepository != null && executorService != null) {
                loadClassesData();
            } else {
                Log.w(TAG, "Database not available, skipping class loading");
            }
            
            Log.d(TAG, "Classes view created successfully");
            return view;
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating classes view", e);
            e.printStackTrace();
            return createFallbackView("Error loading classes: " + e.getMessage());
        }
    }
    
    private TextView createFallbackView(String message) {
        TextView fallbackView = new TextView(requireContext());
        fallbackView.setText("Classes - " + message + "\n\nPlease restart the app or contact support.");
        fallbackView.setTextSize(16);
        fallbackView.setTextColor(android.graphics.Color.RED);
        fallbackView.setGravity(android.view.Gravity.CENTER);
        fallbackView.setPadding(50, 100, 50, 100);
        return fallbackView;
    }
    
    private void initViews(View view) {
        try {
            emptyStateText = view.findViewById(R.id.empty_state_text);
            addClassFab = view.findViewById(R.id.add_class_fab);
            classesRecycler = view.findViewById(R.id.classes_recycler);
            
            Log.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }
    
    private void setupRecyclerView() {
        try {
            if (classesRecycler != null) {
                classesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                classesAdapter = new ClassesAdapter(new ArrayList<>(), false);
                classesRecycler.setAdapter(classesAdapter);
                
                // Set click listener for class items
                classesAdapter.setOnClassClickListener(new ClassesAdapter.OnClassClickListener() {
                    @Override
                    public void onClassClick(ClassEntity classObj) {
                        handleClassClick(classObj);
                    }
                });
                
                Log.d(TAG, "RecyclerView setup completed");
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
                    // Teachers can add classes and manage their classes
                    setupTeacherFunctionality();
                } else if ("ADMIN".equals(role) || "Administrator".equals(role)) {
                    // Admins can add and manage all classes
                    setupAdminFunctionality();
                } else if ("STUDENT".equals(role) || "Student".equals(role)) {
                    // Students can only view classes
                    setupStudentFunctionality();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up role-based functionality", e);
        }
    }
    
    private void setupTeacherFunctionality() {
        try {
            Log.d(TAG, "Setting up teacher functionality");
            
            // Show the add class FAB for teachers
            if (addClassFab != null) {
                addClassFab.setVisibility(View.VISIBLE);
                addClassFab.setOnClickListener(v -> {
                    Log.d(TAG, "Teacher clicked add class FAB");
                    Toast.makeText(requireContext(), "Add Class - Coming Soon!", Toast.LENGTH_SHORT).show();
                });
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up teacher functionality", e);
        }
    }
    
    private void setupAdminFunctionality() {
        try {
            Log.d(TAG, "Setting up admin functionality");
            
            // Show the add class FAB for admins
            if (addClassFab != null) {
                addClassFab.setVisibility(View.VISIBLE);
                addClassFab.setOnClickListener(v -> {
                    Log.d(TAG, "Admin clicked add class FAB");
                    Toast.makeText(requireContext(), "Add Class - Coming Soon!", Toast.LENGTH_SHORT).show();
                });
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up admin functionality", e);
        }
    }
    
    private void setupStudentFunctionality() {
        try {
            Log.d(TAG, "Setting up student functionality");
            
            // Hide the add class FAB for students
            if (addClassFab != null) {
                addClassFab.setVisibility(View.GONE);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up student functionality", e);
        }
    }
    
    private void loadClassesData() {
        try {
            if (sessionManager == null || !sessionManager.isLoggedIn()) {
                Log.w(TAG, "User not logged in, cannot load classes");
                return;
            }
            
            String userRole = sessionManager.getUserRole();
            Log.d(TAG, "Loading classes for user role: " + userRole);
            
            executorService.execute(() -> {
                try {
                    List<ClassEntity> classes = null;
                    
                    if ("TEACHER".equals(userRole) || "Teacher".equals(userRole)) {
                        // Load classes taught by this teacher
                        classes = dataRepository.getClassesByTeacherSync(sessionManager.getUserId());
                    } else if ("STUDENT".equals(userRole) || "Student".equals(userRole)) {
                        // Load classes where this student is enrolled
                        classes = dataRepository.getEnrolledClasses(sessionManager.getUserId());
                    } else if ("ADMIN".equals(userRole) || "Administrator".equals(userRole)) {
                        // Load all classes in the system
                        classes = dataRepository.getAllClassesSync();
                    }
                    
                    if (classes == null) {
                        classes = new ArrayList<>();
                    }
                    
                    final List<ClassEntity> finalClasses = classes;
                    
                    requireActivity().runOnUiThread(() -> {
                        updateClassesUI(finalClasses);
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error loading classes data", e);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error loading classes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in loadClassesData", e);
        }
    }
    
    private void updateClassesUI(List<ClassEntity> classes) {
        try {
            Log.d(TAG, "updateClassesUI called with " + (classes != null ? classes.size() : "null") + " classes from Room database");
            
            if (classes == null || classes.isEmpty()) {
                Log.d(TAG, "No classes to display from Room database, showing empty state");
                showEmptyState();
            } else {
                Log.d(TAG, "Classes found in Room database, hiding empty state and updating adapter");
                hideEmptyState();
                if (classesAdapter != null) {
                    Log.d(TAG, "Updating adapter with " + classes.size() + " classes from Room database");
                    classesAdapter.updateClasses(classes);
                } else {
                    Log.e(TAG, "classesAdapter is null!");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating classes UI with Room database data", e);
        }
    }
    
    private void handleClassClick(ClassEntity classObj) {
        try {
            if (sessionManager == null || !sessionManager.isLoggedIn()) {
                return;
            }
            
            String userRole = sessionManager.getUserRole();
            Log.d(TAG, "Class clicked: " + classObj.getClassName() + " by user with role: " + userRole);
            
            if ("TEACHER".equals(userRole) || "Teacher".equals(userRole)) {
                // Show options for teachers
                showTeacherClassOptions(classObj);
            } else if ("STUDENT".equals(userRole) || "Student".equals(userRole)) {
                // Show options for students
                showStudentClassOptions(classObj);
            } else if ("ADMIN".equals(userRole) || "Administrator".equals(userRole)) {
                // Show options for admins
                showAdminClassOptions(classObj);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling class click", e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showTeacherClassOptions(final ClassEntity classObj) {
        try {
            String[] options = {"Mark Attendance", "View Attendance", "View Students", "Class Details"};
            
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Class: " + classObj.getClassName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Mark Attendance
                            launchMarkAttendance(classObj);
                            break;
                        case 1: // View Attendance
                            launchViewAttendance("class", classObj.getId());
                            break;
                        case 2: // View Students
                            showClassStudents(classObj);
                            break;
                        case 3: // Class Details
                            showClassDetails(classObj);
                            break;
                    }
                })
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing teacher class options", e);
        }
    }
    
    private void showStudentClassOptions(final ClassEntity classObj) {
        try {
            String[] options = {"View My Attendance", "Class Details"};
            
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Class: " + classObj.getClassName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // View My Attendance
                            launchViewAttendance("individual", classObj.getId());
                            break;
                        case 1: // Class Details
                            showClassDetails(classObj);
                            break;
                    }
                })
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing student class options", e);
        }
    }
    
    private void showAdminClassOptions(final ClassEntity classObj) {
        try {
            String[] options = {"View Attendance", "View Students", "Class Details", "Edit Class"};
            
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Class: " + classObj.getClassName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // View Attendance
                            launchViewAttendance("class", classObj.getId());
                            break;
                        case 1: // View Students
                            showClassStudents(classObj);
                            break;
                        case 2: // Class Details
                            showClassDetails(classObj);
                            break;
                        case 3: // Edit Class
                            Toast.makeText(requireContext(), "Edit Class - Coming Soon!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing admin class options", e);
        }
    }
    
    private void launchMarkAttendance(ClassEntity classObj) {
        try {
            Intent intent = new Intent(requireContext(), MarkAttendanceActivity.class);
            intent.putExtra("CLASS_ID", classObj.getId());
            intent.putExtra("CLASS_NAME", classObj.getClassName());
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching mark attendance", e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void launchViewAttendance(String type, String classId) {
        try {
            Intent intent = new Intent(requireContext(), ViewAttendanceActivity.class);
            intent.putExtra("TYPE", type);
            intent.putExtra("CLASS_ID", classId);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching view attendance", e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showClassStudents(ClassEntity classObj) {
        try {
            Toast.makeText(requireContext(), "View Students - Coming Soon!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing class students", e);
        }
    }
    
    private void showClassDetails(ClassEntity classObj) {
        try {
            Toast.makeText(requireContext(), "Class Details - Coming Soon!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing class details", e);
        }
    }
    
    private void showEmptyState() {
        try {
            if (emptyStateText != null) {
                emptyStateText.setVisibility(View.VISIBLE);
            }
            if (classesRecycler != null) {
                classesRecycler.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing empty state", e);
        }
    }
    
    private void hideEmptyState() {
        try {
            if (emptyStateText != null) {
                emptyStateText.setVisibility(View.GONE);
            }
            if (classesRecycler != null) {
                classesRecycler.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding empty state", e);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.d(TAG, "ClassesFragment destroyed");
    }
}
