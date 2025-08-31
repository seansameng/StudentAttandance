package com.example.studentattendance.dto;

import com.example.studentattendance.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UserDto {
    
    // Create User Request
    public static class CreateUserRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        
        @NotBlank(message = "First name is required")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        private String lastName;
        
        private String phoneNumber;
        private String studentId;
        private String department;
        private String role;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    // Update User Request
    public static class UpdateUserRequest {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String department;
        private String profileImage;
        
        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getProfileImage() { return profileImage; }
        public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    }
    
    // Change Password Request
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;
        
        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        private String newPassword;
        
        // Getters and Setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    // User Response
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String studentId;
        private String department;
        private User.UserRole role;
        private String profileImage;
        private boolean isActive;
        private LocalDateTime lastLogin;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public User.UserRole getRole() { return role; }
        public void setRole(User.UserRole role) { this.role = role; }
        
        public String getProfileImage() { return profileImage; }
        public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        public LocalDateTime getLastLogin() { return lastLogin; }
        public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
    
    // User Statistics
    public static class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long studentsCount;
        private long teachersCount;
        private long adminsCount;
        
        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        
        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
        
        public long getStudentsCount() { return studentsCount; }
        public void setStudentsCount(long studentsCount) { this.studentsCount = studentsCount; }
        
        public long getTeachersCount() { return teachersCount; }
        public void setTeachersCount(long teachersCount) { this.teachersCount = teachersCount; }
        
        public long getAdminsCount() { return adminsCount; }
        public void setAdminsCount(long adminsCount) { this.adminsCount = adminsCount; }
    }
    
    // User Search Result
    public static class UserSearchResult {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private User.UserRole role;
        private String department;
        private boolean isActive;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public User.UserRole getRole() { return role; }
        public void setRole(User.UserRole role) { this.role = role; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
}
