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
    tableName = "class_enrollments",
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
        @Index(value = {"studentId", "classId"}, unique = true),
        @Index(value = "studentId"),
        @Index(value = "classId")
    }
)
public class ClassEnrollmentEntity {
    @PrimaryKey
    @NonNull
    private String id;
    
    @NonNull
    private String studentId;
    
    @NonNull
    private String classId;
    
    @TypeConverters(DateConverter.class)
    private Date enrolledAt;
    
    private boolean isActive;
    
    // Constructor
    public ClassEnrollmentEntity() {}
    
    @Ignore
    public ClassEnrollmentEntity(@NonNull String id, @NonNull String studentId, @NonNull String classId, 
                                Date enrolledAt, boolean isActive) {
        this.id = id;
        this.studentId = studentId;
        this.classId = classId;
        this.enrolledAt = enrolledAt;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(@NonNull String studentId) { this.studentId = studentId; }
    
    public String getClassId() { return classId; }
    public void setClassId(@NonNull String classId) { this.classId = classId; }
    
    public Date getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Date enrolledAt) { this.enrolledAt = enrolledAt; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
