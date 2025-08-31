package com.example.studentattendance.repositories;

import com.example.studentattendance.models.Class;
import com.example.studentattendance.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    
    List<Class> findByIsActiveTrue();
    
    List<Class> findByTeacher(User teacher);
    
    List<Class> findByTeacherId(Long teacherId);
    
    List<Class> findBySemester(String semester);
    
    List<Class> findByAcademicYear(String academicYear);
    
    List<Class> findBySemesterAndAcademicYear(String semester, String academicYear);
    
    List<Class> findBySubject(String subject);
    
    List<Class> findByRoom(String room);
    
    @Query("SELECT c FROM Class c WHERE c.isActive = true AND c.schedule LIKE %:day%")
    List<Class> findByScheduleDay(@Param("day") String day);
    
    @Query("SELECT c FROM Class c WHERE c.isActive = true AND c.createdAt >= :startDate AND c.createdAt <= :endDate")
    List<Class> findUpcomingClasses(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c FROM Class c WHERE c.isActive = true AND (c.className LIKE %:searchTerm% OR c.subject LIKE %:searchTerm% OR c.description LIKE %:searchTerm%)")
    List<Class> searchClasses(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(c) FROM Class c WHERE c.isActive = true")
    long countByIsActiveTrue();
    
    @Query("SELECT COUNT(c) FROM Class c WHERE c.teacher.id = :teacherId")
    long countByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT COUNT(c) FROM Class c WHERE c.semester = :semester AND c.academicYear = :academicYear")
    long countBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") String academicYear);
}
