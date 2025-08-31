package com.example.studentattandance.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.studentattandance.database.entities.ClassEnrollmentEntity;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.entities.AttendanceEntity;
import com.example.studentattandance.repository.DataRepository;

import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {
    
    private DataRepository repository;
    
    public DatabaseViewModel(Application application) {
        super(application);
        repository = new DataRepository(application);
    }
    
    // User operations
    public void insertUser(UserEntity user) {
        repository.insertUser(user);
    }
    
    public void updateUser(UserEntity user) {
        repository.updateUser(user);
    }
    
    public void deleteUser(UserEntity user) {
        repository.deleteUser(user);
    }
    
    public LiveData<UserEntity> getUserById(String userId) {
        return repository.getUserById(userId);
    }
    
    public LiveData<UserEntity> getUserByUsername(String username) {
        return repository.getUserByUsername(username);
    }
    
    public LiveData<List<UserEntity>> getAllUsers() {
        return repository.getAllUsers();
    }
    
    public LiveData<List<UserEntity>> getUsersByRole(String role) {
        return repository.getUsersByRole(role);
    }
    
    public void deleteAllUsers() {
        repository.deleteAllUsers();
    }
    
    // Class operations
    public void insertClass(com.example.studentattandance.database.entities.ClassEntity classEntity) {
        repository.insertClass(classEntity);
    }
    
    public LiveData<com.example.studentattandance.database.entities.ClassEntity> getClassById(String classId) {
        return repository.getClassById(classId);
    }
    
    public LiveData<List<com.example.studentattandance.database.entities.ClassEntity>> getAllClasses() {
        return repository.getAllClasses();
    }
    
    public void deleteAllClasses() {
        repository.deleteAllClasses();
    }
    
    // Attendance operations
    public void insertAttendance(AttendanceEntity attendance) {
        repository.insertAttendance(attendance);
    }
    
    public LiveData<List<AttendanceEntity>> getAllAttendances() {
        return repository.getAllAttendances();
    }
    
    public void deleteAllAttendances() {
        repository.deleteAllAttendances();
    }
    
    // Additional methods for the new activities
    public LiveData<List<com.example.studentattandance.database.entities.ClassEntity>> getClassesByTeacher(String teacherId) {
        return repository.getClassesByTeacher(teacherId);
    }
    
    public void updateClass(com.example.studentattandance.database.entities.ClassEntity classEntity) {
        repository.updateClass(classEntity);
    }
    
    public void deleteClass(com.example.studentattandance.database.entities.ClassEntity classEntity) {
        repository.deleteClass(classEntity);
    }
    
    public void updateAttendance(AttendanceEntity attendanceEntity) {
        repository.updateAttendance(attendanceEntity);
    }
    
    public void deleteAttendance(AttendanceEntity attendanceEntity) {
        repository.deleteAttendance(attendanceEntity);
    }
}
