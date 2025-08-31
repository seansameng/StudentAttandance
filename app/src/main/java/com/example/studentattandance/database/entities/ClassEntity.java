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
    tableName = "classes",
    foreignKeys = {
        @ForeignKey(
            entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "teacherId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = "teacherId"),
        @Index(value = "subject")
    }
)
public class ClassEntity {
    @PrimaryKey
    @NonNull
    private String id;
    
    @NonNull
    private String className;
    @NonNull
    private String subject;
    @NonNull
    private String teacherId;
    
    private String schedule;
    private String room;
    
    @TypeConverters(DateConverter.class)
    private Date createdAt;
    
    // Constructor
    public ClassEntity() {}
    
    @Ignore
    public ClassEntity(@NonNull String id, @NonNull String className, @NonNull String subject, @NonNull String teacherId, 
                      String schedule, String room, Date createdAt) {
        this.id = id;
        this.className = className;
        this.subject = subject;
        this.teacherId = teacherId;
        this.schedule = schedule;
        this.room = room;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public String getClassName() { return className; }
    public void setClassName(@NonNull String className) { this.className = className; }
    
    public String getSubject() { return subject; }
    public void setSubject(@NonNull String subject) { this.subject = subject; }
    
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(@NonNull String teacherId) { this.teacherId = teacherId; }
    
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    // Additional methods for adapter compatibility
    public String getTeacherName() {
        // This would typically be loaded from a join with UserEntity
        // For now, return a placeholder
        return "Teacher " + teacherId;
    }

    public int getMaxStudents() {
        // Default max students
        return 30;
    }

    public int getEnrolledStudentsCount() {
        // This would typically be loaded from ClassEnrollmentEntity
        // For now, return a placeholder
        return 0;
    }
}
