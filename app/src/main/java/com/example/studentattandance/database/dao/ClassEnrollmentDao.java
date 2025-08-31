package com.example.studentattandance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentattandance.database.entities.ClassEnrollmentEntity;

import java.util.List;

@Dao
public interface ClassEnrollmentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEnrollment(ClassEnrollmentEntity enrollment);
    
    @Update
    void updateEnrollment(ClassEnrollmentEntity enrollment);
    
    @Delete
    void deleteEnrollment(ClassEnrollmentEntity enrollment);
    
    @Query("SELECT * FROM class_enrollments WHERE id = :enrollmentId")
    LiveData<ClassEnrollmentEntity> getEnrollmentById(String enrollmentId);
    
    @Query("SELECT * FROM class_enrollments WHERE studentId = :studentId AND isActive = 1")
    LiveData<List<ClassEnrollmentEntity>> getActiveEnrollmentsByStudent(String studentId);
    
    @Query("SELECT * FROM class_enrollments WHERE classId = :classId AND isActive = 1")
    LiveData<List<ClassEnrollmentEntity>> getActiveEnrollmentsByClass(String classId);
    
    @Query("SELECT * FROM class_enrollments WHERE classId = :classId AND isActive = 1")
    List<ClassEnrollmentEntity> getActiveEnrollmentsByClassSync(String classId);
    
    @Query("SELECT * FROM class_enrollments WHERE studentId = :studentId AND classId = :classId")
    LiveData<ClassEnrollmentEntity> getEnrollmentByStudentAndClass(String studentId, String classId);
    
    @Query("SELECT COUNT(*) FROM class_enrollments WHERE classId = :classId AND isActive = 1")
    LiveData<Integer> getActiveStudentCountForClass(String classId);
    
    @Query("SELECT COUNT(*) FROM class_enrollments WHERE studentId = :studentId AND isActive = 1")
    LiveData<Integer> getActiveClassCountForStudent(String studentId);
    
    @Query("SELECT COUNT(*) FROM class_enrollments WHERE studentId = :studentId AND isActive = 1")
    Integer getActiveClassCountForStudentSync(String studentId);
    
    @Query("UPDATE class_enrollments SET isActive = :isActive WHERE studentId = :studentId AND classId = :classId")
    void updateEnrollmentStatus(String studentId, String classId, boolean isActive);
    
    @Query("SELECT * FROM class_enrollments ORDER BY enrolledAt DESC")
    LiveData<List<ClassEnrollmentEntity>> getAllEnrollments();
    
    @Query("DELETE FROM class_enrollments")
    void deleteAllEnrollments();
}
