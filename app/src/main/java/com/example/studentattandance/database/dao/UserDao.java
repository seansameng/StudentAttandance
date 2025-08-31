package com.example.studentattandance.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentattandance.database.entities.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);
    
    @Update
    void updateUser(UserEntity user);
    
    @Delete
    void deleteUser(UserEntity user);
    
    @Query("SELECT * FROM users WHERE id = :userId")
    LiveData<UserEntity> getUserById(String userId);
    
    @Query("SELECT * FROM users WHERE id = :userId")
    UserEntity getUserByIdSync(String userId);
    
    @Query("SELECT * FROM users WHERE username = :username")
    LiveData<UserEntity> getUserByUsername(String username);
    
    @Query("SELECT * FROM users WHERE username = :username")
    UserEntity getUserByUsernameSync(String username);
    
    @Query("SELECT * FROM users WHERE username = :username AND id != :excludeId")
    LiveData<UserEntity> getUserByUsernameExcludingId(String username, String excludeId);
    
    @Query("SELECT * FROM users ORDER BY firstName")
    LiveData<List<UserEntity>> getAllUsers();
    
    @Query("SELECT * FROM users ORDER BY firstName")
    List<UserEntity> getAllUsersSync();
    
    @Query("SELECT * FROM users WHERE role = :role")
    LiveData<List<UserEntity>> getUsersByRole(String role);
    
    @Query("SELECT * FROM users WHERE role = :role")
    List<UserEntity> getUsersByRoleSync(String role);
    
    @Query("DELETE FROM users")
    void deleteAllUsers();
    
    // Additional useful queries
    @Query("SELECT * FROM users WHERE firstName LIKE '%' || :searchQuery || '%' OR lastName LIKE '%' || :searchQuery || '%' OR username LIKE '%' || :searchQuery || '%'")
    LiveData<List<UserEntity>> searchUsers(String searchQuery);
    
    @Query("SELECT * FROM users WHERE email = :email")
    LiveData<UserEntity> getUserByEmail(String email);
    
    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    LiveData<Integer> getUserCountByRole(String role);
    
    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY firstName, lastName")
    LiveData<List<UserEntity>> getAllStudents();
    
    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY firstName, lastName")
    List<UserEntity> getAllStudentsSync();
    
    @Query("SELECT * FROM users WHERE role = 'TEACHER' ORDER BY firstName, lastName")
    LiveData<List<UserEntity>> getAllTeachers();
    
    @Query("SELECT * FROM users WHERE role = 'TEACHER' ORDER BY firstName, lastName")
    List<UserEntity> getAllTeachersSync();
    
    @Query("SELECT * FROM users WHERE role = 'ADMIN' ORDER BY firstName, lastName")
    LiveData<List<UserEntity>> getAllAdmins();
    
    @Query("SELECT * FROM users WHERE role = 'ADMIN' ORDER BY firstName, lastName")
    List<UserEntity> getAllAdminsSync();
    
    @Query("SELECT * FROM users WHERE id IN (:userIds) ORDER BY firstName, lastName")
    List<UserEntity> getUsersByIdsSync(List<String> userIds);
    
    // Authentication query
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    UserEntity authenticateUser(String username, String password);
}
