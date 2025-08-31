package com.example.studentattendance.controllers;

import com.example.studentattendance.dto.UserDto;
import com.example.studentattendance.models.User;
import com.example.studentattendance.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto.UserResponse>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            List<UserDto.UserResponse> userResponses = users.stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDto.UserResponse> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user != null) {
                return ResponseEntity.ok(convertToUserResponse(user));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get users by role
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<UserDto.UserResponse>> getUsersByRole(@PathVariable String role) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            List<User> users = userService.findByRole(userRole);
            List<UserDto.UserResponse> userResponses = users.stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Search users
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<UserDto.UserResponse>> searchUsers(@RequestParam String query) {
        try {
            List<User> users = userService.searchUsers(query);
            List<UserDto.UserResponse> userResponses = users.stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Create new user (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserResponse> createUser(@Valid @RequestBody UserDto.CreateUserRequest request) {
        try {
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().build();
            }
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().build();
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword()); // Will be encoded in service
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setStudentId(request.getStudentId());
            user.setDepartment(request.getDepartment());

            // Set role with proper enum conversion and validation
            User.UserRole userRole = User.UserRole.STUDENT; // default
            if (request.getRole() != null) {
                try {
                    userRole = User.UserRole.valueOf(request.getRole().toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Invalid role provided, use default STUDENT role
                    userRole = User.UserRole.STUDENT;
                }
            }
            user.setRole(userRole);
            user.setActive(true);

            User savedUser = userService.save(user);
            return ResponseEntity.ok(convertToUserResponse(savedUser));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Update user
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDto.UserResponse> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserDto.UpdateUserRequest request) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            if (request.getFirstName() != null)
                user.setFirstName(request.getFirstName());
            if (request.getLastName() != null)
                user.setLastName(request.getLastName());
            if (request.getPhoneNumber() != null)
                user.setPhoneNumber(request.getPhoneNumber());
            if (request.getDepartment() != null)
                user.setDepartment(request.getDepartment());
            if (request.getProfileImage() != null)
                user.setProfileImage(request.getProfileImage());

            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(convertToUserResponse(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Change password
    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> changePassword(@PathVariable Long id,
            @Valid @RequestBody UserDto.ChangePasswordRequest request) {
        try {
            boolean success = userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
            if (success) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body("Current password is incorrect");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Deactivate user (Admin only)
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            user.setActive(false);
            userService.updateUser(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Activate user (Admin only)
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            user.setActive(true);
            userService.updateUser(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete user (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get user statistics
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserStats> getUserStats() {
        try {
            UserDto.UserStats stats = new UserDto.UserStats();
            stats.setTotalUsers(userService.countAll());
            stats.setActiveUsers(userService.countActiveUsers());
            stats.setStudentsCount(userService.countByRole(User.UserRole.STUDENT));
            stats.setTeachersCount(userService.countByRole(User.UserRole.TEACHER));
            stats.setAdminsCount(userService.countByRole(User.UserRole.ADMIN));
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private UserDto.UserResponse convertToUserResponse(User user) {
        UserDto.UserResponse response = new UserDto.UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setStudentId(user.getStudentId());
        response.setDepartment(user.getDepartment());
        response.setRole(user.getRole());
        response.setProfileImage(user.getProfileImage());
        response.setActive(user.isActive());
        response.setLastLogin(user.getLastLogin());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
