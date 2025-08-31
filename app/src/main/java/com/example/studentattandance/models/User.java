package com.example.studentattandance.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("firstName")
    private String firstName;
    
    @SerializedName("lastName")
    private String lastName;
    
    @SerializedName("role")
    private UserRole role;
    
    @SerializedName("profileImage")
    private String profileImage;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("studentId")
    private String studentId;
    
    @SerializedName("department")
    private String department;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("lastLogin")
    private String lastLogin;
    
    @SerializedName("isActive")
    private boolean isActive;

    public enum UserRole {
        STUDENT("Student"),
        TEACHER("Teacher"),
        ADMIN("Administrator");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
        
        public static UserRole fromString(String roleString) {
            if (roleString == null) return STUDENT;
            
            switch (roleString.toUpperCase()) {
                case "ADMIN":
                case "ADMINISTRATOR":
                    return ADMIN;
                case "TEACHER":
                    return TEACHER;
                case "STUDENT":
                    return STUDENT;
                default:
                    return STUDENT;
            }
        }
    }

    // Constructors
    public User() {}

    public User(String id, String username, String email, String firstName, String lastName, UserRole role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isStudent() {
        return role == UserRole.STUDENT;
    }

    public boolean isTeacher() {
        return role == UserRole.TEACHER;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    // Additional methods for compatibility
    public String getPhone() {
        return phoneNumber;
    }

    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    public String getAddress() {
        // For now, return a placeholder since we don't have address field
        return "Address not available";
    }

    public void setAddress(String address) {
        // For now, do nothing since we don't have address field
    }
}
