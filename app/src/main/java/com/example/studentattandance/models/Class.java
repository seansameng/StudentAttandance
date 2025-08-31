package com.example.studentattandance.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Class {
    @SerializedName("id")
    private String id;
    
    @SerializedName("className")
    private String className;
    
    @SerializedName("subject")
    private String subject;
    
    @SerializedName("teacherId")
    private String teacherId;
    
    @SerializedName("teacherName")
    private String teacherName;
    
    @SerializedName("schedule")
    private String schedule;
    
    @SerializedName("room")
    private String room;
    
    @SerializedName("semester")
    private String semester;
    
    @SerializedName("academicYear")
    private String academicYear;
    
    @SerializedName("maxStudents")
    private int maxStudents;
    
    @SerializedName("enrolledStudents")
    private List<String> enrolledStudents;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("createdAt")
    private String createdAt;

    // Constructors
    public Class() {}

    public Class(String id, String className, String subject, String teacherId, String schedule) {
        this.id = id;
        this.className = className;
        this.subject = subject;
        this.teacherId = teacherId;
        this.schedule = schedule;
    }

    public Class(String id, String className, String subject, String teacherId, String teacherName, 
                 String schedule, String room, String semester, String academicYear, int maxStudents, 
                 List<String> enrolledStudents, String description, boolean isActive, String createdAt) {
        this.id = id;
        this.className = className;
        this.subject = subject;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.schedule = schedule;
        this.room = room;
        this.semester = semester;
        this.academicYear = academicYear;
        this.maxStudents = maxStudents;
        this.enrolledStudents = enrolledStudents;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getEnrolledCount() {
        return enrolledStudents != null ? enrolledStudents.size() : 0;
    }

    public boolean isFull() {
        return getEnrolledCount() >= maxStudents;
    }

    public String getDisplayName() {
        return className + " - " + subject;
    }
}
