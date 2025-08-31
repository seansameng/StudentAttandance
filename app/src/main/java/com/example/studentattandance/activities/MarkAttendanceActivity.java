package com.example.studentattandance.activities;

import android.content.Intent;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.adapters.StudentAttendanceAdapter;
import com.example.studentattandance.models.StudentAttendanceItem;
import com.example.studentattandance.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MarkAttendanceActivity extends AppCompatActivity implements StudentAttendanceAdapter.OnAttendanceChangeListener {
    
    private static final String TAG = "MarkAttendanceActivity";
    
    private SessionManager sessionManager;
    private Spinner spinnerSubject, spinnerTeacher;
    private TextView tvSelectedDate, tvStudentCount;
    private Button btnSelectDate, btnSaveAttendance;
    private RecyclerView recyclerStudents;
    
    private StudentAttendanceAdapter studentAdapter;
    private List<StudentAttendanceItem> studentList;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;
    
    // Data received from AttendanceFragment
    private String selectedClassId;
    private String selectedClassName;
    private String selectedSubject;
    private String selectedSchedule;
    private String selectedRoom;
    private int selectedStudentCount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);
        
        try {
            Log.d(TAG, "Creating MarkAttendanceActivity...");
            
            // Initialize SessionManager
            sessionManager = SessionManager.getInstance(this);
            
            // Initialize date formatter
            dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
            selectedDate = Calendar.getInstance();
            
            // Initialize views
            initViews();
            
            // Get data from intent (passed from AttendanceFragment)
            getIntentData();
            
            // Update header with class info
            updateHeaderWithClassInfo();
            
            // Setup spinners with data from intent or sample data
            setupSpinners();
            
            // Setup RecyclerView
            setupRecyclerView();
            
            // Setup click listeners
            setupClickListeners();
            
            // Load sample student data
            loadSampleStudentData();
            
            Log.d(TAG, "MarkAttendanceActivity created successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating MarkAttendanceActivity", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initViews() {
        try {
            spinnerSubject = findViewById(R.id.spinner_subject);
            spinnerTeacher = findViewById(R.id.spinner_teacher);
            tvSelectedDate = findViewById(R.id.tv_selected_date);
            btnSelectDate = findViewById(R.id.btn_select_date);
            tvStudentCount = findViewById(R.id.tv_student_count);
            recyclerStudents = findViewById(R.id.recycler_students);
            btnSaveAttendance = findViewById(R.id.btn_save_attendance);
            
            // Set current date
            updateDateDisplay();
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw e;
        }
    }
    
    private void updateHeaderWithClassInfo() {
        try {
            if (selectedClassName != null) {
                // Update the title to show the selected class
                setTitle("Mark Attendance - " + selectedClassName);
                
                // You can also update other UI elements here if needed
                Log.d(TAG, "Updated header for class: " + selectedClassName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating header", e);
        }
    }
    
    private void getIntentData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                selectedClassId = intent.getStringExtra("class_id");
                selectedClassName = intent.getStringExtra("class_name");
                selectedSubject = intent.getStringExtra("subject");
                selectedSchedule = intent.getStringExtra("schedule");
                selectedRoom = intent.getStringExtra("room");
                selectedStudentCount = intent.getIntExtra("student_count", 0);
                
                Log.d(TAG, "Received data: Class=" + selectedClassName + ", Subject=" + selectedSubject);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data", e);
        }
    }
    
    private void setupSpinners() {
        try {
            // Setup Subject/Class spinner - use received data if available
            if (selectedClassName != null && selectedSubject != null) {
                // Pre-select the received class
                String[] subjects = {selectedClassName + " - " + selectedSubject};
                ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
                subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubject.setAdapter(subjectAdapter);
                spinnerSubject.setEnabled(false); // Disable selection since it's pre-selected
            } else {
                // Fallback to sample data
                String[] subjects = {"Mathematics 101", "Physics 201", "Chemistry 101", "English Literature", "Computer Science"};
                ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
                subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubject.setAdapter(subjectAdapter);
            }
            
            // Setup Teacher spinner
            String[] teachers = {"Dr. Smith", "Prof. Johnson", "Dr. Williams", "Prof. Brown", "Dr. Davis"};
            ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teachers);
            teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTeacher.setAdapter(teacherAdapter);
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up spinners", e);
        }
    }
    
    private void setupRecyclerView() {
        try {
            recyclerStudents.setLayoutManager(new LinearLayoutManager(this));
            studentList = new ArrayList<>();
            studentAdapter = new StudentAttendanceAdapter(studentList, this);
            recyclerStudents.setAdapter(studentAdapter);
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
        }
    }
    
    private void setupClickListeners() {
        try {
            // Date selection button
            btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
            
            // Save attendance button
            btnSaveAttendance.setOnClickListener(v -> saveAttendance());
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners", e);
        }
    }
    
    private void showDatePickerDialog() {
        try {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            
            // Set max date to today
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing date picker dialog", e);
            Toast.makeText(this, "Error selecting date", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateDateDisplay() {
        try {
            String dateText = dateFormat.format(selectedDate.getTime());
            tvSelectedDate.setText(dateText);
        } catch (Exception e) {
            Log.e(TAG, "Error updating date display", e);
        }
    }
    
    private void loadSampleStudentData() {
        try {
            // Create sample student data - use received count if available
            int studentCount = (selectedStudentCount > 0) ? selectedStudentCount : 8;
            studentList.clear();
            
            // Generate sample students based on count
            for (int i = 1; i <= studentCount; i++) {
                String studentId = String.format("S%03d", i);
                String studentName = getSampleStudentName(i);
                String studentIdNumber = String.format("2024%03d", i);
                studentList.add(new StudentAttendanceItem(studentId, studentName, studentIdNumber));
            }
            
            // Update adapter and student count
            studentAdapter.notifyDataSetChanged();
            updateStudentCount();
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading sample student data", e);
        }
    }
    
    private String getSampleStudentName(int index) {
        String[] names = {
            "John Doe", "Jane Smith", "Mike Johnson", "Sarah Wilson", 
            "David Brown", "Emily Davis", "Robert Miller", "Lisa Garcia",
            "James Wilson", "Maria Rodriguez", "Michael Brown", "Jennifer Lee",
            "Christopher Davis", "Jessica Taylor", "Daniel Anderson", "Amanda White",
            "Matthew Thompson", "Nicole Martinez", "Andrew Garcia", "Stephanie Robinson",
            "Joshua Lewis", "Rebecca Clark", "Kevin Hall", "Laura Young",
            "Brian Allen", "Michelle King", "Steven Wright", "Ashley Green",
            "Timothy Baker", "Kimberly Adams", "Jason Nelson", "Heather Carter"
        };
        
        if (index <= names.length) {
            return names[index - 1];
        } else {
            return "Student " + index;
        }
    }
    
    private void updateStudentCount() {
        try {
            int totalStudents = studentList.size();
            int markedStudents = 0;
            
            for (StudentAttendanceItem student : studentList) {
                if (student.isAttendanceMarked()) {
                    markedStudents++;
                }
            }
            
            String countText = markedStudents + "/" + totalStudents + " students marked";
            tvStudentCount.setText(countText);
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating student count", e);
        }
    }
    
    @Override
    public void onAttendanceChanged(StudentAttendanceItem student, boolean isPresent) {
        try {
            Log.d(TAG, "Attendance changed for " + student.getStudentName() + ": " + (isPresent ? "Present" : "Absent"));
            updateStudentCount();
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling attendance change", e);
        }
    }
    
    private void saveAttendance() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }
            
            // Check if all students have attendance marked
            List<StudentAttendanceItem> unmarkedStudents = new ArrayList<>();
            for (StudentAttendanceItem student : studentList) {
                if (!student.isAttendanceMarked()) {
                    unmarkedStudents.add(student);
                }
            }
            
            if (!unmarkedStudents.isEmpty()) {
                showUnmarkedStudentsDialog(unmarkedStudents);
                return;
            }
            
            // Show confirmation dialog
            showSaveConfirmationDialog();
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving attendance", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateInputs() {
        try {
            // Check if subject is selected
            if (spinnerSubject.getSelectedItem() == null) {
                Toast.makeText(this, "Please select a subject/class", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            // Check if teacher is selected
            if (spinnerTeacher.getSelectedItem() == null) {
                Toast.makeText(this, "Please select a teacher", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            // Check if date is selected
            if (selectedDate == null) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error validating inputs", e);
            return false;
        }
    }
    
    private void showUnmarkedStudentsDialog(List<StudentAttendanceItem> unmarkedStudents) {
        try {
            StringBuilder message = new StringBuilder();
            message.append("The following students don't have attendance marked:\n\n");
            
            for (StudentAttendanceItem student : unmarkedStudents) {
                message.append("• ").append(student.getStudentName()).append("\n");
            }
            
            message.append("\nPlease mark attendance for all students before saving.");
            
            new AlertDialog.Builder(this)
                .setTitle("Incomplete Attendance")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing unmarked students dialog", e);
        }
    }
    
    private void showSaveConfirmationDialog() {
        try {
            String subject = spinnerSubject.getSelectedItem().toString();
            String teacher = spinnerTeacher.getSelectedItem().toString();
            String date = dateFormat.format(selectedDate.getTime());
            
            StringBuilder message = new StringBuilder();
            message.append("Save attendance for:\n\n");
            message.append("Subject: ").append(subject).append("\n");
            message.append("Teacher: ").append(teacher).append("\n");
            message.append("Date: ").append(date).append("\n\n");
            
            // Count present and absent students
            int presentCount = 0, absentCount = 0;
            for (StudentAttendanceItem student : studentList) {
                if (student.isPresent()) presentCount++;
                else if (student.isAbsent()) absentCount++;
            }
            
            message.append("Present: ").append(presentCount).append("\n");
            message.append("Absent: ").append(absentCount).append("\n\n");
            message.append("Are you sure you want to save this attendance record?");
            
            new AlertDialog.Builder(this)
                .setTitle("Confirm Save")
                .setMessage(message.toString())
                .setPositiveButton("Save", (dialog, which) -> {
                    performSaveAttendance();
                })
                .setNegativeButton("Cancel", null)
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing save confirmation dialog", e);
        }
    }
    
    private void performSaveAttendance() {
        try {
            // TODO: Implement actual database save
            // For now, just show success message
            
            Toast.makeText(this, "Attendance saved successfully!", Toast.LENGTH_LONG).show();
            
            // Show summary
            showAttendanceSummary();
            
        } catch (Exception e) {
            Log.e(TAG, "Error performing save attendance", e);
            Toast.makeText(this, "Error saving attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showAttendanceSummary() {
        try {
            String subject = spinnerSubject.getSelectedItem().toString();
            String date = dateFormat.format(selectedDate.getTime());
            
            int presentCount = 0, absentCount = 0;
            for (StudentAttendanceItem student : studentList) {
                if (student.isPresent()) presentCount++;
                else if (student.isAbsent()) absentCount++;
            }
            
            StringBuilder message = new StringBuilder();
            message.append("Attendance Summary\n\n");
            message.append("Subject: ").append(subject).append("\n");
            message.append("Date: ").append(date).append("\n");
            message.append("Total Students: ").append(studentList.size()).append("\n");
            message.append("Present: ").append(presentCount).append("\n");
            message.append("Absent: ").append(absentCount).append("\n");
            message.append("Attendance Rate: ").append(String.format("%.1f%%", (presentCount * 100.0 / studentList.size())));
            
            new AlertDialog.Builder(this)
                .setTitle("Attendance Saved")
                .setMessage(message.toString())
                .setPositiveButton("OK", (dialog, which) -> {
                    finish(); // Close the activity
                })
                .show();
                
        } catch (Exception e) {
            Log.e(TAG, "Error showing attendance summary", e);
            finish(); // Close the activity anyway
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MarkAttendanceActivity destroyed");
    }
}
