package com.example.studentattandance.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

import com.example.studentattandance.database.converters.DateConverter;
import java.util.Date;

@Entity(
    tableName = "users",
    indices = {
        @Index(value = "username", unique = true),
        @Index(value = "email"),
        @Index(value = "role")
    }
)
public class UserEntity {
    @PrimaryKey
    @NonNull
    private String id;
    
    @NonNull
    private String username;
    private String email;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String role;
    
    private String password;
    
    @TypeConverters(DateConverter.class)
    private Date createdAt;
    
    private Date lastLogin;
    
    private String phone;
    private String location;
    
    // Constructor
    public UserEntity() {}
    
    @Ignore
    public UserEntity(@NonNull String id, @NonNull String username, String email, @NonNull String firstName, @NonNull String lastName, @NonNull String role, Date createdAt, Date lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(@NonNull String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(@NonNull String lastName) { this.lastName = lastName; }
    
    public String getRole() { return role; }
    public void setRole(@NonNull String role) { this.role = role; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
