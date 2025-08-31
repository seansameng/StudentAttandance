package com.example.studentattandance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentattandance.database.entities.AttendanceEntity;

import java.util.List;

@Dao
public interface AttendanceDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttendance(AttendanceEntity attendance);
    
    @Update
    void updateAttendance(AttendanceEntity attendance);
    
    @Delete
    void deleteAttendance(AttendanceEntity attendance);
    
    @Query("SELECT * FROM attendance WHERE id = :attendanceId")
    LiveData<AttendanceEntity> getAttendanceById(String attendanceId);
    
    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    LiveData<List<AttendanceEntity>> getAttendancesByStudent(String studentId);
    
    @Query("SELECT * FROM attendance WHERE classId = :classId")
    LiveData<List<AttendanceEntity>> getAttendancesByClass(String classId);
    
    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND classId = :classId")
    LiveData<List<AttendanceEntity>> getAttendancesByStudentAndClass(String studentId, String classId);
    
    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND date = :date")
    LiveData<List<AttendanceEntity>> getAttendancesByStudentAndDate(String studentId, String date);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND classId = :classId AND status = 'PRESENT'")
    LiveData<Integer> getPresentCountByStudentAndClass(String studentId, String classId);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND classId = :classId AND status = 'ABSENT'")
    LiveData<Integer> getAbsentCountByStudentAndClass(String studentId, String classId);
    
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    LiveData<List<AttendanceEntity>> getAllAttendances();
    
    @Query("DELETE FROM attendance")
    void deleteAllAttendances();
    
    // Additional reporting queries
    @Query("SELECT COUNT(*) FROM attendance WHERE classId = :classId AND date = :date AND status = 'PRESENT'")
    LiveData<Integer> getPresentCountByClassAndDate(String classId, String date);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE classId = :classId AND date = :date AND status = 'ABSENT'")
    LiveData<Integer> getAbsentCountByClassAndDate(String classId, String date);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE classId = :classId AND date = :date AND status = 'LATE'")
    LiveData<Integer> getLateCountByClassAndDate(String classId, String date);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE classId = :classId AND date = :date")
    LiveData<Integer> getTotalCountByClassAndDate(String classId, String date);
    
    @Query("SELECT * FROM attendance WHERE classId = :classId AND date = :date ORDER BY studentId")
    LiveData<List<AttendanceEntity>> getAttendancesByClassAndDate(String classId, String date);
    
    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND classId = :classId ORDER BY date DESC")
    LiveData<List<AttendanceEntity>> getAttendanceHistoryByStudentAndClass(String studentId, String classId);
    
    // Student attendance statistics
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND status = 'PRESENT'")
    LiveData<Integer> getPresentCountByStudent(String studentId);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId")
    LiveData<Integer> getTotalAttendanceCountByStudent(String studentId);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND status = 'PRESENT'")
    Integer getPresentCountByStudentSync(String studentId);
    
    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId")
    Integer getTotalAttendanceCountByStudentSync(String studentId);
}
