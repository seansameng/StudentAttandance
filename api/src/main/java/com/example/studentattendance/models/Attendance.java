package com.example.studentattendance.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@EntityListeners(AuditingEntityListener.class)
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Class is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class classEntity;
    
    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "time_in")
    private LocalTime timeIn;
    
    @Column(name = "time_out")
    private LocalTime timeOut;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by")
    private User markedBy;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Attendance() {}
    
    public Attendance(Class classEntity, User student, LocalDate date, AttendanceStatus status) {
        this.classEntity = classEntity;
        this.student = student;
        this.date = date;
        this.status = status;
    }
    
    public Attendance(Class classEntity, User student, LocalDate date, LocalTime timeIn, 
                     AttendanceStatus status, User markedBy) {
        this.classEntity = classEntity;
        this.student = student;
        this.date = date;
        this.timeIn = timeIn;
        this.status = status;
        this.markedBy = markedBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Class getClassEntity() { return classEntity; }
    public void setClassEntity(Class classEntity) { this.classEntity = classEntity; }
    
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalTime getTimeIn() { return timeIn; }
    public void setTimeIn(LocalTime timeIn) { this.timeIn = timeIn; }
    
    public LocalTime getTimeOut() { return timeOut; }
    public void setTimeOut(LocalTime timeOut) { this.timeOut = timeOut; }
    
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public User getMarkedBy() { return markedBy; }
    public void setMarkedBy(User markedBy) { this.markedBy = markedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public String getStudentName() {
        return student != null ? student.getFullName() : "Unknown";
    }
    
    public String getClassName() {
        return classEntity != null ? classEntity.getClassName() : "Unknown";
    }
    
    public String getMarkedByName() {
        return markedBy != null ? markedBy.getFullName() : "System";
    }
    
    public boolean isPresent() {
        return status == AttendanceStatus.PRESENT;
    }
    
    public boolean isAbsent() {
        return status == AttendanceStatus.ABSENT;
    }
    
    public boolean isLate() {
        return status == AttendanceStatus.LATE;
    }
    
    public boolean isExcused() {
        return status == AttendanceStatus.EXCUSED;
    }
    
    public boolean isHalfDay() {
        return status == AttendanceStatus.HALF_DAY;
    }
    
    // Attendance Status Enum
    public enum AttendanceStatus {
        PRESENT("Present"),
        ABSENT("Absent"),
        LATE("Late"),
        EXCUSED("Excused"),
        HALF_DAY("Half Day");
        
        private final String displayName;
        
        AttendanceStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}


























