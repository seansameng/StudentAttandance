package com.example.studentattendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public class ClassDto {
    
    // Create Class Request
    public static class CreateClassRequest {
        @NotBlank(message = "Class name is required")
        private String className;
        
        @NotBlank(message = "Subject is required")
        private String subject;
        
        @NotNull(message = "Teacher ID is required")
        private Long teacherId;
        
        @NotBlank(message = "Schedule is required")
        private String schedule;
        
        @NotBlank(message = "Room is required")
        private String room;
        
        @NotNull(message = "Max students is required")
        @Positive(message = "Max students must be positive")
        private Integer maxStudents;
        
        private String description;
        private String semester;
        private String academicYear;
        
        // Getters and Setters
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
        
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        
        public Integer getMaxStudents() { return maxStudents; }
        public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        
        public String getAcademicYear() { return academicYear; }
        public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    }
    
    // Update Class Request
    public static class UpdateClassRequest {
        private String className;
        private String subject;
        private String schedule;
        private String room;
        private Integer maxStudents;
        private String description;
        private String semester;
        private String academicYear;
        
        // Getters and Setters
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        
        public Integer getMaxStudents() { return maxStudents; }
        public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        
        public String getAcademicYear() { return academicYear; }
        public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    }
    
    // Class Response
    public static class ClassResponse {
        private Long id;
        private String className;
        private String subject;
        private Long teacherId;
        private String teacherName;
        private String schedule;
        private String room;
        private Integer maxStudents;
        private String description;
        private String semester;
        private String academicYear;
        private boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
        
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        
        public Integer getMaxStudents() { return maxStudents; }
        public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        
        public String getAcademicYear() { return academicYear; }
        public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
    
    // Class Statistics
    public static class ClassStats {
        private Long classId;
        private String className;
        private int totalStudents;
        private int maxStudents;
        private double enrollmentPercentage;
        private boolean isActive;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getMaxStudents() { return maxStudents; }
        public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
        
        public double getEnrollmentPercentage() { return enrollmentPercentage; }
        public void setEnrollmentPercentage(double enrollmentPercentage) { this.enrollmentPercentage = enrollmentPercentage; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
    
    // Overall Class Statistics
    public static class OverallClassStats {
        private long totalClasses;
        private long activeClasses;
        private long totalStudents;
        private double averageClassSize;
        
        // Getters and Setters
        public long getTotalClasses() { return totalClasses; }
        public void setTotalClasses(long totalClasses) { this.totalClasses = totalClasses; }
        
        public long getActiveClasses() { return activeClasses; }
        public void setActiveClasses(long activeClasses) { this.activeClasses = activeClasses; }
        
        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
        
        public double getAverageClassSize() { return averageClassSize; }
        public void setAverageClassSize(double averageClassSize) { this.averageClassSize = averageClassSize; }
    }
    
    // Class Search Result
    public static class ClassSearchResult {
        private Long id;
        private String className;
        private String subject;
        private String teacherName;
        private String schedule;
        private String room;
        private boolean isActive;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
    
    // Class Schedule
    public static class ClassSchedule {
        private Long classId;
        private String className;
        private String subject;
        private String schedule;
        private String room;
        private String teacherName;
        private boolean isActive;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
    
    // Class Enrollment
    public static class ClassEnrollment {
        private Long classId;
        private String className;
        private Long studentId;
        private String studentName;
        private String enrollmentDate;
        private String status;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public String getEnrollmentDate() { return enrollmentDate; }
        public void setEnrollmentDate(String enrollmentDate) { this.enrollmentDate = enrollmentDate; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    // Class Details
    public static class ClassDetails {
        private Long id;
        private String className;
        private String subject;
        private Long teacherId;
        private String teacherName;
        private String schedule;
        private String room;
        private Integer maxStudents;
        private String description;
        private String semester;
        private String academicYear;
        private boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<ClassEnrollment> enrollments;
        private int currentEnrollment;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
        
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        
        public Integer getMaxStudents() { return maxStudents; }
        public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }
        
        public String getAcademicYear() { return academicYear; }
        public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public List<ClassEnrollment> getEnrollments() { return enrollments; }
        public void setEnrollments(List<ClassEnrollment> enrollments) { this.enrollments = enrollments; }
        
        public int getCurrentEnrollment() { return currentEnrollment; }
        public void setCurrentEnrollment(int currentEnrollment) { this.currentEnrollment = currentEnrollment; }
    }
}
