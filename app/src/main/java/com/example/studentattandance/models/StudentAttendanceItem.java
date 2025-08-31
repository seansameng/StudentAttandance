package com.example.studentattandance.models;

public class StudentAttendanceItem {
    private String studentId;
    private String studentName;
    private String studentIdNumber;
    private boolean isPresent;
    private boolean isAbsent;
    private String notes;

    public StudentAttendanceItem() {}

    public StudentAttendanceItem(String studentId, String studentName, String studentIdNumber) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentIdNumber = studentIdNumber;
        this.isPresent = false;
        this.isAbsent = false;
        this.notes = "";
    }

    // Getters and Setters
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

    public String getStudentIdNumber() {
        return studentIdNumber;
    }

    public void setStudentIdNumber(String studentIdNumber) {
        this.studentIdNumber = studentIdNumber;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        this.isPresent = present;
        if (present) {
            this.isAbsent = false;
        }
    }

    public boolean isAbsent() {
        return isAbsent;
    }

    public void setAbsent(boolean absent) {
        this.isAbsent = absent;
        if (absent) {
            this.isPresent = false;
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAttendanceStatus() {
        if (isPresent) {
            return "Present";
        } else if (isAbsent) {
            return "Absent";
        } else {
            return "Not Marked";
        }
    }

    public boolean isAttendanceMarked() {
        return isPresent || isAbsent;
    }
}
