package com.example.studentattandance.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewUserFragment extends Fragment {
    
    private static final String TAG = "ViewUserFragment";
    
    private TextView tvUsername, tvFullName, tvEmail, tvRole, tvCreatedDate, tvLastLogin;
    private Button btnEdit, btnDelete, btnBack, btnGoBack;
    private ProgressBar progressBar;
    private LinearLayout userInfoLayout, emptyStateLayout;
    
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    
    private String userId;
    private UserEntity currentUser;
    
    public static ViewUserFragment newInstance(String userId) {
        ViewUserFragment fragment = new ViewUserFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(TAG, "ViewUserFragment onCreateView started");
            
            View view = inflater.inflate(R.layout.fragment_view_user, container, false);
            
            // Initialize components
            initViews(view);
            setupClickListeners();
            
            // Initialize data
            dataRepository = new DataRepository(requireContext());
            sessionManager = SessionManager.getInstance(requireContext());
            executorService = Executors.newSingleThreadExecutor();
            
            // Check admin access
            if (!isAdminUser()) {
                showAccessDenied();
                return view;
            }
            
            // Load user data
            if (userId != null) {
                loadUserData();
            } else {
                showEmptyState("No user ID provided");
            }
            
            Log.d(TAG, "ViewUserFragment onCreateView completed successfully");
            return view;
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView", e);
            Toast.makeText(requireContext(), "Error creating view: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    
    private void initViews(View view) {
        try {
            tvUsername = view.findViewById(R.id.tv_username);
            tvFullName = view.findViewById(R.id.tv_full_name);
            tvEmail = view.findViewById(R.id.tv_email);
            tvRole = view.findViewById(R.id.tv_role);
            tvCreatedDate = view.findViewById(R.id.tv_created_date);
            tvLastLogin = view.findViewById(R.id.tv_last_login);
            btnEdit = view.findViewById(R.id.btn_edit);
            btnDelete = view.findViewById(R.id.btn_delete);
            btnBack = view.findViewById(R.id.btn_back_to_list);
            btnGoBack = view.findViewById(R.id.btn_go_back);
            progressBar = view.findViewById(R.id.progress_bar);
            userInfoLayout = view.findViewById(R.id.user_info_layout);
            emptyStateLayout = view.findViewById(R.id.empty_state_layout);
            
            Log.d(TAG, "Views initialized successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw e;
        }
    }
    
    private void setupClickListeners() {
        try {
            if (btnEdit != null) {
                btnEdit.setOnClickListener(v -> editUser());
            }
            
            if (btnDelete != null) {
                btnDelete.setOnClickListener(v -> showDeleteConfirmation());
            }
            
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> goBack());
            }
            
            if (btnGoBack != null) {
                btnGoBack.setOnClickListener(v -> goBack());
            }
            
            Log.d(TAG, "Click listeners setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
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
    
    private void showAccessDenied() {
        try {
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
            if (userInfoLayout != null) {
                userInfoLayout.setVisibility(View.GONE);
            }
            
            // Update empty state message
            TextView tvEmptyMessage = emptyStateLayout.findViewById(R.id.tv_empty_message);
            if (tvEmptyMessage != null) {
                tvEmptyMessage.setText("Access Denied\n\nOnly administrators can view user details.");
            }
            
            Log.d(TAG, "Access denied shown");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing access denied", e);
        }
    }
    
    private void loadUserData() {
        try {
            showProgress(true);
            
            executorService.execute(() -> {
                try {
                    UserEntity user = dataRepository.getUserByIdSync(userId);
                    
                    if (user == null) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            showEmptyState("User not found");
                        });
                        return;
                    }
                    
                    currentUser = user;
                    
                    runOnUiThread(() -> {
                        showProgress(false);
                        populateUserData(user);
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error loading user data", e);
                    runOnUiThread(() -> {
                        showProgress(false);
                        showEmptyState("Error loading user: " + e.getMessage());
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in loadUserData", e);
            showProgress(false);
        }
    }
    
    private void populateUserData(UserEntity user) {
        try {
            if (user == null) return;
            
            // Populate user information
            if (tvUsername != null) {
                tvUsername.setText(user.getUsername());
            }
            
            if (tvFullName != null) {
                String fullName = (user.getFirstName() != null ? user.getFirstName() : "") + 
                                " " + (user.getLastName() != null ? user.getLastName() : "");
                tvFullName.setText(fullName.trim());
            }
            
            if (tvEmail != null) {
                tvEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");
            }
            
            if (tvRole != null) {
                tvRole.setText(user.getRole() != null ? user.getRole() : "Unknown");
            }
            
            // Format and display dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
            
            if (tvCreatedDate != null && user.getCreatedAt() != null) {
                tvCreatedDate.setText("Created: " + dateFormat.format(user.getCreatedAt()));
            } else if (tvCreatedDate != null) {
                tvCreatedDate.setText("Created: Unknown");
            }
            
            if (tvLastLogin != null && user.getLastLogin() != null) {
                tvLastLogin.setText("Last Login: " + dateFormat.format(user.getLastLogin()));
            } else if (tvLastLogin != null) {
                tvLastLogin.setText("Last Login: Never");
            }
            
            // Show user info layout
            showUserInfo();
            
            Log.d(TAG, "User data populated successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error populating user data", e);
        }
    }
    
    private void showUserInfo() {
        try {
            if (userInfoLayout != null) {
                userInfoLayout.setVisibility(View.VISIBLE);
            }
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing user info", e);
        }
    }
    
    private void showEmptyState(String message) {
        try {
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
            if (userInfoLayout != null) {
                userInfoLayout.setVisibility(View.GONE);
            }
            
            // Update empty state message
            TextView tvEmptyMessage = emptyStateLayout.findViewById(R.id.tv_empty_message);
            if (tvEmptyMessage != null) {
                tvEmptyMessage.setText(message);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing empty state", e);
        }
    }
    
    private void editUser() {
        try {
            if (currentUser == null) {
                Toast.makeText(requireContext(), "No user data available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to EditUserActivity
            android.content.Intent intent = new android.content.Intent(requireContext(), 
                com.example.studentattandance.activities.EditUserActivity.class);
            intent.putExtra("userId", currentUser.getId());
            intent.putExtra("mode", "edit");
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to edit user", e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showDeleteConfirmation() {
        try {
            if (currentUser == null) {
                Toast.makeText(requireContext(), "No user data available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + currentUser.getFirstName() + " " + currentUser.getLastName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser())
                .setNegativeButton("Cancel", null)
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing delete confirmation", e);
        }
    }
    
    private void deleteUser() {
        try {
            if (currentUser == null) {
                Toast.makeText(requireContext(), "No user data available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            showProgress(true);
            
            executorService.execute(() -> {
                try {
                    dataRepository.deleteUser(currentUser);
                    
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(requireContext(), 
                            "User deleted successfully", Toast.LENGTH_SHORT).show();
                        goBack();
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting user", e);
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(requireContext(), 
                            "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteUser", e);
            showProgress(false);
        }
    }
    
    private void goBack() {
        try {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error going back", e);
        }
    }
    
    private void showProgress(boolean show) {
        try {
            if (progressBar != null) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            if (userInfoLayout != null) {
                userInfoLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
            if (btnEdit != null) {
                btnEdit.setEnabled(!show);
            }
            if (btnDelete != null) {
                btnDelete.setEnabled(!show);
            }
            if (btnBack != null) {
                btnBack.setEnabled(!show);
            }
            if (btnGoBack != null) {
                btnGoBack.setEnabled(!show);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding progress", e);
        }
    }
    
    private void runOnUiThread(Runnable runnable) {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(runnable);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error running on UI thread", e);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        Log.d(TAG, "ViewUserFragment destroyed");
    }
}
