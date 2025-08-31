package com.example.studentattendance.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_enrollments")
@EntityListeners(AuditingEntityListener.class)
public class ClassEnrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class classObj;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;
    
    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public ClassEnrollment() {}
    
    public ClassEnrollment(Class classObj, User user) {
        this.classObj = classObj;
        this.user = user;
        this.enrollmentDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Class getClassObj() { return classObj; }
    public void setClassObj(Class classObj) { this.classObj = classObj; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
    
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Enum for Enrollment Status
    public enum EnrollmentStatus {
        ENROLLED("Enrolled"),
        DROPPED("Dropped"),
        SUSPENDED("Suspended"),
        GRADUATED("Graduated");
        
        private final String displayName;
        
        EnrollmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
