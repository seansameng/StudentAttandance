package com.example.studentattandance.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

import com.example.studentattandance.database.converters.DateConverter;
import java.util.Date;

@Entity(
    tableName = "attendance",
    foreignKeys = {
        @ForeignKey(
            entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "studentId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = ClassEntity.class,
            parentColumns = "id",
            childColumns = "classId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = {"studentId", "classId", "date"}, unique = true),
        @Index(value = "studentId"),
        @Index(value = "classId"),
        @Index(value = "date")
    }
)
public class AttendanceEntity {
    @PrimaryKey
    @NonNull
    private String id;
    
    @NonNull
    private String studentId;
    @NonNull
    private String classId;
    
    @NonNull
    private String date;
    @NonNull
    private String status; // PRESENT, ABSENT, LATE
    
    private String className; // Display name for the class
    
    @TypeConverters(DateConverter.class)
    private Date createdAt;
    
    @TypeConverters(DateConverter.class)
    private Date markedAt; // When attendance was marked
    
    // Constructor
    public AttendanceEntity() {}
    
    @Ignore
    public AttendanceEntity(@NonNull String id, @NonNull String studentId, @NonNull String classId, @NonNull String date, @NonNull String status, String className, Date createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.classId = classId;
        this.date = date;
        this.status = status;
        this.className = className;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(@NonNull String studentId) { this.studentId = studentId; }
    
    public String getClassId() { return classId; }
    public void setClassId(@NonNull String classId) { this.classId = classId; }
    
    public String getDate() { return date; }
    public void setDate(@NonNull String date) { this.date = date; }
    
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getMarkedAt() { return markedAt; }
    public void setMarkedAt(Date markedAt) { this.markedAt = markedAt; }
}
