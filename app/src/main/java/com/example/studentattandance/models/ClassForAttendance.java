package com.example.studentattandance.models;

public class ClassForAttendance {
    private String id;
    private String className;
    private String subject;
    private String schedule;
    private String room;
    private int studentCount;
    private String lastAttendance;
    private boolean isActive;

    public ClassForAttendance() {}

    public ClassForAttendance(String id, String className, String subject, String schedule, String room, int studentCount, String lastAttendance, boolean isActive) {
        this.id = id;
        this.className = className;
        this.subject = subject;
        this.schedule = schedule;
        this.room = room;
        this.studentCount = studentCount;
        this.lastAttendance = lastAttendance;
        this.isActive = isActive;
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

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public String getLastAttendance() {
        return lastAttendance;
    }

    public void setLastAttendance(String lastAttendance) {
        this.lastAttendance = lastAttendance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStudentCountText() {
        return studentCount + " student" + (studentCount != 1 ? "s" : "");
    }
}
