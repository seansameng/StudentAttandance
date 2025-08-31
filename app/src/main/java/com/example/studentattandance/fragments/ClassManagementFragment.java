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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout;

import com.example.studentattandance.R;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.adapters.ClassesAdapter;
import com.example.studentattandance.activities.AddClassActivity;
import com.example.studentattandance.activities.EditClassActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassManagementFragment extends Fragment {
    
    private static final String TAG = "ClassManagementFragment";
    
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTotalClasses, tvActiveClasses, tvTotalStudents;
    private Button btnManageEnrollments, btnAddClass;
    private EditText etSearchClass;
    private Spinner spinnerFilter, spinnerSortBy;
    private RecyclerView rvClasses;
    private LinearLayout emptyStateClasses;
    
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ClassesAdapter classesAdapter;
    private List<ClassEntity> classesList;
    private List<ClassEntity> filteredClassesList;
    private ExecutorService executorService;
    private String currentSearchQuery = "";
    private String currentFilter = "ALL";
    private String currentSortBy = "NAME";
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Creating class management view...");
            
            sessionManager = SessionManager.getInstance(requireContext());
            dataRepository = new DataRepository(requireContext());
            executorService = Executors.newSingleThreadExecutor();
            
            // Check if user is admin
            if (!isAdminUser()) {
                return createAccessDeniedView();
            }
            
            View view = inflater.inflate(R.layout.fragment_class_management, container, false);
            
            initViews(view);
            setupRecyclerView();
            setupClickListeners();
            
            // Load initial data
            loadClassesData();
            
            Log.d(TAG, "Class management view created successfully");
            return view;
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating class management view", e);
            e.printStackTrace();
            
            TextView fallbackView = new TextView(requireContext());
            fallbackView.setText("Class Management - Error loading view\n\n" + e.getMessage());
            fallbackView.setTextSize(16);
            fallbackView.setTextColor(android.graphics.Color.RED);
            fallbackView.setGravity(android.view.Gravity.CENTER);
            fallbackView.setPadding(50, 100, 50, 100);
            return fallbackView;
        }
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
    
    private View createAccessDeniedView() {
        TextView accessDeniedView = new TextView(requireContext());
        accessDeniedView.setText("Access Denied\n\nOnly administrators can access this section.");
        accessDeniedView.setTextSize(18);
        accessDeniedView.setTextColor(android.graphics.Color.RED);
        accessDeniedView.setGravity(android.view.Gravity.CENTER);
        accessDeniedView.setPadding(50, 100, 50, 100);
        return accessDeniedView;
    }
    
    private void initViews(View view) {
        try {
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
            tvTotalClasses = view.findViewById(R.id.tv_total_classes);
            tvActiveClasses = view.findViewById(R.id.tv_active_classes);
            tvTotalStudents = view.findViewById(R.id.tv_total_students);
            btnManageEnrollments = view.findViewById(R.id.btn_manage_enrollments);
            btnAddClass = view.findViewById(R.id.btn_add_class);
            etSearchClass = view.findViewById(R.id.et_search_class);
            spinnerFilter = view.findViewById(R.id.spinner_filter);
            spinnerSortBy = view.findViewById(R.id.spinner_sort_by);
            rvClasses = view.findViewById(R.id.rv_classes);
            emptyStateClasses = view.findViewById(R.id.empty_state_classes);
            
            // Debug logging for view initialization
            if (swipeRefreshLayout != null) {
                Log.d(TAG, "swipeRefreshLayout found successfully");
            } else {
                Log.e(TAG, "swipeRefreshLayout is NULL - not found in layout!");
            }
            
            if (btnAddClass != null) {
                Log.d(TAG, "btnAddClass found successfully - ID: " + btnAddClass.getId());
            } else {
                Log.e(TAG, "btnAddClass is NULL - button not found in layout!");
            }
            
            if (btnManageEnrollments != null) {
                Log.d(TAG, "btnManageEnrollments found successfully");
            } else {
                Log.e(TAG, "btnManageEnrollments is NULL - button not found in layout!");
            }
            
            Log.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }
    
    private void setupRecyclerView() {
        try {
            classesList = new ArrayList<>();
            classesAdapter = new ClassesAdapter(classesList, false);
            classesAdapter.setOnClassClickListener(classObj -> {
                // Handle class click - show options
                showClassOptions(classObj);
            });
            
            rvClasses.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvClasses.setAdapter(classesAdapter);
            
            Log.d(TAG, "RecyclerView setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
        }
    }
    
    private void setupClickListeners() {
        try {
            Log.d(TAG, "setupClickListeners started");
            
            // Setup SwipeRefreshLayout if available
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setOnRefreshListener(this::loadClassesData);
                Log.d(TAG, "SwipeRefreshLayout listener set successfully");
            } else {
                Log.w(TAG, "SwipeRefreshLayout is null - skipping refresh listener");
            }
            
            // Add New Class button - THIS IS THE KEY FIX
            if (btnAddClass != null) {
                Log.d(TAG, "Setting up Add New Class button click listener");
                btnAddClass.setOnClickListener(v -> {
                    Log.d(TAG, "Add New Class button clicked!");
                    Toast.makeText(requireContext(), "Add New Class button clicked!", Toast.LENGTH_SHORT).show();
                    
                    // Launch AddClassActivity
                    try {
                        Log.d(TAG, "Attempting to launch AddClassActivity...");
                        Intent intent = new Intent(requireContext(), AddClassActivity.class);
                        Log.d(TAG, "Intent created: " + intent.toString());
                        startActivity(intent);
                        Log.d(TAG, "AddClassActivity launched successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error launching AddClassActivity", e);
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                Log.d(TAG, "Add New Class button click listener set successfully");
            } else {
                Log.e(TAG, "btnAddClass is null - cannot set click listener");
            }
            
            // Manage Enrollments button
            if (btnManageEnrollments != null) {
                btnManageEnrollments.setOnClickListener(v -> {
                    Log.d(TAG, "Manage enrollments button clicked");
                    Toast.makeText(requireContext(), "Manage Enrollments - Coming Soon!", Toast.LENGTH_SHORT).show();
                });
            }
            
            // Search functionality
            if (etSearchClass != null) {
                etSearchClass.setOnEditorActionListener((v, actionId, event) -> {
                    performClassSearch(etSearchClass.getText().toString());
                    return true;
                });
                
                // Add text change listener for real-time search
                etSearchClass.addTextChangedListener(new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    
                    @Override
                    public void afterTextChanged(android.text.Editable s) {
                        performClassSearch(s.toString());
                    }
                });
            }
            
            // Setup spinners
            setupSpinners();
            
            Log.d(TAG, "Click listeners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
        }
    }
    
    private void showClassOptions(ClassEntity classObj) {
        String[] options = {"View Details", "Edit Class", "Manage Enrollments", "View Attendance", "Delete Class"};
        
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Class: " + classObj.getClassName())
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // View Details
                        // Launch EditClassActivity in view mode
                        Intent viewIntent = new Intent(requireContext(), EditClassActivity.class);
                        viewIntent.putExtra("classId", classObj.getId());
                        viewIntent.putExtra("mode", "view");
                        startActivity(viewIntent);
                        break;
                    case 1: // Edit Class
                        // Launch EditClassActivity in edit mode
                        Intent editIntent = new Intent(requireContext(), EditClassActivity.class);
                        editIntent.putExtra("classId", classObj.getId());
                        editIntent.putExtra("mode", "edit");
                        startActivity(editIntent);
                        break;
                    case 2: // Manage Enrollments
                        Toast.makeText(requireContext(), "Manage Enrollments - Coming Soon!", Toast.LENGTH_SHORT).show();
                        break;
                    case 3: // View Attendance
                        Toast.makeText(requireContext(), "View Attendance - Coming Soon!", Toast.LENGTH_SHORT).show();
                        break;
                    case 4: // Delete Class
                        deleteClass(classObj);
                        break;
                }
            })
            .show();
    }
    
    private void deleteClass(ClassEntity classObj) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Class")
            .setMessage("Are you sure you want to delete " + classObj.getClassName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                performDeleteClass(classObj);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void performDeleteClass(ClassEntity classObj) {
        executorService.execute(() -> {
            try {
                dataRepository.deleteClass(classObj);
                
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), 
                                 "Class deleted successfully", Toast.LENGTH_SHORT).show();
                    loadClassesData(); // Reload the list
                });
                
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), 
                                 "Error deleting class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void setupSpinners() {
        try {
            // Setup filter spinner
            if (spinnerFilter != null) {
                String[] filterOptions = {"ALL", "ACTIVE", "INACTIVE"};
                android.widget.ArrayAdapter<String> filterAdapter = new android.widget.ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_spinner_item, filterOptions);
                filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFilter.setAdapter(filterAdapter);
                
                spinnerFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        currentFilter = filterOptions[position];
                        applyFiltersAndSort();
                    }
                    
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
            }
            
            // Setup sort by spinner
            if (spinnerSortBy != null) {
                String[] sortOptions = {"NAME", "SUBJECT", "CAPACITY", "CREATED DATE"};
                android.widget.ArrayAdapter<String> sortAdapter = new android.widget.ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_spinner_item, sortOptions);
                sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSortBy.setAdapter(sortAdapter);
                
                spinnerSortBy.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        currentSortBy = sortOptions[position];
                        applyFiltersAndSort();
                    }
                    
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
            }
            
            Log.d(TAG, "Spinners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up spinners", e);
        }
    }
    
    private void loadClassesData() {
        try {
            Log.d(TAG, "Loading classes data...");
            
            executorService.execute(() -> {
                try {
                    Log.d(TAG, "Executing database query for classes...");
                    List<ClassEntity> classes = dataRepository.getAllClassesSync();
                    Log.d(TAG, "Database query result: " + (classes != null ? classes.size() : "null") + " classes");
                    
                    if (classes == null) {
                        Log.d(TAG, "Classes list is null, creating empty list");
                        classes = new ArrayList<>();
                    }
                    
                    // If no classes found, show empty state (data should be seeded by LoginActivity)
                    if (classes.isEmpty()) {
                        Log.d(TAG, "No classes found in database. Data should be seeded by LoginActivity.");
                        Log.d(TAG, "Please log out and log back in to seed the database.");
                    }
                    
                    final List<ClassEntity> finalClasses = classes;
                    Log.d(TAG, "Final classes list size: " + finalClasses.size());
                    
                    requireActivity().runOnUiThread(() -> {
                        Log.d(TAG, "Updating UI on main thread with " + finalClasses.size() + " classes");
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
    
    // Sample class creation removed - data should be seeded by LoginActivity and DatabaseSeeder
    
    private void updateClassesUI(List<ClassEntity> classes) {
        try {
            Log.d(TAG, "updateClassesUI called with " + (classes != null ? classes.size() : "null") + " classes");
            
            classesList = classes;
            
            if (classes == null || classes.isEmpty()) {
                Log.d(TAG, "No classes to display, showing empty state");
                showEmptyStateClasses("No classes found");
            } else {
                Log.d(TAG, "Classes found, hiding empty state and updating adapter");
                hideEmptyStateClasses();
                
                if (classesAdapter != null) {
                    Log.d(TAG, "Updating adapter with " + classes.size() + " classes");
                    classesAdapter.updateClasses(classes);
                } else {
                    Log.e(TAG, "classesAdapter is null!");
                }
                
                // Update stats
                updateClassStats(classes);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating classes UI", e);
        }
    }
    
    private void updateClassStats(List<ClassEntity> classes) {
        try {
            if (tvTotalClasses != null) {
                tvTotalClasses.setText(String.valueOf(classes.size()));
            }
            
            if (tvActiveClasses != null) {
                // For now, assume all classes are active since ClassEntity doesn't have isActive field
                tvActiveClasses.setText(String.valueOf(classes.size()));
            }
            
            if (tvTotalStudents != null) {
                // This would need to be calculated from enrollments
                tvTotalStudents.setText("0");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating class stats", e);
        }
    }
    
    private void showEmptyStateClasses(String message) {
        try {
            if (emptyStateClasses != null) {
                emptyStateClasses.setVisibility(View.VISIBLE);
            }
            if (rvClasses != null) {
                rvClasses.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing empty state classes", e);
        }
    }
    
    private void hideEmptyStateClasses() {
        try {
            if (emptyStateClasses != null) {
                emptyStateClasses.setVisibility(View.GONE);
            }
            if (rvClasses != null) {
                rvClasses.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding empty state classes", e);
        }
    }
    
    private void performClassSearch(String query) {
        try {
            currentSearchQuery = query;
            applyFiltersAndSort();
        } catch (Exception e) {
            Log.e(TAG, "Error performing class search", e);
        }
    }
    
    private void applyFiltersAndSort() {
        try {
            if (classesList == null) return;
            
            // Apply search filter
            List<ClassEntity> filteredList = new ArrayList<>();
            for (ClassEntity classEntity : classesList) {
                if (matchesSearch(classEntity) && matchesFilter(classEntity)) {
                    filteredList.add(classEntity);
                }
            }
            
            // Apply sorting
            List<ClassEntity> sortedList = sortClasses(filteredList);
            
            // Update UI
            filteredClassesList = sortedList;
            updateClassesUI(sortedList);
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying filters and sort", e);
        }
    }
    
    private boolean matchesSearch(ClassEntity classEntity) {
        if (currentSearchQuery == null || currentSearchQuery.trim().isEmpty()) {
            return true;
        }
        
        String query = currentSearchQuery.toLowerCase().trim();
        return classEntity.getClassName().toLowerCase().contains(query) ||
               (classEntity.getSubject() != null && classEntity.getSubject().toLowerCase().contains(query)) ||
               (classEntity.getSchedule() != null && classEntity.getSchedule().toLowerCase().contains(query)) ||
               (classEntity.getRoom() != null && classEntity.getRoom().toLowerCase().contains(query));
    }
    
    private boolean matchesFilter(ClassEntity classEntity) {
        if ("ALL".equals(currentFilter)) {
            return true;
        } else if ("ACTIVE".equals(currentFilter)) {
            // For now, assume all classes are active since ClassEntity doesn't have isActive field
            return true;
        } else if ("INACTIVE".equals(currentFilter)) {
            // For now, assume no classes are inactive since ClassEntity doesn't have isActive field
            return false;
        }
        return true;
    }
    
    private List<ClassEntity> sortClasses(List<ClassEntity> classes) {
        try {
            List<ClassEntity> sortedClasses = new ArrayList<>(classes);
            
            switch (currentSortBy) {
                case "NAME":
                    sortedClasses.sort((c1, c2) -> {
                        String name1 = c1.getClassName() != null ? c1.getClassName().toLowerCase() : "";
                        String name2 = c2.getClassName() != null ? c2.getClassName().toLowerCase() : "";
                        return name1.compareTo(name2);
                    });
                    break;
                    
                case "SUBJECT":
                    sortedClasses.sort((c1, c2) -> {
                        String subject1 = c1.getSubject() != null ? c1.getSubject().toLowerCase() : "";
                        String subject2 = c2.getSubject() != null ? c2.getSubject().toLowerCase() : "";
                        return subject1.compareTo(subject2);
                    });
                    break;
                    
                case "CAPACITY":
                    // Use maxStudents instead of capacity since ClassEntity doesn't have capacity field
                    sortedClasses.sort((c1, c2) -> Integer.compare(c1.getMaxStudents(), c2.getMaxStudents()));
                    break;
                    
                case "CREATED DATE":
                    sortedClasses.sort((c1, c2) -> {
                        if (c1.getCreatedAt() == null && c2.getCreatedAt() == null) return 0;
                        if (c1.getCreatedAt() == null) return 1;
                        if (c2.getCreatedAt() == null) return -1;
                        return c2.getCreatedAt().compareTo(c1.getCreatedAt()); // Newest first
                    });
                    break;
            }
            
            return sortedClasses;
            
        } catch (Exception e) {
            Log.e(TAG, "Error sorting classes", e);
            return classes;
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh class list when returning from activities
        if (dataRepository != null) {
            loadClassesData();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.d(TAG, "ClassManagementFragment destroyed");
    }
}
