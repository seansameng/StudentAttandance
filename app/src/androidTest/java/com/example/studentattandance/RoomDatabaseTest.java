package com.example.studentattandance;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.studentattandance.database.AppDatabase;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.AttendanceEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RoomDatabaseTest {
    
    private AppDatabase database;
    
    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }
    
    @After
    public void closeDb() {
        database.close();
    }
    
    @Test
    public void testDatabaseCreation() {
        assertNotNull(database);
        assertNotNull(database.userDao());
        assertNotNull(database.classDao());
        assertNotNull(database.attendanceDao());
    }
    
    @Test
    public void testEntityCreation() {
        // Test UserEntity
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setUsername("testuser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole("STUDENT");
        user.setCreatedAt(new Date());
        
        assertEquals("1", user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("STUDENT", user.getRole());
        
        // Test ClassEntity
        ClassEntity classEntity = new ClassEntity();
        classEntity.setId("1");
        classEntity.setClassName("Math 101");
        classEntity.setSubject("Mathematics");
        classEntity.setTeacherId("teacher1");
        classEntity.setSchedule("Monday 9:00 AM");
        classEntity.setRoom("Room 101");
        
        assertEquals("1", classEntity.getId());
        assertEquals("Math 101", classEntity.getClassName());
        assertEquals("Mathematics", classEntity.getSubject());
        assertEquals("teacher1", classEntity.getTeacherId());
        
        // Test AttendanceEntity
        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setId("1");
        attendance.setStudentId("student1");
        attendance.setClassId("class1");
        attendance.setDate("2024-01-15");
        attendance.setStatus("PRESENT");
        
        assertEquals("1", attendance.getId());
        assertEquals("student1", attendance.getStudentId());
        assertEquals("class1", attendance.getClassId());
        assertEquals("2024-01-15", attendance.getDate());
        assertEquals("PRESENT", attendance.getStatus());
    }
}
