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
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewClassFragment extends Fragment {
    
    private static final String TAG = "ViewClassFragment";
    
    private TextView tvClassName, tvSubject, tvDescription, tvCapacity, tvTeacher, tvCreatedDate, tvStatus;
    private Button btnEdit, btnDelete, btnBackToList, btnGoBack;
    private ProgressBar progressBar;
    private LinearLayout classInfoLayout, emptyStateLayout;
    
    private DataRepository dataRepository;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    
    private String classId;
    private ClassEntity currentClass;
    
    public static ViewClassFragment newInstance(String classId) {
        ViewClassFragment fragment = new ViewClassFragment();
        Bundle args = new Bundle();
        args.putString("classId", classId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classId = getArguments().getString("classId");
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(TAG, "ViewClassFragment onCreateView started");
            
            View view = inflater.inflate(R.layout.fragment_view_class, container, false);
            
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
            
            // Load class data
            if (classId != null) {
                loadClassData();
            } else {
                showEmptyState("No class ID provided");
            }
            
            Log.d(TAG, "ViewClassFragment onCreateView completed successfully");
            return view;
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView", e);
            Toast.makeText(requireContext(), "Error creating view: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    
    private void initViews(View view) {
        try {
            tvClassName = view.findViewById(R.id.tv_class_name);
            tvSubject = view.findViewById(R.id.tv_subject);
            tvDescription = view.findViewById(R.id.tv_description);
            tvCapacity = view.findViewById(R.id.tv_capacity);
            tvTeacher = view.findViewById(R.id.tv_teacher);
            tvCreatedDate = view.findViewById(R.id.tv_created_date);
            tvStatus = view.findViewById(R.id.tv_status);
                    btnEdit = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnBackToList = view.findViewById(R.id.btn_back_to_list);
        btnGoBack = view.findViewById(R.id.btn_go_back);
            progressBar = view.findViewById(R.id.progress_bar);
            classInfoLayout = view.findViewById(R.id.class_info_layout);
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
                btnEdit.setOnClickListener(v -> editClass());
            }
            
            if (btnDelete != null) {
                btnDelete.setOnClickListener(v -> showDeleteConfirmation());
            }
            
                    if (btnBackToList != null) {
            btnBackToList.setOnClickListener(v -> goBack());
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
            if (classInfoLayout != null) {
                classInfoLayout.setVisibility(View.GONE);
            }
            
            // Update empty state message
            TextView tvEmptyMessage = emptyStateLayout.findViewById(R.id.tv_empty_message);
            if (tvEmptyMessage != null) {
                tvEmptyMessage.setText("Access Denied\n\nOnly administrators can view class details.");
            }
            
            Log.d(TAG, "Access denied shown");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing access denied", e);
        }
    }
    
    private void loadClassData() {
        try {
            showProgress(true);
            
            executorService.execute(() -> {
                try {
                    ClassEntity classEntity = dataRepository.getClassByIdSync(classId);
                    
                    if (classEntity == null) {
                        runOnUiThread(() -> {
                            showProgress(false);
                            showEmptyState("Class not found");
                        });
                        return;
                    }
                    
                    currentClass = classEntity;
                    
                    runOnUiThread(() -> {
                        showProgress(false);
                        populateClassData(classEntity);
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error loading class data", e);
                    runOnUiThread(() -> {
                        showProgress(false);
                        showEmptyState("Error loading class: " + e.getMessage());
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in loadClassData", e);
            showProgress(false);
        }
    }
    
    private void populateClassData(ClassEntity classEntity) {
        try {
            if (classEntity == null) return;
            
            // Populate class information
            if (tvClassName != null) {
                tvClassName.setText(classEntity.getClassName());
            }
            
            if (tvSubject != null) {
                tvSubject.setText(classEntity.getSubject());
            }
            
            if (tvDescription != null) {
                tvDescription.setText(classEntity.getSchedule() != null ? classEntity.getSchedule() : "No schedule");
            }
            
            if (tvCapacity != null) {
                tvCapacity.setText(classEntity.getRoom() != null ? classEntity.getRoom() : "No room");
            }
            
            if (tvTeacher != null) {
                tvTeacher.setText(classEntity.getTeacherId() != null ? classEntity.getTeacherId() : "No teacher assigned");
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
            
            // Show class info layout
            showClassInfo();
            
            Log.d(TAG, "Class data populated successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error populating class data", e);
        }
    }
    
    private void showClassInfo() {
        try {
            if (classInfoLayout != null) {
                classInfoLayout.setVisibility(View.VISIBLE);
            }
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing class info", e);
        }
    }
    
    private void showEmptyState(String message) {
        try {
            if (emptyStateLayout != null) {
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
            if (classInfoLayout != null) {
                classInfoLayout.setVisibility(View.GONE);
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
    
    private void editClass() {
        try {
            if (currentClass == null) {
                Toast.makeText(requireContext(), "No class data available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to EditClassActivity
            android.content.Intent intent = new android.content.Intent(requireContext(), 
                com.example.studentattandance.activities.EditClassActivity.class);
            intent.putExtra("classId", currentClass.getId());
            intent.putExtra("mode", "edit");
            startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to edit class", e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showDeleteConfirmation() {
        try {
            if (currentClass == null) {
                Toast.makeText(requireContext(), "No class data available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete " + currentClass.getClassName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteClass())
                .setNegativeButton("Cancel", null)
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing delete confirmation", e);
        }
    }
    
    private void deleteClass() {
        try {
            if (currentClass == null) {
                Toast.makeText(requireContext(), "No class data available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            showProgress(true);
            
            executorService.execute(() -> {
                try {
                    dataRepository.deleteClass(currentClass);
                    
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(requireContext(), 
                            "Class deleted successfully", Toast.LENGTH_SHORT).show();
                        goBack();
                    });
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting class", e);
                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(requireContext(), 
                            "Error deleting class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteClass", e);
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
            if (classInfoLayout != null) {
                classInfoLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
            if (btnEdit != null) {
                btnEdit.setEnabled(!show);
            }
            if (btnDelete != null) {
                btnDelete.setEnabled(!show);
            }
                    if (btnBackToList != null) {
            btnBackToList.setEnabled(!show);
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
        Log.d(TAG, "ViewClassFragment destroyed");
    }
}
