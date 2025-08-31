package com.example.studentattandance.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.studentattandance.database.AppDatabase;
import com.example.studentattandance.database.dao.AttendanceDao;
import com.example.studentattandance.database.dao.ClassDao;
import com.example.studentattandance.database.dao.ClassEnrollmentDao;
import com.example.studentattandance.database.dao.UserDao;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.ClassEnrollmentEntity;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.entities.AttendanceEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataRepository {
    
    private UserDao userDao;
    private ClassDao classDao;
    private AttendanceDao attendanceDao;
    private ClassEnrollmentDao classEnrollmentDao;
    private ExecutorService executorService;
    
    public DataRepository(Context context) {
        try {
            Log.d("DataRepository", "Initializing DataRepository...");
            AppDatabase database = AppDatabase.getInstance(context);
            Log.d("DataRepository", "AppDatabase instance obtained");
            
            userDao = database.userDao();
            classDao = database.classDao();
            attendanceDao = database.attendanceDao();
            classEnrollmentDao = database.classEnrollmentDao();
            Log.d("DataRepository", "All DAOs initialized successfully");
            
            executorService = Executors.newFixedThreadPool(4);
            Log.d("DataRepository", "DataRepository initialization completed successfully");
            
        } catch (Exception e) {
            Log.e("DataRepository", "Error initializing DataRepository", e);
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DataRepository", e);
        }
    }
    
    // User operations
    public void insertUser(UserEntity user) {
        // Ensure ID is set before inserting
        if (user.getId() == null || user.getId().trim().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }
        executorService.execute(() -> userDao.insertUser(user));
    }
    
    public void updateUser(UserEntity user) {
        executorService.execute(() -> userDao.updateUser(user));
    }
    
    public void deleteUser(UserEntity user) {
        executorService.execute(() -> userDao.deleteUser(user));
    }
    
    public LiveData<UserEntity> getUserById(String userId) {
        return userDao.getUserById(userId);
    }
    
    // Synchronous version for activities
    public UserEntity getUserByIdSync(String userId) {
        try {
            return userDao.getUserByIdSync(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public LiveData<UserEntity> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }
    
    // Synchronous version for activities
    public UserEntity getUserByUsernameSync(String username) {
        try {
            return userDao.getUserByUsernameSync(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public LiveData<List<UserEntity>> getAllUsers() {
        return userDao.getAllUsers();
    }
    
    // Synchronous version for admin dashboard
    public List<UserEntity> getAllUsersSync() {
        try {
            return userDao.getAllUsersSync();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public LiveData<List<UserEntity>> getUsersByRole(String role) {
        return userDao.getUsersByRole(role);
    }
    
    // Synchronous version for activities
    public List<UserEntity> getUsersByRoleSync(String role) {
        try {
            return userDao.getUsersByRoleSync(role);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public LiveData<List<UserEntity>> searchUsers(String searchQuery) {
        return userDao.searchUsers(searchQuery);
    }
    
    public LiveData<UserEntity> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
    
    public LiveData<Integer> getUserCountByRole(String role) {
        return userDao.getUserCountByRole(role);
    }
    
    public LiveData<List<UserEntity>> getAllStudents() {
        return userDao.getAllStudents();
    }
    
    // Get students by class using actual enrollment data
    public List<UserEntity> getStudentsByClassSync(String classId) {
        try {
            // Get enrollment IDs for the class
            List<ClassEnrollmentEntity> enrollments = classEnrollmentDao.getActiveEnrollmentsByClassSync(classId);
            if (enrollments == null || enrollments.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Get student IDs from enrollments
            List<String> studentIds = new ArrayList<>();
            for (ClassEnrollmentEntity enrollment : enrollments) {
                studentIds.add(enrollment.getStudentId());
            }
            
            // Get actual student entities
            return userDao.getUsersByIdsSync(studentIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public LiveData<List<UserEntity>> getAllTeachers() {
        return userDao.getAllTeachers();
    }
    
    public LiveData<List<UserEntity>> getAllAdmins() {
        return userDao.getAllAdmins();
    }
    
    public void deleteAllUsers() {
        executorService.execute(() -> userDao.deleteAllUsers());
    }
    
    // Class operations
    public void insertClass(ClassEntity classEntity) {
        // Ensure ID is set before inserting
        if (classEntity.getId() == null || classEntity.getId().trim().isEmpty()) {
            classEntity.setId(UUID.randomUUID().toString());
        }
        executorService.execute(() -> classDao.insertClass(classEntity));
    }
    
    public void updateClass(ClassEntity classEntity) {
        executorService.execute(() -> classDao.updateClass(classEntity));
    }
    
    public void deleteClass(ClassEntity classEntity) {
        executorService.execute(() -> classDao.deleteClass(classEntity));
    }
    
    public LiveData<ClassEntity> getClassById(String classId) {
        return classDao.getClassById(classId);
    }
    
    // Synchronous version for activities
    public ClassEntity getClassByIdSync(String classId) {
        try {
            return classDao.getClassByIdSync(classId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public LiveData<List<ClassEntity>> getClassesByTeacher(String teacherId) {
        return classDao.getClassesByTeacher(teacherId);
    }
    
    public LiveData<List<ClassEntity>> getAllClasses() {
        return classDao.getAllClasses();
    }
    
    // Synchronous version for admin dashboard
    public List<ClassEntity> getAllClassesSync() {
        try {
            return classDao.getAllClassesSync();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    

    
    public LiveData<List<ClassEntity>> getClassesBySubject(String subject) {
        return classDao.getClassesBySubject(subject);
    }
    
    public LiveData<List<ClassEntity>> getClassesByRoom(String room) {
        return classDao.getClassesByRoom(room);
    }
    
    public LiveData<List<ClassEntity>> searchClasses(String searchQuery) {
        return classDao.searchClasses(searchQuery);
    }
    
    public LiveData<Integer> getClassCountByTeacher(String teacherId) {
        return classDao.getClassCountByTeacher(teacherId);
    }
    
    public void deleteAllClasses() {
        executorService.execute(() -> classDao.deleteAllClasses());
    }
    
    // Class Enrollment operations
    public void enrollStudentInClass(String studentId, String classId) {
        executorService.execute(() -> {
            try {
                ClassEnrollmentEntity enrollment = new ClassEnrollmentEntity();
                enrollment.setId(UUID.randomUUID().toString());
                enrollment.setStudentId(studentId);
                enrollment.setClassId(classId);
                enrollment.setEnrolledAt(new Date());
                enrollment.setActive(true);
                classEnrollmentDao.insertEnrollment(enrollment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public void unenrollStudentFromClass(String studentId, String classId) {
        executorService.execute(() -> {
            try {
                classEnrollmentDao.updateEnrollmentStatus(studentId, classId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public List<ClassEnrollmentEntity> getEnrollmentsByClassSync(String classId) {
        try {
            return classEnrollmentDao.getActiveEnrollmentsByClassSync(classId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // Attendance operations
    public void insertAttendance(AttendanceEntity attendance) {
        // Ensure ID is set before inserting
        if (attendance.getId() == null || attendance.getId().trim().isEmpty()) {
            attendance.setId(UUID.randomUUID().toString());
        }
        executorService.execute(() -> attendanceDao.insertAttendance(attendance));
    }
    
    public void updateAttendance(AttendanceEntity attendance) {
        executorService.execute(() -> attendanceDao.updateAttendance(attendance));
    }
    
    public void deleteAttendance(AttendanceEntity attendance) {
        executorService.execute(() -> attendanceDao.deleteAttendance(attendance));
    }
    
    public LiveData<List<AttendanceEntity>> getAttendancesByStudent(String studentId) {
        return attendanceDao.getAttendancesByStudent(studentId);
    }
    
    public LiveData<List<AttendanceEntity>> getAttendancesByClass(String classId) {
        return attendanceDao.getAttendancesByClass(classId);
    }
    
    public LiveData<List<AttendanceEntity>> getAllAttendances() {
        return attendanceDao.getAllAttendances();
    }
    
    public LiveData<Integer> getPresentCountByStudentAndClass(String studentId, String classId) {
        return attendanceDao.getPresentCountByStudentAndClass(studentId, classId);
    }
    
    public LiveData<Integer> getAbsentCountByStudentAndClass(String studentId, String classId) {
        return attendanceDao.getAbsentCountByStudentAndClass(studentId, classId);
    }
    
    public LiveData<Integer> getPresentCountByClassAndDate(String classId, String date) {
        return attendanceDao.getPresentCountByClassAndDate(classId, date);
    }
    
    public LiveData<Integer> getAbsentCountByClassAndDate(String classId, String date) {
        return attendanceDao.getAbsentCountByClassAndDate(classId, date);
    }
    
    public LiveData<Integer> getLateCountByClassAndDate(String classId, String date) {
        return attendanceDao.getLateCountByClassAndDate(classId, date);
    }
    
    public LiveData<Integer> getTotalCountByClassAndDate(String classId, String date) {
        return attendanceDao.getTotalCountByClassAndDate(classId, date);
    }
    
    public LiveData<List<AttendanceEntity>> getAttendancesByClassAndDate(String classId, String date) {
        return attendanceDao.getAttendancesByClassAndDate(classId, date);
    }
    
    public LiveData<List<AttendanceEntity>> getAttendanceHistoryByStudentAndClass(String studentId, String classId) {
        return attendanceDao.getAttendanceHistoryByStudentAndClass(studentId, classId);
    }
    
    public void deleteAllAttendances() {
        executorService.execute(() -> attendanceDao.deleteAllAttendances());
    }
    
    // ClassEnrollment operations
    public void insertEnrollment(ClassEnrollmentEntity enrollment) {
        // Ensure ID is set before inserting
        if (enrollment.getId() == null || enrollment.getId().trim().isEmpty()) {
            enrollment.setId(UUID.randomUUID().toString());
        }
        executorService.execute(() -> classEnrollmentDao.insertEnrollment(enrollment));
    }
    
    public void updateEnrollment(ClassEnrollmentEntity enrollment) {
        executorService.execute(() -> classEnrollmentDao.updateEnrollment(enrollment));
    }
    
    public void deleteEnrollment(ClassEnrollmentEntity enrollment) {
        executorService.execute(() -> classEnrollmentDao.deleteEnrollment(enrollment));
    }
    
    public LiveData<List<ClassEnrollmentEntity>> getActiveEnrollmentsByStudent(String studentId) {
        return classEnrollmentDao.getActiveEnrollmentsByStudent(studentId);
    }
    
    public LiveData<List<ClassEnrollmentEntity>> getActiveEnrollmentsByClass(String classId) {
        return classEnrollmentDao.getActiveEnrollmentsByClass(classId);
    }
    
    public LiveData<ClassEnrollmentEntity> getEnrollmentByStudentAndClass(String studentId, String classId) {
        return classEnrollmentDao.getEnrollmentByStudentAndClass(studentId, classId);
    }
    
    public LiveData<Integer> getActiveStudentCountForClass(String classId) {
        return classEnrollmentDao.getActiveStudentCountForClass(classId);
    }
    
    public LiveData<Integer> getActiveClassCountForStudent(String studentId) {
        return classEnrollmentDao.getActiveClassCountForStudent(studentId);
    }
    
    public void updateEnrollmentStatus(String studentId, String classId, boolean isActive) {
        executorService.execute(() -> classEnrollmentDao.updateEnrollmentStatus(studentId, classId, isActive));
    }
    
    public LiveData<List<ClassEnrollmentEntity>> getAllEnrollments() {
        return classEnrollmentDao.getAllEnrollments();
    }
    
    public void deleteAllEnrollments() {
        executorService.execute(() -> classEnrollmentDao.deleteAllEnrollments());
    }
    
    // Synchronous methods for fragments
    public List<AttendanceEntity> getAttendanceByStudent(String studentId) {
        try {
            return attendanceDao.getAttendancesByStudent(studentId).getValue();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<AttendanceEntity> getAttendanceByTeacher(String teacherId) {
        try {
            // Get classes taught by this teacher, then get attendance for those classes
            return attendanceDao.getAllAttendances().getValue(); // Simplified for now
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<AttendanceEntity> getAllAttendance() {
        try {
            return attendanceDao.getAllAttendances().getValue();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<ClassEntity> getEnrolledClasses(String studentId) {
        try {
            return classDao.getEnrolledClasses(studentId).getValue();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<ClassEntity> getClassesByTeacherSync(String teacherId) {
        try {
            return classDao.getClassesByTeacher(teacherId).getValue();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    // Authentication method
    public UserEntity authenticateUser(String username, String password) {
        try {
            Log.d("DataRepository", "Authenticating user: " + username);
            UserEntity user = userDao.authenticateUser(username, password);
            Log.d("DataRepository", "Authentication result: " + (user != null ? "success" : "failed"));
            return user;
        } catch (Exception e) {
            Log.e("DataRepository", "Error authenticating user: " + username, e);
            e.printStackTrace();
            return null;
        }
    }
    
    // Additional synchronous methods for DatabaseConnectionManager
    public List<AttendanceEntity> getAllAttendancesSync() {
        try {
            return attendanceDao.getAllAttendances().getValue();
        } catch (Exception e) {
            Log.e("DataRepository", "Error getting all attendances sync", e);
            return new ArrayList<>();
        }
    }
    
    public List<ClassEnrollmentEntity> getAllEnrollmentsSync() {
        try {
            return classEnrollmentDao.getAllEnrollments().getValue();
        } catch (Exception e) {
            Log.e("DataRepository", "Error getting all enrollments sync", e);
            return new ArrayList<>();
        }
    }
    
}
