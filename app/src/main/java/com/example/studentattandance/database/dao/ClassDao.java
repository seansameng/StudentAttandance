package com.example.studentattandance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentattandance.database.entities.ClassEntity;

import java.util.List;

@Dao
public interface ClassDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClass(ClassEntity classEntity);
    
    @Update
    void updateClass(ClassEntity classEntity);
    
    @Delete
    void deleteClass(ClassEntity classEntity);
    
    @Query("SELECT * FROM classes WHERE id = :classId")
    LiveData<ClassEntity> getClassById(String classId);
    
    @Query("SELECT * FROM classes WHERE id = :classId")
    ClassEntity getClassByIdSync(String classId);
    
    @Query("SELECT * FROM classes WHERE teacherId = :teacherId")
    LiveData<List<ClassEntity>> getClassesByTeacher(String teacherId);
    
    @Query("SELECT * FROM classes WHERE schedule LIKE '%' || :day || '%'")
    LiveData<List<ClassEntity>> getClassesByDay(String day);
    
    @Query("SELECT * FROM classes ORDER BY className")
    LiveData<List<ClassEntity>> getAllClasses();
    
    @Query("SELECT * FROM classes ORDER BY className")
    List<ClassEntity> getAllClassesSync();
    
    @Query("DELETE FROM classes")
    void deleteAllClasses();
    
    // Additional useful queries
    @Query("SELECT * FROM classes WHERE subject = :subject")
    LiveData<List<ClassEntity>> getClassesBySubject(String subject);
    
    @Query("SELECT * FROM classes WHERE room = :room")
    LiveData<List<ClassEntity>> getClassesByRoom(String room);
    
    @Query("SELECT * FROM classes WHERE schedule LIKE '%' || :time || '%'")
    LiveData<List<ClassEntity>> getClassesByTime(String time);
    
    @Query("SELECT COUNT(*) FROM classes WHERE teacherId = :teacherId")
    LiveData<Integer> getClassCountByTeacher(String teacherId);
    
    @Query("SELECT * FROM classes WHERE className LIKE '%' || :searchQuery || '%' OR subject LIKE '%' || :searchQuery || '%'")
    LiveData<List<ClassEntity>> searchClasses(String searchQuery);
    
    @Query("SELECT c.* FROM classes c " +
           "INNER JOIN class_enrollments ce ON c.id = ce.classId " +
           "WHERE ce.studentId = :studentId AND ce.isActive = 1")
    LiveData<List<ClassEntity>> getEnrolledClasses(String studentId);
}
