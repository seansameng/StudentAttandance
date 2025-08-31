package com.example.studentattandance.utils;

import android.content.Context;
import android.util.Log;

import com.example.studentattandance.database.entities.ClassEnrollmentEntity;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.entities.AttendanceEntity;
import com.example.studentattandance.repository.DataRepository;

import java.util.Date;
import java.util.UUID;

public class DatabaseSeeder {
    
    private static final String TAG = "DatabaseSeeder";
    private DataRepository repository;
    
    public DatabaseSeeder(Context context) {
        repository = new DataRepository(context);
    }
    
    public void seedAllData() {
        Log.d(TAG, "Starting database seeding...");
        
        // Seed users first
        seedUsers();
        
        // Seed classes
        seedClasses();
        
        // Seed attendance records
        seedAttendance();
        
        // Seed class enrollments
        seedEnrollments();
        
        Log.d(TAG, "Database seeding completed!");
    }
    
    private void createTestUsers() {
        Log.d(TAG, "Creating test users (admin, teacher, student)...");
        
        // Create admin user
        UserEntity adminUser = new UserEntity();
        adminUser.setId(UUID.randomUUID().toString());
        adminUser.setUsername("admin");
        adminUser.setPassword("123456");
        adminUser.setEmail("admin@test.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole("ADMIN");
        adminUser.setCreatedAt(new Date());
        adminUser.setLastLogin(new Date());
        repository.insertUser(adminUser);
        Log.d(TAG, "Created admin user with password: 123456");
        
        // Create teacher user
        UserEntity teacherUser = new UserEntity();
        teacherUser.setId(UUID.randomUUID().toString());
        teacherUser.setUsername("teacher");
        teacherUser.setPassword("123456");
        teacherUser.setEmail("teacher@test.com");
        teacherUser.setFirstName("Teacher");
        teacherUser.setLastName("User");
        teacherUser.setRole("TEACHER");
        teacherUser.setCreatedAt(new Date());
        teacherUser.setLastLogin(new Date());
        repository.insertUser(teacherUser);
        Log.d(TAG, "Created teacher user with password: 123456");
        
        // Create student user
        UserEntity studentUser = new UserEntity();
        studentUser.setId(UUID.randomUUID().toString());
        studentUser.setUsername("student");
        studentUser.setPassword("123456");
        studentUser.setEmail("student@test.com");
        studentUser.setFirstName("Student");
        studentUser.setLastName("User");
        studentUser.setRole("STUDENT");
        studentUser.setCreatedAt(new Date());
        studentUser.setLastLogin(new Date());
        repository.insertUser(studentUser);
        Log.d(TAG, "Created student user with password: 123456");
    }
    
    private void seedUsers() {
        Log.d(TAG, "Seeding users...");
        
        // Create specific test users first
        createTestUsers();
        
        // Create 10 additional users with different roles
        String[] firstNames = {"John", "Sarah", "Michael", "Emily", "David", "Lisa", "Robert", "Jennifer", "William", "Amanda"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};
        String[] roles = {"STUDENT", "STUDENT", "TEACHER", "STUDENT", "ADMIN", "STUDENT", "TEACHER", "STUDENT", "STUDENT", "TEACHER"};
        String[] usernames = {"john", "sarah", "michael", "emily", "david", "lisa", "robert", "jennifer", "william", "amanda"};
        String[] emails = {"john@school.com", "sarah@school.com", "michael@school.com", "emily@school.com", "david@school.com", 
                          "lisa@school.com", "robert@school.com", "jennifer@school.com", "william@school.com", "amanda@school.com"};
        
        for (int i = 0; i < 10; i++) {
            UserEntity user = new UserEntity();
            user.setId(UUID.randomUUID().toString());
            user.setUsername(usernames[i]);
            user.setEmail(emails[i]);
            user.setFirstName(firstNames[i]);
            user.setLastName(lastNames[i]);
            user.setRole(roles[i]);
            user.setPassword("123456"); // Set default password for all users
            user.setCreatedAt(new Date());
            user.setLastLogin(new Date());
            
            repository.insertUser(user);
            Log.d(TAG, "Inserted user: " + user.getUsername() + " with password: 123456");
        }
    }
    
    private void seedClasses() {
        Log.d(TAG, "Seeding classes...");
        
        // Create 10 classes
        String[] classNames = {"Mathematics 101", "English Literature", "Physics Fundamentals", "History of Art", "Computer Science", 
                              "Chemistry Lab", "World Geography", "Music Theory", "Physical Education", "Business Ethics"};
        String[] subjects = {"Mathematics", "English", "Physics", "Art History", "Computer Science", 
                           "Chemistry", "Geography", "Music", "Physical Education", "Business"};
        String[] schedules = {"Monday 9:00 AM", "Tuesday 10:30 AM", "Wednesday 2:00 PM", "Thursday 11:00 AM", "Friday 1:30 PM",
                            "Monday 3:00 PM", "Tuesday 1:00 PM", "Wednesday 9:30 AM", "Thursday 2:30 PM", "Friday 10:00 AM"};
        String[] rooms = {"Room 101", "Room 202", "Lab A", "Art Studio", "Computer Lab", 
                         "Chemistry Lab", "Room 303", "Music Room", "Gym", "Conference Room"};
        
        for (int i = 0; i < 10; i++) {
            ClassEntity classEntity = new ClassEntity();
            classEntity.setId(UUID.randomUUID().toString());
            classEntity.setClassName(classNames[i]);
            classEntity.setSubject(subjects[i]);
            classEntity.setTeacherId("teacher-" + (i + 1)); // We'll link this to actual teacher IDs later
            classEntity.setSchedule(schedules[i]);
            classEntity.setRoom(rooms[i]);
            classEntity.setCreatedAt(new Date());
            
            repository.insertClass(classEntity);
            Log.d(TAG, "Inserted class: " + classEntity.getClassName());
        }
    }
    
    private void seedAttendance() {
        Log.d(TAG, "Seeding attendance records...");
        
        // Create 10 attendance records
        String[] statuses = {"PRESENT", "ABSENT", "LATE", "PRESENT", "PRESENT", "ABSENT", "LATE", "PRESENT", "PRESENT", "ABSENT"};
        String[] dates = {"2024-01-15", "2024-01-16", "2024-01-17", "2024-01-18", "2024-01-19", 
                         "2024-01-22", "2024-01-23", "2024-01-24", "2024-01-25", "2024-01-26"};
        
        for (int i = 0; i < 10; i++) {
            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setId(UUID.randomUUID().toString());
            attendance.setStudentId("student-" + (i + 1)); // We'll link this to actual student IDs later
            attendance.setClassId("class-" + (i + 1)); // We'll link this to actual class IDs later
            attendance.setDate(dates[i]);
            attendance.setStatus(statuses[i]);
            attendance.setCreatedAt(new Date());
            
            repository.insertAttendance(attendance);
            Log.d(TAG, "Inserted attendance record: " + attendance.getStatus() + " on " + attendance.getDate());
        }
    }
    
    private void seedEnrollments() {
        Log.d(TAG, "Seeding class enrollments...");
        
        // For now, we'll create some sample enrollments
        // In a real app, you'd get actual user and class IDs from the database
        
        // Enroll some students in the first few classes
        for (int i = 0; i < 3; i++) { // First 3 classes
            for (int j = 0; j < 3; j++) { // First 3 students
                try {
                    // Note: This is a simplified approach. In production, you'd get actual IDs
                    String classId = "class-" + (i + 1);
                    String studentId = "student-" + (i + 1);
                    
                    // Enroll student in class
                    repository.enrollStudentInClass(studentId, classId);
                    Log.d(TAG, "Enrolled student " + studentId + " in class " + classId);
                } catch (Exception e) {
                    Log.e(TAG, "Error enrolling student in class", e);
                }
            }
        }
        
        Log.d(TAG, "Class enrollments seeded!");
    }
    
    public void clearAllData() {
        Log.d(TAG, "Clearing all data...");
        repository.deleteAllUsers();
        repository.deleteAllClasses();
        repository.deleteAllAttendances();
        Log.d(TAG, "All data cleared!");
    }
}
