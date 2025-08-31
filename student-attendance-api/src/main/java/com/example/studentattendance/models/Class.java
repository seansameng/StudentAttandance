package com.example.studentattendance.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "classes")
@EntityListeners(AuditingEntityListener.class)
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Class name is required")
    @Column(name = "class_name", nullable = false)
    private String className;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotNull(message = "Teacher is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @NotBlank(message = "Schedule is required")
    private String schedule;

    private String room;

    @Column(name = "max_students")
    @Positive(message = "Max students must be positive")
    private Integer maxStudents;

    private String description;

    @Column(name = "semester")
    private String semester;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "is_active")
    private boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "classObj", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClassEnrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "classObj", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Attendance> attendances = new HashSet<>();

    // Constructors
    public Class() {
    }

    public Class(String className, String subject, User teacher, String schedule, String room,
            Integer maxStudents, String description, String semester, String academicYear) {
        this.className = className;
        this.subject = subject;
        this.teacher = teacher;
        this.schedule = schedule;
        this.room = room;
        this.maxStudents = maxStudents;
        this.description = description;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public String getTeacherName() {
        return teacher != null ? teacher.getFullName() : "Unknown Teacher";
    }

    public Long getTeacherId() {
        return teacher != null ? teacher.getId() : null;
    }

    public void setTeacherId(Long teacherId) {
        if (teacherId != null) {
            User teacherUser = new User();
            teacherUser.setId(teacherId);
            this.teacher = teacherUser;
        } else {
            this.teacher = null;
        }
    }

    public int getEnrolledCount() {
        return enrollments != null ? enrollments.size() : 0;
    }

    public boolean hasAvailableSeats() {
        return maxStudents == null || getEnrolledCount() < maxStudents;
    }
}
