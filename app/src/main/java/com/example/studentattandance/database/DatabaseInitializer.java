package com.example.studentattandance.database;

import android.content.Context;
import android.util.Log;

import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.ClassEnrollmentEntity;
import com.example.studentattandance.database.entities.AttendanceEntity;
import com.example.studentattandance.repository.DataRepository;
import com.example.studentattandance.database.DatabaseHelper;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseInitializer {
    private static final String TAG = "DatabaseInitializer";
    private final DataRepository repository;
    private final ExecutorService executorService;
    
    public DatabaseInitializer(Context context) {
        repository = new DataRepository(context);
        executorService = Executors.newSingleThreadExecutor();
    }
    
    // Initialize database with sample data for testing
    public void initializeDatabase(DatabaseInitCallback callback) {
        executorService.execute(() -> {
            try {
                Log.i(TAG, "Starting database initialization...");
                
                // Check if database is accessible
                if (!isDatabaseAccessible()) {
                    callback.onError(new Exception("Database is not accessible"));
                    return;
                }
                
                // Insert sample data
                insertSampleData();
                
                Log.i(TAG, "Database initialization completed successfully");
                callback.onSuccess();
                
            } catch (Exception e) {
                Log.e(TAG, "Database initialization failed", e);
                callback.onError(e);
            }
        });
    }
    
    private boolean isDatabaseAccessible() {
        try {
            // Try to access the database
            repository.getAllUsersSync();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Database accessibility check failed", e);
            return false;
        }
    }
    
    private void insertSampleData() {
        try {
            // Insert sample users
            UserEntity adminUser = new UserEntity();
            adminUser.setId(DatabaseHelper.generateId());
            adminUser.setUsername("admin");
            adminUser.setPassword("123456");
            adminUser.setEmail("admin@school.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setRole("ADMIN");
            adminUser.setCreatedAt(new Date());
            adminUser.setLastLogin(new Date());
            repository.insertUser(adminUser);
            
            UserEntity teacherUser = new UserEntity();
            teacherUser.setId(DatabaseHelper.generateId());
            teacherUser.setUsername("teacher1");
            teacherUser.setPassword("123456");
            teacherUser.setEmail("teacher1@school.com");
            teacherUser.setFirstName("John");
            teacherUser.setLastName("Doe");
            teacherUser.setRole("TEACHER");
            teacherUser.setCreatedAt(new Date());
            teacherUser.setLastLogin(new Date());
            repository.insertUser(teacherUser);
            
            UserEntity studentUser = new UserEntity();
            studentUser.setId(DatabaseHelper.generateId());
            studentUser.setUsername("student1");
            studentUser.setPassword("123456");
            studentUser.setEmail("student1@school.com");
            studentUser.setFirstName("Jane");
            studentUser.setLastName("Smith");
            studentUser.setRole("STUDENT");
            studentUser.setCreatedAt(new Date());
            studentUser.setLastLogin(new Date());
            repository.insertUser(studentUser);
            
            // Insert sample class
            ClassEntity sampleClass = new ClassEntity();
            sampleClass.setId(DatabaseHelper.generateId());
            sampleClass.setClassName("Mathematics 101");
            sampleClass.setSubject("Mathematics");
            sampleClass.setTeacherId(teacherUser.getId());
            sampleClass.setSchedule("Monday, Wednesday, Friday 9:00 AM");
            sampleClass.setRoom("Room 101");
            sampleClass.setCreatedAt(new Date());
            repository.insertClass(sampleClass);
            
            // Insert sample enrollment
            ClassEnrollmentEntity enrollment = new ClassEnrollmentEntity();
            enrollment.setId(DatabaseHelper.generateId());
            enrollment.setStudentId(studentUser.getId());
            enrollment.setClassId(sampleClass.getId());
            enrollment.setEnrolledAt(new Date());
            enrollment.setActive(true);
            repository.insertEnrollment(enrollment);
            
            // Insert sample attendance
            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setId(DatabaseHelper.generateId());
            attendance.setStudentId(studentUser.getId());
            attendance.setClassId(sampleClass.getId());
            attendance.setDate("2024-01-15");
            attendance.setStatus("PRESENT");
            attendance.setCreatedAt(new Date());
            repository.insertAttendance(attendance);
            
            Log.i(TAG, "Sample data inserted successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to insert sample data", e);
            throw e;
        }
    }
    
    // Clean up resources
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // Callback interface
    public interface DatabaseInitCallback {
        void onSuccess();
        void onError(Exception e);
    }
}
