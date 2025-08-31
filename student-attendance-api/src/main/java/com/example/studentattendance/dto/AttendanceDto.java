package com.example.studentattendance.dto;

import com.example.studentattendance.models.Attendance;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AttendanceDto {
    
    // Mark Attendance Request
    public static class MarkAttendanceRequest {
        @NotNull(message = "Student ID is required")
        private Long studentId;
        
        @NotNull(message = "Class ID is required")
        private Long classId;
        
        @NotNull(message = "Date is required")
        private LocalDate date;
        
        @NotNull(message = "Status is required")
        private Attendance.AttendanceStatus status;
        
        private String timeIn;
        private String timeOut;
        private String notes;
        private String markedBy;
        
        // Getters and Setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public Attendance.AttendanceStatus getStatus() { return status; }
        public void setStatus(Attendance.AttendanceStatus status) { this.status = status; }
        
        public String getTimeIn() { return timeIn; }
        public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
        
        public String getTimeOut() { return timeOut; }
        public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public String getMarkedBy() { return markedBy; }
        public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }
    }
    
    // Bulk Attendance Request
    public static class BulkAttendanceRequest {
        @NotNull(message = "Class ID is required")
        private Long classId;
        
        @NotNull(message = "Date is required")
        private LocalDate date;
        
        @NotNull(message = "Attendances list is required")
        private List<MarkAttendanceRequest> attendances;
        
        private String markedBy;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public List<MarkAttendanceRequest> getAttendances() { return attendances; }
        public void setAttendances(List<MarkAttendanceRequest> attendances) { this.attendances = attendances; }
        
        public String getMarkedBy() { return markedBy; }
        public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }
    }
    
    // Update Attendance Request
    public static class UpdateAttendanceRequest {
        private Attendance.AttendanceStatus status;
        private String timeIn;
        private String timeOut;
        private String notes;
        
        // Getters and Setters
        public Attendance.AttendanceStatus getStatus() { return status; }
        public void setStatus(Attendance.AttendanceStatus status) { this.status = status; }
        
        public String getTimeIn() { return timeIn; }
        public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
        
        public String getTimeOut() { return timeOut; }
        public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    // Attendance Response
    public static class AttendanceResponse {
        private Long id;
        private Long studentId;
        private String studentName;
        private Long classId;
        private String className;
        private LocalDate date;
        private Attendance.AttendanceStatus status;
        private String timeIn;
        private String timeOut;
        private String notes;
        private String markedBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public Attendance.AttendanceStatus getStatus() { return status; }
        public void setStatus(Attendance.AttendanceStatus status) { this.status = status; }
        
        public String getTimeIn() { return timeIn; }
        public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
        
        public String getTimeOut() { return timeOut; }
        public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public String getMarkedBy() { return markedBy; }
        public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
    
    // Class Attendance Statistics
    public static class ClassAttendanceStats {
        private Long classId;
        private String className;
        private int totalRecords;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double averageAttendance;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
    }
    
    // Student Attendance Summary
    public static class StudentAttendanceSummary {
        private Long studentId;
        private String studentName;
        private int totalRecords;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double attendancePercentage;
        
        // Getters and Setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
    
    // Overall Attendance Statistics
    public static class OverallAttendanceStats {
        private int totalRecords;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double averageAttendance;
        
        // Getters and Setters
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
    }
    
    // Recent Attendance Activity
    public static class RecentAttendanceActivity {
        private Long id;
        private String studentName;
        private String className;
        private Attendance.AttendanceStatus status;
        private LocalDate date;
        private String timeIn;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public Attendance.AttendanceStatus getStatus() { return status; }
        public void setStatus(Attendance.AttendanceStatus status) { this.status = status; }
        
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public String getTimeIn() { return timeIn; }
        public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
    }
    
    // Attendance History Item
    public static class AttendanceHistoryItem {
        private Long id;
        private String className;
        private Attendance.AttendanceStatus status;
        private LocalDate date;
        private String timeIn;
        private String timeOut;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public Attendance.AttendanceStatus getStatus() { return status; }
        public void setStatus(Attendance.AttendanceStatus status) { this.status = status; }
        
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public String getTimeIn() { return timeIn; }
        public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
        
        public String getTimeOut() { return timeOut; }
        public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
    }
    
    // Attendance Summary by Date
    public static class AttendanceSummaryByDate {
        private LocalDate date;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private int totalCount;
        private double attendancePercentage;
        
        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
    
    // Attendance Summary by Class
    public static class AttendanceSummaryByClass {
        private Long classId;
        private String className;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private int totalCount;
        private double attendancePercentage;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
}
