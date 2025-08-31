package com.example.studentattendance;

import com.example.studentattendance.models.User;
import com.example.studentattendance.models.Class;
import com.example.studentattendance.repositories.UserRepository;
import com.example.studentattendance.repositories.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
@EnableWebSecurity
public class StudentAttendanceApplication {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    public static void main(String[] args) {
        SpringApplication.run(StudentAttendanceApplication.class, args);
        System.out.println("🚀 Student Attendance API is running on port 8080!");
        System.out.println("📱 Your Android app can now connect to: http://localhost:8080/api");
        System.out.println("🗄️  H2 Database Console: http://localhost:8080/h2-console");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    // Health check
    @GetMapping("/api/health")
    public String health() {
        return "Student Attendance API is running!";
    }

    // Test endpoint
    @GetMapping("/api/test")
    public String test() {
        return "API connection successful!";
    }

    // USER CRUD OPERATIONS
    @PostMapping("/api/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @PutMapping("/api/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setRole(userDetails.getRole());
            return userRepository.save(existingUser);
        }
        return null;
    }

    @DeleteMapping("/api/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "User deleted successfully";
        }
        return "User not found";
    }

    // CLASS CRUD OPERATIONS
    @PostMapping("/api/classes")
    public Class createClass(@RequestBody Class classData) {
        return classRepository.save(classData);
    }

    @GetMapping("/api/classes")
    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    @GetMapping("/api/classes/{id}")
    public Class getClassById(@PathVariable Long id) {
        return classRepository.findById(id).orElse(null);
    }

    @PutMapping("/api/classes/{id}")
    public Class updateClass(@PathVariable Long id, @RequestBody Class classDetails) {
        Optional<Class> classData = classRepository.findById(id);
        if (classData.isPresent()) {
            Class existingClass = classData.get();
            existingClass.setClassName(classDetails.getClassName());
            existingClass.setSubject(classDetails.getSubject());
            existingClass.setSchedule(classDetails.getSchedule());
            existingClass.setTeacherId(classDetails.getTeacherId());
            return classRepository.save(existingClass);
        }
        return null;
    }

    @DeleteMapping("/api/classes/{id}")
    public String deleteClass(@PathVariable Long id) {
        if (classRepository.existsById(id)) {
            classRepository.deleteById(id);
            return "Class deleted successfully";
        }
        return "Class not found";
    }
}
