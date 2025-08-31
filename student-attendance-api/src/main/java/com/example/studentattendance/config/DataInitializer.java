package com.example.studentattendance.config;

import com.example.studentattendance.models.Class;
import com.example.studentattendance.models.User;
import com.example.studentattendance.repositories.ClassRepository;
import com.example.studentattendance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no users exist
        if (userRepository.count() == 0) {
            initializeSampleData();
        }
    }
    
    private void initializeSampleData() {
        // Create Admin User
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@school.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setRole(User.UserRole.ADMIN);
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(admin);
        
        // Create Teacher User
        User teacher = new User();
        teacher.setUsername("teacher");
        teacher.setEmail("teacher@school.com");
        teacher.setPassword(passwordEncoder.encode("teacher123"));
        teacher.setFirstName("John");
        teacher.setLastName("Smith");
        teacher.setRole(User.UserRole.TEACHER);
        teacher.setActive(true);
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        userRepository.save(teacher);
        
        // Create Student User
        User student = new User();
        student.setUsername("student");
        student.setEmail("student@school.com");
        student.setPassword(passwordEncoder.encode("student123"));
        student.setFirstName("Jane");
        student.setLastName("Doe");
        student.setRole(User.UserRole.STUDENT);
        student.setStudentId("STU001");
        student.setDepartment("Computer Science");
        student.setActive(true);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        userRepository.save(student);
        
        // Create Sample Classes
        Class mathClass = new Class();
        mathClass.setClassName("Mathematics 101");
        mathClass.setSubject("Advanced Calculus");
        mathClass.setTeacher(teacher);
        mathClass.setSchedule("Mon 9:00 AM - 10:30 AM");
        mathClass.setRoom("Room 101");
        mathClass.setMaxStudents(30);
        mathClass.setDescription("Advanced mathematics course covering calculus concepts");
        mathClass.setSemester("Fall 2024");
        mathClass.setAcademicYear("2024-2025");
        mathClass.setActive(true);
        mathClass.setCreatedAt(LocalDateTime.now());
        mathClass.setUpdatedAt(LocalDateTime.now());
        classRepository.save(mathClass);
        
        Class physicsClass = new Class();
        physicsClass.setClassName("Physics 201");
        physicsClass.setSubject("Quantum Mechanics");
        physicsClass.setTeacher(teacher);
        physicsClass.setSchedule("Tue 10:30 AM - 12:00 PM");
        physicsClass.setRoom("Room 202");
        physicsClass.setMaxStudents(25);
        physicsClass.setDescription("Physics fundamentals and quantum mechanics");
        physicsClass.setSemester("Fall 2024");
        physicsClass.setAcademicYear("2024-2025");
        physicsClass.setActive(true);
        physicsClass.setCreatedAt(LocalDateTime.now());
        physicsClass.setUpdatedAt(LocalDateTime.now());
        classRepository.save(physicsClass);
        
        System.out.println("✅ Sample data initialized successfully!");
        System.out.println("👤 Test Users:");
        System.out.println("   Admin: username=admin, password=admin123");
        System.out.println("   Teacher: username=teacher, password=teacher123");
        System.out.println("   Student: username=student, password=student123");
    }
}
