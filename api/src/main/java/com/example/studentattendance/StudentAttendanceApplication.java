package com.example.studentattendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudentAttendanceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StudentAttendanceApplication.class, args);
        System.out.println("🚀 Student Attendance API is running on port 8080!");
        System.out.println("📱 Your Android app can now connect to: http://localhost:8080/api");
        System.out.println("🏥 Health check: http://localhost:8080/api/actuator/health");
    }
}


























