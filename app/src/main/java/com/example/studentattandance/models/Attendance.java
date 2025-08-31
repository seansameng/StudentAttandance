package com.example.studentattandance.models;

import com.google.gson.annotations.SerializedName;

public class Attendance {
    @SerializedName("id")
    private String id;
    
    @SerializedName("studentId")
    private String studentId;
    
    @SerializedName("studentName")
    private String studentName;
    
    @SerializedName("classId")
    private String classId;
    
    @SerializedName("className")
    private String className;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("status")
    private AttendanceStatus status;
    
    @SerializedName("timeIn")
    private String timeIn;
    
    @SerializedName("timeOut")
    private String timeOut;
    
    @SerializedName("markedBy")
    private String markedBy;
    
    @SerializedName("notes")
    private String notes;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

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

    // Constructors
    public Attendance() {}

    public Attendance(String studentId, String classId, String date, AttendanceStatus status) {
        this.studentId = studentId;
        this.classId = classId;
        this.date = date;
        this.status = status;
    }

    public Attendance(String id, String studentId, String classId, String studentName, String className, 
                     String date, AttendanceStatus status, String timeIn, String timeOut, String markedBy, String notes) {
        this.id = id;
        this.studentId = studentId;
        this.classId = classId;
        this.studentName = studentName;
        this.className = className;
        this.date = date;
        this.status = status;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.markedBy = markedBy;
        this.notes = notes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(String markedBy) {
        this.markedBy = markedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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

    public String getStatusColor() {
        switch (status) {
            case PRESENT:
                return "#4CAF50"; // Green
            case ABSENT:
                return "#F44336"; // Red
            case LATE:
                return "#FF9800"; // Orange
            case EXCUSED:
                return "#2196F3"; // Blue
            case HALF_DAY:
                return "#9C27B0"; // Purple
            default:
                return "#757575"; // Gray
        }
    }
}
