package com.example.studentattendance.repositories;

import com.example.studentattendance.models.Attendance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Find by student, class, and date
    Attendance findByStudentIdAndClassIdAndDate(Long studentId, Long classId, LocalDate date);

    // Find by class and date
    List<Attendance> findByClassIdAndDate(Long classId, LocalDate date);

    // Find by student and date range
    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);

    // Find by class and date range
    List<Attendance> findByClassIdAndDateBetween(Long classId, LocalDate startDate, LocalDate endDate);

    // Find by date range
    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Find by date
    List<Attendance> findByDate(LocalDate date);

    // Find by class, student and date range
    List<Attendance> findByClassIdAndStudentIdAndDateBetween(Long classId, Long studentId, LocalDate startDate,
            LocalDate endDate);

    // Find by date
    List<Attendance> findByDate(LocalDate date);

    // Find by class, student and date range
    List<Attendance> findByClassIdAndStudentIdAndDateBetween(Long classId, Long studentId, LocalDate startDate,
            LocalDate endDate);

    // Find by date
    List<Attendance> findByDate(LocalDate date);

    // Find by class, student and date range
    List<Attendance> findByClassIdAndStudentIdAndDateBetween(Long classId, Long studentId, LocalDate startDate,
            LocalDate endDate);

    // Find by teacher and date range
    @Query("SELECT a FROM Attendance a WHERE a.classObj.teacher.id = :teacherId AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByTeacherIdAndDateBetween(@Param("teacherId") Long teacherId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Count by date and status
    int countByDateAndStatus(LocalDate date, Attendance.AttendanceStatus status);

    // Count by teacher, date, and status
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.classObj.teacher.id = :teacherId AND a.date = :date AND a.status = :status")
    int countByTeacherIdAndDateAndStatus(@Param("teacherId") Long teacherId,
            @Param("date") LocalDate date,
            @Param("status") Attendance.AttendanceStatus status);

    // Count by date and updated after
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.date = :date AND a.updatedAt > :updatedAfter")
    int countByDateAndUpdatedAtAfter(@Param("date") LocalDate date,
            @Param("updatedAfter") LocalDateTime updatedAfter);

    // Count distinct students by teacher
    @Query("SELECT COUNT(DISTINCT a.student.id) FROM Attendance a WHERE a.classObj.teacher.id = :teacherId")
    int countDistinctStudentsByTeacherId(@Param("teacherId") Long teacherId);

    // Find recent by teacher
    @Query("SELECT a FROM Attendance a WHERE a.classObj.teacher.id = :teacherId ORDER BY a.updatedAt DESC")
    List<Attendance> findRecentByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    // Find recent by student
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId ORDER BY a.date DESC")
    List<Attendance> findRecentByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    // Find by student and class
    List<Attendance> findByStudentIdAndClassId(Long studentId, Long classId);

    // Find by class and status
    List<Attendance> findByClassIdAndStatus(Long classId, Attendance.AttendanceStatus status);

    // Find by student and status
    List<Attendance> findByStudentIdAndStatus(Long studentId, Attendance.AttendanceStatus status);

    // Find by date and status
    List<Attendance> findByDateAndStatus(LocalDate date, Attendance.AttendanceStatus status);

    // Count by class and status
    int countByClassIdAndStatus(Long classId, Attendance.AttendanceStatus status);

    // Count by student and status
    int countByStudentIdAndStatus(Long studentId, Attendance.AttendanceStatus status);

    // Find by date range and status
    List<Attendance> findByDateBetweenAndStatus(LocalDate startDate, LocalDate endDate,
            Attendance.AttendanceStatus status);

    // Find by class and date range and status
    List<Attendance> findByClassIdAndDateBetweenAndStatus(Long classId, LocalDate startDate, LocalDate endDate,
            Attendance.AttendanceStatus status);

    // Find by student and date range and status
    List<Attendance> findByStudentIdAndDateBetweenAndStatus(Long studentId, LocalDate startDate, LocalDate endDate,
            Attendance.AttendanceStatus status);

    // Find by teacher and date range and status
    @Query("SELECT a FROM Attendance a WHERE a.classObj.teacher.id = :teacherId AND a.date BETWEEN :startDate AND :endDate AND a.status = :status")
    List<Attendance> findByTeacherIdAndDateBetweenAndStatus(@Param("teacherId") Long teacherId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Attendance.AttendanceStatus status);

    // Find by date and time range (for late attendance)
    @Query("SELECT a FROM Attendance a WHERE a.date = :date AND a.timeIn > :lateTime")
    List<Attendance> findByDateAndTimeInAfter(@Param("date") LocalDate date,
            @Param("lateTime") String lateTime);

    // Find by student and month
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    List<Attendance> findByStudentIdAndYearAndMonth(@Param("studentId") Long studentId,
            @Param("year") int year,
            @Param("month") int month);

    // Find by class and month
    @Query("SELECT a FROM Attendance a WHERE a.classObj.id = :classId AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    List<Attendance> findByClassIdAndYearAndMonth(@Param("classId") Long classId,
            @Param("year") int year,
            @Param("month") int month);

    // Find by teacher and month
    @Query("SELECT a FROM Attendance a WHERE a.classObj.teacher.id = :teacherId AND YEAR(a.date) = :year AND MONTH(a.date) = :month")
    List<Attendance> findByTeacherIdAndYearAndMonth(@Param("teacherId") Long teacherId,
            @Param("year") int year,
            @Param("month") int month);

    // Get attendance summary by class and date range
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.classObj.id = :classId AND a.date BETWEEN :startDate AND :endDate GROUP BY a.status")
    List<Object[]> getAttendanceSummaryByClassAndDateRange(@Param("classId") Long classId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Get attendance summary by student and date range
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.date BETWEEN :startDate AND :endDate GROUP BY a.status")
    List<Object[]> getAttendanceSummaryByStudentAndDateRange(@Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Get attendance summary by teacher and date range
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.classObj.teacher.id = :teacherId AND a.date BETWEEN :startDate AND :endDate GROUP BY a.status")
    List<Object[]> getAttendanceSummaryByTeacherAndDateRange(@Param("teacherId") Long teacherId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Find by notes containing text
    List<Attendance> findByNotesContainingIgnoreCase(String notes);

    // Find by marked by user
    List<Attendance> findByMarkedBy(String markedBy);

    // Find by date and marked by
    List<Attendance> findByDateAndMarkedBy(LocalDate date, String markedBy);

    // Find by class and marked by
    List<Attendance> findByClassIdAndMarkedBy(Long classId, String markedBy);

    // Find by student and marked by
    List<Attendance> findByStudentIdAndMarkedBy(Long studentId, String markedBy);

    // Get daily attendance count by status
    @Query("SELECT a.date, a.status, COUNT(a) FROM Attendance a WHERE a.date BETWEEN :startDate AND :endDate GROUP BY a.date, a.status ORDER BY a.date")
    List<Object[]> getDailyAttendanceCountByStatus(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Get weekly attendance count by status
    @Query("SELECT YEARWEEK(a.date) as week, a.status, COUNT(a) FROM Attendance a WHERE a.date BETWEEN :startDate AND :endDate GROUP BY YEARWEEK(a.date), a.status ORDER BY week")
    List<Object[]> getWeeklyAttendanceCountByStatus(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Get monthly attendance count by status
    @Query("SELECT YEAR(a.date) as year, MONTH(a.date) as month, a.status, COUNT(a) FROM Attendance a WHERE a.date BETWEEN :startDate AND :endDate GROUP BY YEAR(a.date), MONTH(a.date), a.status ORDER BY year, month")
    List<Object[]> getMonthlyAttendanceCountByStatus(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
