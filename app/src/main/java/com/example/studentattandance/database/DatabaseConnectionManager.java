package com.example.studentattandance.database;

import android.content.Context;
import android.util.Log;

import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.ClassEnrollmentEntity;
import com.example.studentattandance.database.entities.AttendanceEntity;
import com.example.studentattandance.repository.DataRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseConnectionManager {
    private static final String TAG = "DatabaseConnectionManager";
    
    private static DatabaseConnectionManager INSTANCE;
    private final Context context;
    private final AppDatabase database;
    private final DataRepository repository;
    private final ExecutorService executorService;
    
    private DatabaseConnectionManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.context);
        this.repository = new DataRepository(this.context);
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized DatabaseConnectionManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseConnectionManager(context);
        }
        return INSTANCE;
    }
    
    /**
     * Test database connection and return status
     */
    public void testDatabaseConnection(DatabaseConnectionCallback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Testing database connection...");
                
                // Test 1: Check if database is open
                boolean isOpen = database.isOpen();
                Log.d(TAG, "Database is open: " + isOpen);
                
                if (!isOpen) {
                    callback.onConnectionFailed("Database is not open");
                    return;
                }
                
                // Test 2: Try to access DAOs
                try {
                    database.userDao();
                    database.classDao();
                    database.attendanceDao();
                    database.classEnrollmentDao();
                    Log.d(TAG, "All DAOs accessible");
                } catch (Exception e) {
                    Log.e(TAG, "Error accessing DAOs", e);
                    callback.onConnectionFailed("Cannot access database DAOs: " + e.getMessage());
                    return;
                }
                
                // Test 3: Try a simple database operation
                try {
                    List<UserEntity> users = repository.getAllUsersSync();
                    Log.d(TAG, "Database query successful, found " + (users != null ? users.size() : 0) + " users");
                } catch (Exception e) {
                    Log.e(TAG, "Error executing database query", e);
                    callback.onConnectionFailed("Database query failed: " + e.getMessage());
                    return;
                }
                
                // Test 4: Check database file location
                String dbPath = database.getOpenHelper().getDatabaseName();
                Log.d(TAG, "Database path: " + dbPath);
                
                callback.onConnectionSuccess("Database connection successful. Path: " + dbPath);
                
            } catch (Exception e) {
                Log.e(TAG, "Database connection test failed", e);
                callback.onConnectionFailed("Connection test failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * Initialize database with sample data if empty
     */
    public void initializeDatabaseIfNeeded(DatabaseInitCallback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Checking if database needs initialization...");
                
                // Check if database has any data
                List<UserEntity> existingUsers = repository.getAllUsersSync();
                
                if (existingUsers != null && !existingUsers.isEmpty()) {
                    Log.d(TAG, "Database already has " + existingUsers.size() + " users, skipping initialization");
                    callback.onInitSuccess("Database already initialized with " + existingUsers.size() + " users");
                    return;
                }
                
                Log.d(TAG, "Database is empty, initializing with sample data...");
                
                // Create sample admin user
                UserEntity adminUser = new UserEntity();
                adminUser.setId(DatabaseHelper.generateId());
                adminUser.setUsername("admin");
                adminUser.setPassword("123456");
                adminUser.setEmail("admin@school.com");
                adminUser.setFirstName("Admin");
                adminUser.setLastName("User");
                adminUser.setRole("ADMIN");
                adminUser.setCreatedAt(new java.util.Date());
                adminUser.setLastLogin(new java.util.Date());
                
                repository.insertUser(adminUser);
                Log.d(TAG, "Created admin user: admin/123456");
                
                // Create sample teacher user
                UserEntity teacherUser = new UserEntity();
                teacherUser.setId(DatabaseHelper.generateId());
                teacherUser.setUsername("teacher");
                teacherUser.setPassword("123456");
                teacherUser.setEmail("teacher@school.com");
                teacherUser.setFirstName("John");
                teacherUser.setLastName("Teacher");
                teacherUser.setRole("TEACHER");
                teacherUser.setCreatedAt(new java.util.Date());
                teacherUser.setLastLogin(new java.util.Date());
                
                repository.insertUser(teacherUser);
                Log.d(TAG, "Created teacher user: teacher/123456");
                
                // Create sample student user
                UserEntity studentUser = new UserEntity();
                studentUser.setId(DatabaseHelper.generateId());
                studentUser.setUsername("student");
                studentUser.setPassword("123456");
                studentUser.setEmail("student@school.com");
                studentUser.setFirstName("Jane");
                studentUser.setLastName("Student");
                studentUser.setRole("STUDENT");
                studentUser.setCreatedAt(new java.util.Date());
                studentUser.setLastLogin(new java.util.Date());
                
                repository.insertUser(studentUser);
                Log.d(TAG, "Created student user: student/123456");
                
                // Create sample class
                ClassEntity sampleClass = new ClassEntity();
                sampleClass.setId(DatabaseHelper.generateId());
                sampleClass.setClassName("Mathematics 101");
                sampleClass.setSubject("Mathematics");
                sampleClass.setTeacherId(teacherUser.getId());
                sampleClass.setSchedule("Monday, Wednesday, Friday 9:00 AM");
                sampleClass.setRoom("Room 101");
                sampleClass.setCreatedAt(new java.util.Date());
                
                repository.insertClass(sampleClass);
                Log.d(TAG, "Created sample class: Mathematics 101");
                
                // Create sample enrollment
                ClassEnrollmentEntity enrollment = new ClassEnrollmentEntity();
                enrollment.setId(DatabaseHelper.generateId());
                enrollment.setStudentId(studentUser.getId());
                enrollment.setClassId(sampleClass.getId());
                enrollment.setEnrolledAt(new java.util.Date());
                enrollment.setActive(true);
                
                repository.insertEnrollment(enrollment);
                Log.d(TAG, "Created sample enrollment");
                
                // Create sample attendance
                AttendanceEntity attendance = new AttendanceEntity();
                attendance.setId(DatabaseHelper.generateId());
                attendance.setStudentId(studentUser.getId());
                attendance.setClassId(sampleClass.getId());
                attendance.setDate("2024-01-15");
                attendance.setStatus("PRESENT");
                attendance.setCreatedAt(new java.util.Date());
                
                repository.insertAttendance(attendance);
                Log.d(TAG, "Created sample attendance record");
                
                Log.d(TAG, "Database initialization completed successfully");
                callback.onInitSuccess("Database initialized with sample data");
                
            } catch (Exception e) {
                Log.e(TAG, "Database initialization failed", e);
                callback.onInitFailed("Initialization failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * Get database statistics
     */
    public void getDatabaseStats(DatabaseStatsCallback callback) {
        executorService.execute(() -> {
            try {
                List<UserEntity> users = repository.getAllUsersSync();
                List<ClassEntity> classes = repository.getAllClassesSync();
                List<AttendanceEntity> attendances = repository.getAllAttendancesSync();
                List<ClassEnrollmentEntity> enrollments = repository.getAllEnrollmentsSync();
                
                int userCount = users != null ? users.size() : 0;
                int classCount = classes != null ? classes.size() : 0;
                int attendanceCount = attendances != null ? attendances.size() : 0;
                int enrollmentCount = enrollments != null ? enrollments.size() : 0;
                
                callback.onStatsReceived(userCount, classCount, attendanceCount, enrollmentCount);
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting database stats", e);
                callback.onStatsError("Failed to get stats: " + e.getMessage());
            }
        });
    }
    
    /**
     * Clear all database data
     */
    public void clearAllData(DatabaseClearCallback callback) {
        executorService.execute(() -> {
            try {
                database.clearAllTables();
                Log.d(TAG, "All database tables cleared successfully");
                callback.onClearSuccess("All data cleared successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to clear database", e);
                callback.onClearFailed("Failed to clear data: " + e.getMessage());
            }
        });
    }
    
    /**
     * Close database connection
     */
    public void closeDatabase() {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
            AppDatabase.closeDatabase();
            Log.d(TAG, "Database connection closed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error closing database", e);
        }
    }
    
    // Callback interfaces
    public interface DatabaseConnectionCallback {
        void onConnectionSuccess(String message);
        void onConnectionFailed(String error);
    }
    
    public interface DatabaseInitCallback {
        void onInitSuccess(String message);
        void onInitFailed(String error);
    }
    
    public interface DatabaseStatsCallback {
        void onStatsReceived(int userCount, int classCount, int attendanceCount, int enrollmentCount);
        void onStatsError(String error);
    }
    
    public interface DatabaseClearCallback {
        void onClearSuccess(String message);
        void onClearFailed(String error);
    }
}
