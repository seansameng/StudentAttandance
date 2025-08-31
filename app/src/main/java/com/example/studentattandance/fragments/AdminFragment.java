package com.example.studentattandance.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.studentattandance.R;
import com.example.studentattandance.adapters.AdminPagerAdapter;
import com.example.studentattandance.utils.SessionManager;
import com.example.studentattandance.fragments.UserManagementFragment;
import com.example.studentattandance.fragments.ClassManagementFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminFragment extends Fragment {
    private static final String TAG = "AdminFragment";
    
    private SessionManager sessionManager;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton addFab;
    private AdminPagerAdapter pagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AdminFragment onCreate started");
        
        try {
            sessionManager = SessionManager.getInstance(requireContext());
            Log.d(TAG, "SessionManager initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SessionManager", e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "AdminFragment onCreateView started");
        
        try {
            // Step 1: Check if user is admin (with error handling)
            Log.d(TAG, "Step 1: Checking admin status...");
            boolean isAdmin = false;
            try {
                isAdmin = isAdminUser();
                Log.d(TAG, "Admin check result: " + isAdmin);
            } catch (Exception e) {
                Log.e(TAG, "Error checking admin status", e);
                isAdmin = false;
            }
            
            if (!isAdmin) {
                Log.w(TAG, "User is not admin, showing access denied");
                // TEMPORARY: Show debug info instead of access denied
                return createDebugView();
            }
            Log.d(TAG, "Admin status confirmed");
            
            // Step 2: Inflate layout
            Log.d(TAG, "Step 2: Inflating layout...");
            View view = null;
            try {
                view = inflater.inflate(R.layout.fragment_admin, container, false);
                Log.d(TAG, "Layout inflated successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error inflating layout", e);
                return createErrorView("Failed to load admin interface");
            }
            
            if (view == null) {
                Log.e(TAG, "Inflated view is null");
                return createErrorView("Failed to create admin interface");
            }
            
            // Step 3: Initialize views
            Log.d(TAG, "Step 3: Initializing views...");
            try {
                initViews(view);
                Log.d(TAG, "Views initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing views", e);
                return createErrorView("Failed to initialize interface: " + e.getMessage());
            }
            
            // Step 4: Setup tabs and ViewPager
            Log.d(TAG, "Step 4: Setting up tabs and ViewPager...");
            try {
                setupTabsAndViewPager();
                Log.d(TAG, "Tabs and ViewPager setup successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error setting up tabs and ViewPager", e);
                return createErrorView("Failed to setup navigation: " + e.getMessage());
            }
            
            // Step 5: Setup floating action button
            Log.d(TAG, "Step 5: Setting up floating action button...");
            try {
                setupFloatingActionButton();
                Log.d(TAG, "Floating action button setup successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error setting up floating action button", e);
                // Don't fail completely for this
                Log.w(TAG, "Continuing without floating action button");
            }
            
            Log.d(TAG, "Admin view created successfully");
            return view;
            
        } catch (Exception e) {
            Log.e(TAG, "Critical error creating admin view", e);
            e.printStackTrace();
            
            // Return a simple error view
            return createErrorView("Critical error: " + e.getMessage());
        }
    }

    private boolean isAdminUser() {
        try {
            if (sessionManager == null || !sessionManager.isLoggedIn()) {
                Log.w(TAG, "SessionManager is null or user not logged in");
                return false;
            }
            
            String role = sessionManager.getUserRole();
            if (role == null) {
                Log.w(TAG, "User role is null");
                return false;
            }
            
            boolean isAdmin = "ADMIN".equals(role);
            Log.d(TAG, "User role: " + role + ", isAdmin: " + isAdmin);
            return isAdmin;
            
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
            Log.d(TAG, "Starting view initialization...");
            
            // Initialize TabLayout and ViewPager2
            tabLayout = view.findViewById(R.id.admin_tab_layout);
            if (tabLayout == null) {
                Log.w(TAG, "admin_tab_layout not found");
            } else {
                Log.d(TAG, "TabLayout initialized successfully");
            }
            
            viewPager = view.findViewById(R.id.admin_view_pager);
            if (viewPager == null) {
                Log.w(TAG, "admin_view_pager not found");
            } else {
                Log.d(TAG, "ViewPager2 initialized successfully");
            }
            
            // Initialize FloatingActionButton
            addFab = view.findViewById(R.id.add_fab);
            if (addFab == null) {
                Log.w(TAG, "add_fab not found");
            } else {
                Log.d(TAG, "FloatingActionButton initialized successfully");
            }
            
            Log.d(TAG, "All views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in initViews", e);
            throw e;
        }
    }

    private void setupTabsAndViewPager() {
        try {
            Log.d(TAG, "Setting up tabs and ViewPager...");
            
            if (tabLayout == null || viewPager == null) {
                Log.e(TAG, "TabLayout or ViewPager is null");
                return;
            }
            
            // Create and set the adapter
            pagerAdapter = new AdminPagerAdapter(requireActivity());
            viewPager.setAdapter(pagerAdapter);
            
            // Connect TabLayout with ViewPager2
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("User Management");
                        break;
                    case 1:
                        tab.setText("Class Management");
                        break;
                }
            }).attach();
            
            Log.d(TAG, "Tabs and ViewPager setup completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up tabs and ViewPager", e);
            throw e;
        }
    }

    private void setupFloatingActionButton() {
        try {
            if (addFab == null) {
                Log.w(TAG, "FAB is null, skipping setup");
                return;
            }
            
            addFab.setOnClickListener(v -> {
                try {
                    int currentTab = viewPager.getCurrentItem();
                    if (currentTab == 0) {
                        // User Management tab - launch AddUserActivity
                        android.content.Intent intent = new android.content.Intent(requireContext(), 
                            com.example.studentattandance.activities.AddUserActivity.class);
                        startActivity(intent);
                    } else {
                        // Class Management tab
                        showAddClassDialog();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling FAB click", e);
                    showErrorMessage("Error: " + e.getMessage());
                }
            });
            
            Log.d(TAG, "FAB setup completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up FAB", e);
            throw e;
        }
    }



    private void showAddClassDialog() {
        try {
            // Launch AddClassActivity directly instead of calling a dialog method
            Intent intent = new Intent(requireContext(), com.example.studentattandance.activities.AddClassActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching AddClassActivity", e);
            showErrorMessage("Error launching Add Class: " + e.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        try {
            android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing error message", e);
        }
    }

    private View createErrorView(String errorMessage) {
        try {
            TextView errorView = new TextView(requireContext());
            errorView.setText("Admin Panel Error\n\n" + errorMessage + "\n\nPlease try again or contact support.");
            errorView.setTextSize(16);
            errorView.setTextColor(android.graphics.Color.RED);
            errorView.setGravity(android.view.Gravity.CENTER);
            errorView.setPadding(50, 100, 50, 100);
            return errorView;
        } catch (Exception e) {
            Log.e(TAG, "Error creating error view", e);
            // Ultimate fallback
            TextView fallbackView = new TextView(requireContext());
            fallbackView.setText("Critical Error in Admin Panel");
            fallbackView.setTextSize(18);
            fallbackView.setTextColor(android.graphics.Color.RED);
            fallbackView.setGravity(android.view.Gravity.CENTER);
            fallbackView.setPadding(50, 100, 50, 100);
            return fallbackView;
        }
    }
    
    private View createDebugView() {
        try {
            StringBuilder debugInfo = new StringBuilder();
            debugInfo.append("🔍 DEBUG: Admin Access Check Failed\n\n");
            
            if (sessionManager == null) {
                debugInfo.append("❌ SessionManager: NULL\n");
            } else {
                debugInfo.append("✅ SessionManager: OK\n");
                
                boolean isLoggedIn = sessionManager.isLoggedIn();
                debugInfo.append("📱 Logged In: ").append(isLoggedIn).append("\n");
                
                if (isLoggedIn) {
                    String userId = sessionManager.getUserId();
                    String username = sessionManager.getUsername();
                    String role = sessionManager.getUserRole();
                    
                    debugInfo.append("🆔 User ID: ").append(userId != null ? userId : "NULL").append("\n");
                    debugInfo.append("👤 Username: ").append(username != null ? username : "NULL").append("\n");
                    debugInfo.append("🎭 Role: ").append(role != null ? role : "NULL").append("\n");
                    
                    // Show what we're checking against
                    debugInfo.append("\n🔍 Checking if role equals 'ADMIN': ").append("ADMIN".equals(role));
                }
            }
            
            debugInfo.append("\n\n💡 Expected: Role should be 'ADMIN'");
            debugInfo.append("\n🔧 Try logging in again with admin/123456");
            
            TextView debugView = new TextView(requireContext());
            debugView.setText(debugInfo.toString());
            debugView.setTextSize(14);
            debugView.setTextColor(android.graphics.Color.BLACK);
            debugView.setGravity(android.view.Gravity.START);
            debugView.setPadding(50, 50, 50, 50);
            debugView.setBackgroundColor(android.graphics.Color.YELLOW);
            
            return debugView;
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating debug view", e);
            TextView fallbackView = new TextView(requireContext());
            fallbackView.setText("Debug Error: " + e.getMessage());
            fallbackView.setTextSize(16);
            fallbackView.setTextColor(android.graphics.Color.RED);
            fallbackView.setGravity(android.view.Gravity.CENTER);
            fallbackView.setPadding(50, 100, 50, 100);
            return fallbackView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AdminFragment onDestroy");
    }
}




