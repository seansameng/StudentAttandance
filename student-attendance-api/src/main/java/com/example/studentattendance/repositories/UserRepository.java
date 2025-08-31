package com.example.studentattendance.repositories;

import com.example.studentattendance.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByStudentId(String studentId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByStudentId(String studentId);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByDepartment(String department);
    
    List<User> findByIsActive(boolean isActive);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:searchTerm% OR u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm%")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") User.UserRole role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
}
