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
import android.content.Context;

import com.example.studentattandance.R;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.adapters.UserAdapter;
import com.example.studentattandance.activities.AddUserActivity;
import com.example.studentattandance.activities.EditUserActivity;

import com.example.studentattandance.database.AppDatabase;
import com.example.studentattandance.database.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserManagementFragment extends Fragment {
    
    private static final String TAG = "UserManagementFragment";
    
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView tvTotalUsers, tvAdminUsers, tvTeacherUsers, tvStudentUsers;
    private Button btnBulkActions;
    private Button btnAddUser;
    private EditText etSearchUser;
    private Spinner spinnerRoleFilter;
    private Spinner spinnerSortBy;
    private RecyclerView rvUsers;
    private LinearLayout emptyStateUsers;
    
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private UserAdapter userAdapter;
    private List<UserEntity> usersList;
    private List<UserEntity> filteredUsersList;
    private ExecutorService executorService;
    private String currentSearchQuery = "";
    private String currentRoleFilter = "ALL";
    private String currentSortBy = "NAME";
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Creating user management view...");
            
            sessionManager = SessionManager.getInstance(requireContext());
            
            // Test database creation
            try {
                Log.d(TAG, "Testing database creation...");
                AppDatabase testDb = AppDatabase.getInstance(requireContext());
                Log.d(TAG, "Database instance created successfully");
                
                // Test if we can access the DAO
                UserDao testDao = testDb.userDao();
                Log.d(TAG, "UserDao accessed successfully");
                
                dataRepository = new DataRepository(requireContext());
                Log.d(TAG, "DataRepository created successfully");
                
            } catch (Exception dbError) {
                Log.e(TAG, "Database creation failed", dbError);
                dbError.printStackTrace();
                
                // Show error view
                TextView errorView = new TextView(requireContext());
                errorView.setText("Database Error\n\nFailed to initialize database:\n" + dbError.getMessage());
                errorView.setTextSize(16);
                errorView.setTextColor(android.graphics.Color.RED);
                errorView.setGravity(android.view.Gravity.CENTER);
                errorView.setPadding(50, 100, 50, 100);
                return errorView;
            }
            
            executorService = Executors.newSingleThreadExecutor();
            
            // Check if user is admin
            if (!isAdminUser()) {
                return createAccessDeniedView();
            }
            
            View view = inflater.inflate(R.layout.fragment_user_management, container, false);
            
            initViews(view);
            setupRecyclerView();
            setupSpinners();
            setupClickListeners();
            
            // Load initial data
            loadUsersData();
            
            Log.d(TAG, "User management view created successfully");
            return view;
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating user management view", e);
            e.printStackTrace();
            
            TextView fallbackView = new TextView(requireContext());
            fallbackView.setText("User Management - Error loading view\n\n" + e.getMessage());
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

            btnAddUser = view.findViewById(R.id.btn_add_user);
            tvTotalUsers = view.findViewById(R.id.tv_total_users);
            tvAdminUsers = view.findViewById(R.id.tv_admin_users);
            tvTeacherUsers = view.findViewById(R.id.tv_teacher_users);
            tvStudentUsers = view.findViewById(R.id.tv_student_users);
            btnBulkActions = view.findViewById(R.id.btn_bulk_actions);
            etSearchUser = view.findViewById(R.id.et_search_user);
            spinnerRoleFilter = view.findViewById(R.id.spinner_role_filter);
            spinnerSortBy = view.findViewById(R.id.spinner_sort_by);
            rvUsers = view.findViewById(R.id.rv_users);
            emptyStateUsers = view.findViewById(R.id.empty_state_users);
            
            Log.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }
    
    private void setupRecyclerView() {
        try {
            usersList = new ArrayList<>();
            userAdapter = new UserAdapter(usersList, new UserAdapter.OnUserClickListener() {
                @Override
                public void onUserClick(UserEntity user) {
                    // View user profile using ViewUserFragment
                    // For now, we'll use EditUserActivity in view mode
                    // TODO: Implement ViewUserFragment integration
                    Intent intent = new Intent(requireContext(), EditUserActivity.class);
                    intent.putExtra("userId", user.getId());
                    intent.putExtra("mode", "view");
                    startActivity(intent);
                }
                
                @Override
                public void onEditUser(UserEntity user) {
                    // Edit user profile
                    Intent intent = new Intent(requireContext(), EditUserActivity.class);
                    intent.putExtra("userId", user.getId());
                    intent.putExtra("mode", "edit");
                    startActivity(intent);
                }
                
                @Override
                public void onDeleteUser(UserEntity user) {
                    // Delete user
                    deleteUser(user);
                }
            });
            
            rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvUsers.setAdapter(userAdapter);
            
            Log.d(TAG, "RecyclerView setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
        }
    }
    
    private void setupClickListeners() {
        try {
            Log.d(TAG, "Setting up click listeners...");
            
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setOnRefreshListener(this::loadUsersData);
            }
            

            
            if (btnAddUser != null) {
                btnAddUser.setOnClickListener(v -> {
                    Log.d(TAG, "Add user button clicked");
                    Intent intent = new Intent(requireContext(), AddUserActivity.class);
                    startActivity(intent);
                });
            }
            
            if (btnBulkActions != null) {
                btnBulkActions.setOnClickListener(v -> {
                    Log.d(TAG, "Bulk actions button clicked");
                    // Add test functionality to check database
                    showDatabaseTestOptions();
                });
            }
            
            // Search functionality
            if (etSearchUser != null) {
                etSearchUser.setOnEditorActionListener((v, actionId, event) -> {
                    performUserSearch(etSearchUser.getText().toString());
                    return true;
                });
                
                // Add text change listener for real-time search
                etSearchUser.addTextChangedListener(new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    
                    @Override
                    public void afterTextChanged(android.text.Editable s) {
                        performUserSearch(s.toString());
                    }
                });
            }
            
            Log.d(TAG, "Click listeners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
        }
    }
    
    private void setupSpinners() {
        try {
            // Setup role filter spinner
            if (spinnerRoleFilter != null) {
                String[] roles = {"ALL", "ADMIN", "TEACHER", "STUDENT"};
                android.widget.ArrayAdapter<String> roleAdapter = new android.widget.ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_spinner_item, roles);
                roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRoleFilter.setAdapter(roleAdapter);
                
                spinnerRoleFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        currentRoleFilter = roles[position];
                        applyFiltersAndSort();
                    }
                    
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
            }
            
            // Setup sort by spinner
            if (spinnerSortBy != null) {
                String[] sortOptions = {"NAME", "ROLE", "CREATED DATE", "LAST LOGIN"};
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
    
    private void loadUsersData() {
        try {
            Log.d(TAG, "Loading users data from Room database...");
            
            executorService.execute(() -> {
                try {
                    Log.d(TAG, "Executing database query for users...");
                    List<UserEntity> users = dataRepository.getAllUsersSync();
                    Log.d(TAG, "Database query result: " + (users != null ? users.size() : "null") + " users");
                    
                    if (users == null) {
                        Log.d(TAG, "Users list is null, creating empty list");
                        users = new ArrayList<>();
                    }
                    
                    final List<UserEntity> finalUsers = users;
                    Log.d(TAG, "Final users list size: " + finalUsers.size());
                    
                    requireActivity().runOnUiThread(() -> {
                        Log.d(TAG, "Updating UI on main thread with " + finalUsers.size() + " users");
                        updateUsersUI(finalUsers);
                        
                        // Stop refresh animation if swipe refresh is active
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error loading users data from Room database", e);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error loading users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Stop refresh animation if swipe refresh is active
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in loadUsersData", e);
        }
    }
    
    // Sample data creation removed - data should be seeded by LoginActivity and DatabaseSeeder
    
    private void updateUsersUI(List<UserEntity> users) {
        try {
            Log.d(TAG, "updateUsersUI called with " + (users != null ? users.size() : "null") + " users from Room database");
            
            usersList = users;
            
            if (users == null || users.isEmpty()) {
                Log.d(TAG, "No users to display from Room database, showing empty state");
                showEmptyStateUsers("No users found in database");
                
                // Show helpful message and offer to create sample data
                new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("No Users Found")
                    .setMessage("The database appears to be empty. Would you like to create sample users to get started?")
                    .setPositiveButton("Create Sample Users", (dialog, which) -> {
                        createSampleUsersForFragment();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
                
            } else {
                Log.d(TAG, "Users found in Room database, hiding empty state and updating adapter");
                hideEmptyStateUsers();
                
                if (userAdapter != null) {
                    Log.d(TAG, "Updating adapter with " + users.size() + " users from Room database");
                    userAdapter.updateData(users);
                    
                    // Show success message
                    Toast.makeText(requireContext(), 
                        "Loaded " + users.size() + " users from database", Toast.LENGTH_SHORT).show();
                    
                    // Debug: Check if RecyclerView is visible
                    if (rvUsers != null) {
                        Log.d(TAG, "RecyclerView visibility: " + (rvUsers.getVisibility() == View.VISIBLE ? "VISIBLE" : "NOT VISIBLE"));
                        Log.d(TAG, "RecyclerView adapter: " + (rvUsers.getAdapter() != null ? "SET" : "NULL"));
                        Log.d(TAG, "RecyclerView layout manager: " + (rvUsers.getLayoutManager() != null ? "SET" : "NULL"));
                    } else {
                        Log.e(TAG, "RecyclerView is null!");
                    }
                } else {
                    Log.e(TAG, "userAdapter is null!");
                }
                
                // Update stats
                updateUserStats(users);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating users UI", e);
            e.printStackTrace();
        }
    }
    
    private void createSampleUsersForFragment() {
        try {
            Log.d(TAG, "Creating sample users for fragment...");
            
            executorService.execute(() -> {
                try {
                    // Create admin user
                    UserEntity adminUser = new UserEntity();
                    adminUser.setId(java.util.UUID.randomUUID().toString());
                    adminUser.setUsername("admin");
                    adminUser.setPassword("123456");
                    adminUser.setEmail("admin@test.com");
                    adminUser.setFirstName("Admin");
                    adminUser.setLastName("User");
                    adminUser.setRole("ADMIN");
                    adminUser.setCreatedAt(new java.util.Date());
                    adminUser.setLastLogin(new java.util.Date());
                    dataRepository.insertUser(adminUser);
                    
                    // Create teacher user
                    UserEntity teacherUser = new UserEntity();
                    teacherUser.setId(java.util.UUID.randomUUID().toString());
                    teacherUser.setUsername("teacher");
                    teacherUser.setPassword("123456");
                    teacherUser.setEmail("teacher@test.com");
                    teacherUser.setFirstName("Teacher");
                    teacherUser.setLastName("User");
                    teacherUser.setRole("TEACHER");
                    teacherUser.setCreatedAt(new java.util.Date());
                    teacherUser.setLastLogin(new java.util.Date());
                    dataRepository.insertUser(teacherUser);
                    
                    // Create student user
                    UserEntity studentUser = new UserEntity();
                    studentUser.setId(java.util.UUID.randomUUID().toString());
                    studentUser.setUsername("student");
                    studentUser.setPassword("123456");
                    studentUser.setEmail("student@test.com");
                    studentUser.setFirstName("Student");
                    studentUser.setLastName("User");
                    studentUser.setRole("STUDENT");
                    studentUser.setCreatedAt(new java.util.Date());
                    studentUser.setLastLogin(new java.util.Date());
                    dataRepository.insertUser(studentUser);
                    
                    Log.d(TAG, "Sample users created successfully in fragment");
                    
                    // Reload the data
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "Sample users created! Refreshing list...", Toast.LENGTH_LONG).show();
                        loadUsersData(); // Refresh the list
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error creating sample users in fragment", e);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "Error creating sample users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in createSampleUsersForFragment", e);
        }
    }
    
    private void updateUserStats(List<UserEntity> users) {
        try {
            if (tvTotalUsers != null) {
                tvTotalUsers.setText(String.valueOf(users.size()));
            }
            
            if (tvAdminUsers != null) {
                long adminCount = users.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
                tvAdminUsers.setText(String.valueOf(adminCount));
            }
            
            if (tvTeacherUsers != null) {
                long teacherCount = users.stream().filter(u -> "TEACHER".equals(u.getRole())).count();
                tvTeacherUsers.setText(String.valueOf(teacherCount));
            }
            
            if (tvStudentUsers != null) {
                long studentCount = users.stream().filter(u -> "STUDENT".equals(u.getRole())).count();
                tvStudentUsers.setText(String.valueOf(studentCount));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating user stats", e);
        }
    }
    
    private void showEmptyStateUsers(String message) {
        try {
            if (emptyStateUsers != null) {
                emptyStateUsers.setVisibility(View.VISIBLE);
            }
            if (rvUsers != null) {
                rvUsers.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing empty state users", e);
        }
    }
    
    private void hideEmptyStateUsers() {
        try {
            if (emptyStateUsers != null) {
                emptyStateUsers.setVisibility(View.GONE);
            }
            if (rvUsers != null) {
                rvUsers.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding empty state users", e);
        }
    }
    
    private void performUserSearch(String query) {
        try {
            currentSearchQuery = query != null ? query.trim() : "";
            applyFiltersAndSort();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in performUserSearch", e);
        }
    }
    
    private void applyFiltersAndSort() {
        try {
            if (usersList == null) {
                return;
            }
            
            // Apply search filter
            List<UserEntity> searchFiltered = new ArrayList<>();
            if (currentSearchQuery.isEmpty()) {
                searchFiltered = new ArrayList<>(usersList);
            } else {
                for (UserEntity user : usersList) {
                    if (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                        user.getLastName() != null && user.getLastName().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                        user.getUsername() != null && user.getUsername().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                        user.getEmail() != null && user.getEmail().toLowerCase().contains(currentSearchQuery.toLowerCase())) {
                        searchFiltered.add(user);
                    }
                }
            }
            
            // Apply role filter
            List<UserEntity> roleFiltered = new ArrayList<>();
            if ("ALL".equals(currentRoleFilter)) {
                roleFiltered = new ArrayList<>(searchFiltered);
            } else {
                for (UserEntity user : searchFiltered) {
                    if (currentRoleFilter.equals(user.getRole())) {
                        roleFiltered.add(user);
                    }
                }
            }
            
            // Apply sorting
            List<UserEntity> sortedList = sortUsers(roleFiltered);
            
            // Update UI with filtered and sorted results
            filteredUsersList = sortedList;
            updateUsersUI(filteredUsersList);
            
        } catch (Exception e) {
            Log.e(TAG, "Error applying filters and sort", e);
        }
    }
    
    private List<UserEntity> sortUsers(List<UserEntity> users) {
        try {
            List<UserEntity> sortedUsers = new ArrayList<>(users);
            
            switch (currentSortBy) {
                case "NAME":
                    sortedUsers.sort((u1, u2) -> {
                        String name1 = (u1.getFirstName() + " " + u1.getLastName()).toLowerCase();
                        String name2 = (u2.getFirstName() + " " + u2.getLastName()).toLowerCase();
                        return name1.compareTo(name2);
                    });
                    break;
                    
                case "ROLE":
                    sortedUsers.sort((u1, u2) -> {
                        String role1 = u1.getRole() != null ? u1.getRole() : "";
                        String role2 = u2.getRole() != null ? u2.getRole() : "";
                        return role1.compareTo(role2);
                    });
                    break;
                    
                case "CREATED DATE":
                    sortedUsers.sort((u1, u2) -> {
                        if (u1.getCreatedAt() == null && u2.getCreatedAt() == null) return 0;
                        if (u1.getCreatedAt() == null) return 1;
                        if (u2.getCreatedAt() == null) return -1;
                        return u2.getCreatedAt().compareTo(u1.getCreatedAt()); // Newest first
                    });
                    break;
                    
                case "LAST LOGIN":
                    sortedUsers.sort((u1, u2) -> {
                        if (u1.getLastLogin() == null && u2.getLastLogin() == null) return 0;
                        if (u1.getLastLogin() == null) return 1;
                        if (u2.getLastLogin() == null) return -1;
                        return u2.getLastLogin().compareTo(u1.getLastLogin()); // Newest first
                    });
                    break;
            }
            
            return sortedUsers;
            
        } catch (Exception e) {
            Log.e(TAG, "Error sorting users", e);
            return users;
        }
    }
    
    private void deleteUser(UserEntity user) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete " + user.getFirstName() + " " + user.getLastName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                performDeleteUser(user);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void performDeleteUser(UserEntity user) {
        executorService.execute(() -> {
            try {
                dataRepository.deleteUser(user);
                
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), 
                                 "User deleted successfully", Toast.LENGTH_SHORT).show();
                    loadUsersData(); // Reload the list
                });
                
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), 
                                 "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void testDatabaseConnection() {
        try {
            Log.d(TAG, "Testing database connection...");
            
            executorService.execute(() -> {
                try {
                    // Test basic database operations
                    List<UserEntity> users = dataRepository.getAllUsersSync();
                    Log.d(TAG, "Database test - Current users count: " + (users != null ? users.size() : "null"));
                    
                    if (users == null || users.isEmpty()) {
                        Log.d(TAG, "No users found in Room database. Data should be seeded by LoginActivity.");
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), 
                                "No users found. Please log out and log back in to seed the database.", Toast.LENGTH_LONG).show();
                        });
                    } else {
                        Log.d(TAG, "Users found in database, showing count");
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), 
                                "Database working! Found " + users.size() + " users", Toast.LENGTH_LONG).show();
                        });
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Database test failed", e);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "Database test failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in testDatabaseConnection", e);
        }
    }
    
    private void showDatabaseTestOptions() {
        try {
            String[] options = {
                "Test Database Connection",
                "Test Database Directly", 
                "Check Database Status",
                "Reset Database",
                "Cancel"
            };
            
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Database Test Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            testDatabaseConnection();
                            break;
                        case 1:
                            testDatabaseDirectly();
                            break;
                        case 2:
                            checkDatabaseStatus();
                            break;
                        case 3:
                            resetDatabase();
                            break;
                        case 4:
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing database test options", e);
        }
    }
    
    private void testDatabaseDirectly() {
        try {
            Log.d(TAG, "Testing database directly...");
            
            executorService.execute(() -> {
                try {
                    // Test database instance directly
                    Context appContext = requireContext().getApplicationContext();
                    AppDatabase database = AppDatabase.getInstance(appContext);
                    
                    if (database == null) {
                        Log.e(TAG, "Database instance is null");
                        showDatabaseError("Database instance is null");
                        return;
                    }
                    
                    if (!database.isOpen()) {
                        Log.e(TAG, "Database is not open");
                        showDatabaseError("Database is not open");
                        return;
                    }
                    
                    // Test DAO access
                    UserDao userDao = database.userDao();
                    if (userDao == null) {
                        Log.e(TAG, "UserDao is null");
                        showDatabaseError("UserDao is null");
                        return;
                    }
                    
                    // Test a simple query
                    List<UserEntity> users = userDao.getAllUsersSync();
                    Log.d(TAG, "Direct database test - Users count: " + (users != null ? users.size() : "null"));
                    
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "Direct database test successful! Found " + (users != null ? users.size() : 0) + " users", Toast.LENGTH_LONG).show();
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Direct database test failed", e);
                    showDatabaseError("Direct database test failed: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in testDatabaseDirectly", e);
        }
    }
    
    private void checkDatabaseStatus() {
        try {
            Log.d(TAG, "Checking database status...");
            
            executorService.execute(() -> {
                try {
                    Context appContext = requireContext().getApplicationContext();
                    AppDatabase database = AppDatabase.getInstance(appContext);
                    
                    StringBuilder status = new StringBuilder();
                    status.append("Database Status:\n\n");
                    
                    if (database == null) {
                        status.append("❌ Database instance: NULL\n");
                    } else {
                        status.append("✅ Database instance: EXISTS\n");
                        status.append("📊 Database open: ").append(database.isOpen() ? "YES" : "NO").append("\n");
                        
                        try {
                            String dbPath = database.getOpenHelper().getDatabaseName();
                            status.append("📁 Database name: ").append(dbPath).append("\n");
                        } catch (Exception e) {
                            status.append("❌ Database path: ERROR\n");
                        }
                    }
                    
                    if (dataRepository == null) {
                        status.append("❌ DataRepository: NULL\n");
                    } else {
                        status.append("✅ DataRepository: EXISTS\n");
                    }
                    
                    final String statusText = status.toString();
                    
                    requireActivity().runOnUiThread(() -> {
                        new android.app.AlertDialog.Builder(requireContext())
                            .setTitle("Database Status")
                            .setMessage(statusText)
                            .setPositiveButton("OK", null)
                            .show();
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error checking database status", e);
                    showDatabaseError("Error checking status: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in checkDatabaseStatus", e);
        }
    }
    
    private void resetDatabase() {
        try {
            new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Reset Database")
                .setMessage("This will reset the database instance. Continue?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            Log.d(TAG, "Resetting database...");
                            AppDatabase.resetDatabase();
                            
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Database reset. Please restart the app.", Toast.LENGTH_LONG).show();
                            });
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error resetting database", e);
                            showDatabaseError("Error resetting database: " + e.getMessage());
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error in resetDatabase", e);
        }
    }
    
    private void showDatabaseError(String message) {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Database Error: " + message, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Database Error: " + message);
            
            // Also show in the UI
            if (rvUsers != null) rvUsers.setVisibility(View.GONE);
            if (emptyStateUsers != null) {
                emptyStateUsers.setVisibility(View.VISIBLE);
                // Update the empty state text
                for (int i = 0; i < ((ViewGroup) emptyStateUsers).getChildCount(); i++) {
                    View child = ((ViewGroup) emptyStateUsers).getChildAt(i);
                    if (child instanceof TextView) {
                        TextView textView = (TextView) child;
                        textView.setText("Database Error: " + message);
                        textView.setTextColor(android.graphics.Color.RED);
                        break;
                    }
                }
            }
        });
    }
    
    // Sample users creation removed - data should be seeded by LoginActivity and DatabaseSeeder
    

    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "UserManagementFragment onResume - refreshing data from Room database");
        
        // Refresh user list when returning from activities
        if (dataRepository != null) {
            // Add a small delay to ensure database is ready
            new android.os.Handler().postDelayed(() -> {
                loadUsersData();
            }, 500); // 500ms delay
        } else {
            Log.w(TAG, "DataRepository is null in onResume");
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.d(TAG, "UserManagementFragment destroyed");
    }
}
