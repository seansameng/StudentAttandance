package com.example.studentattendance.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Arrays;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.studentattendance.dto.AuthDto;
import com.example.studentattendance.models.User;
import com.example.studentattendance.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User createUser(AuthDto.RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Set default role if not specified
        if (request.getRole() != null) {
            try {
                user.setRole(User.UserRole.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(User.UserRole.STUDENT); // Default role
            }
        } else {
            user.setRole(User.UserRole.STUDENT); // Default role
        }

        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void updateLastLogin(Long userId) {
        User user = findById(userId);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);
        if (user != null && passwordEncoder.matches(currentPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User save(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());

        // Encode password if it's not already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public long countAll() {
        return userRepository.count();
    }

    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }

    public long countByRole(User.UserRole role) {
        return userRepository.countByRole(role);
    }

    public List<Integer> getRecentLogins(int hours) {
        // Implementation would get recent login counts
        // For now, return mock data
        return Arrays.asList(5, 8, 12, 6, 10); // Mock data
    }

    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }
}
