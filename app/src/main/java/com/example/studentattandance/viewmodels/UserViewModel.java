package com.example.studentattandance.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studentattandance.database.AppDatabase;
import com.example.studentattandance.database.dao.UserDao;
import com.example.studentattandance.database.entities.UserEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserViewModel extends AndroidViewModel {

    private final UserDao userDao;
    private final ExecutorService executorService;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(Application application) {
        super(application);
        try {
            AppDatabase database = AppDatabase.getInstance(application);
            if (database == null) {
                throw new RuntimeException("Failed to get AppDatabase instance");
            }
            userDao = database.userDao();
            if (userDao == null) {
                throw new RuntimeException("Failed to get UserDao from database");
            }
            executorService = Executors.newFixedThreadPool(4);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserViewModel: " + e.getMessage(), e);
        }
    }

    // LiveData for user operations
    public LiveData<UserEntity> getUserById(String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("User ID cannot be null or empty");
            }
            return userDao.getUserById(userId);
        } catch (Exception e) {
            errorMessage.postValue("Error getting user by ID: " + e.getMessage());
            return null;
        }
    }

    public LiveData<UserEntity> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public LiveData<List<UserEntity>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public LiveData<List<UserEntity>> getUsersByRole(String role) {
        return userDao.getUsersByRole(role);
    }

    public LiveData<List<UserEntity>> getAllStudents() {
        return userDao.getAllStudents();
    }

    public LiveData<List<UserEntity>> getAllTeachers() {
        return userDao.getAllTeachers();
    }

    public LiveData<List<UserEntity>> getAllAdmins() {
        return userDao.getAllAdmins();
    }

    public LiveData<List<UserEntity>> searchUsers(String searchQuery) {
        return userDao.searchUsers(searchQuery);
    }

    public LiveData<Integer> getUserCountByRole(String role) {
        return userDao.getUserCountByRole(role);
    }

    // Insert user
    public void insertUser(UserEntity user) {
        executorService.execute(() -> {
            try {
                isLoading.postValue(true);
                userDao.insertUser(user);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Error inserting user: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Update user
    public void updateUser(UserEntity user) {
        executorService.execute(() -> {
            try {
                isLoading.postValue(true);
                userDao.updateUser(user);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Error updating user: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Delete user
    public void deleteUser(UserEntity user) {
        executorService.execute(() -> {
            try {
                isLoading.postValue(true);
                userDao.deleteUser(user);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Error deleting user: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Delete all users
    public void deleteAllUsers() {
        executorService.execute(() -> {
            try {
                isLoading.postValue(true);
                userDao.deleteAllUsers();
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Error deleting all users: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Check if username exists
    public LiveData<Boolean> isUsernameExists(String username) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                UserEntity user = userDao.getUserByUsernameSync(username);
                result.postValue(user != null);
            } catch (Exception e) {
                result.postValue(false);
                errorMessage.postValue("Error checking username: " + e.getMessage());
            }
        });
        return result;
    }

    // Check if email exists
    public LiveData<Boolean> isEmailExists(String email) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                // Since getUserByEmail returns LiveData, we need to handle this differently
                // For now, we'll use a simple approach by checking if the email exists
                result.postValue(false); // Placeholder - implement proper email checking
            } catch (Exception e) {
                result.postValue(false);
                errorMessage.postValue("Error checking email: " + e.getMessage());
            }
        });
        return result;
    }

    // Get user by email
    public LiveData<UserEntity> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    // Get users by IDs
    public List<UserEntity> getUsersByIdsSync(List<String> userIds) {
        return userDao.getUsersByIdsSync(userIds);
    }

    // Get user count by role (synchronous)
    public int getUserCountByRoleSync(String role) {
        try {
            return userDao.getUsersByRoleSync(role).size();
        } catch (Exception e) {
            errorMessage.postValue("Error getting user count: " + e.getMessage());
            return 0;
        }
    }

    // Get all users sync
    public List<UserEntity> getAllUsersSync() {
        try {
            return userDao.getAllUsersSync();
        } catch (Exception e) {
            errorMessage.postValue("Error getting all users: " + e.getMessage());
            return null;
        }
    }

    // Get all students sync
    public List<UserEntity> getAllStudentsSync() {
        try {
            return userDao.getAllStudentsSync();
        } catch (Exception e) {
            errorMessage.postValue("Error getting students: " + e.getMessage());
            return null;
        }
    }

    // Get all teachers sync
    public List<UserEntity> getAllTeachersSync() {
        try {
            return userDao.getAllTeachersSync();
        } catch (Exception e) {
            errorMessage.postValue("Error getting teachers: " + e.getMessage());
            return null;
        }
    }

    // Get all admins sync
    public List<UserEntity> getAllAdminsSync() {
        try {
            return userDao.getAllAdminsSync();
        } catch (Exception e) {
            errorMessage.postValue("Error getting admins: " + e.getMessage());
            return null;
        }
    }

    // Get user by ID sync
    public UserEntity getUserByIdSync(String userId) {
        try {
            return userDao.getUserByIdSync(userId);
        } catch (Exception e) {
            errorMessage.postValue("Error getting user: " + e.getMessage());
            return null;
        }
    }

    // Get user by username sync
    public UserEntity getUserByUsernameSync(String username) {
        try {
            return userDao.getUserByUsernameSync(username);
        } catch (Exception e) {
            errorMessage.postValue("Error getting user by username: " + e.getMessage());
            return null;
        }
    }

    // Get loading state
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Get error message
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Clear error message
    public void clearErrorMessage() {
        errorMessage.postValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
