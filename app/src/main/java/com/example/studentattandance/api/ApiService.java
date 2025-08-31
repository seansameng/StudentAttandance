package com.example.studentattandance.api;

import com.example.studentattandance.models.Attendance;
import com.example.studentattandance.models.Class;
import com.example.studentattandance.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Authentication
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);
    
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest registerRequest);
    
    @POST("auth/refresh")
    Call<AuthResponse> refreshToken(@Header("Authorization") String refreshToken);
    
    @POST("auth/logout")
    Call<Void> logout(@Header("Authorization") String token);
    
    // User Management
    @GET("users/profile")
    Call<User> getProfile(@Header("Authorization") String token);
    
    @PUT("users/profile")
    Call<User> updateProfile(@Header("Authorization") String token, @Body User user);
    
    @PUT("users/password")
    Call<Void> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);
    
    @GET("users")
    Call<List<User>> getUsers(@Header("Authorization") String token, @Query("role") String role);
    
    @GET("users/{id}")
    Call<User> getUser(@Header("Authorization") String token, @Path("id") String userId);
    
    @POST("users")
    Call<User> createUser(@Header("Authorization") String token, @Body User user);
    
    @PUT("users/{id}")
    Call<User> updateUser(@Header("Authorization") String token, @Path("id") String userId, @Body User user);
    
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String token, @Path("id") String userId);
    
    // Class Management
    @GET("classes")
    Call<List<Class>> getClasses(@Header("Authorization") String token, @Query("teacherId") String teacherId);
    
    @GET("classes/{id}")
    Call<Class> getClass(@Header("Authorization") String token, @Path("id") String classId);
    
    @POST("classes")
    Call<Class> createClass(@Header("Authorization") String token, @Body Class classObj);
    
    @PUT("classes/{id}")
    Call<Class> updateClass(@Header("Authorization") String token, @Path("id") String classId, @Body Class classObj);
    
    @DELETE("classes/{id}")
    Call<Void> deleteClass(@Header("Authorization") String token, @Path("id") String classId);
    
    @POST("classes/{id}/enroll")
    Call<Void> enrollStudent(@Header("Authorization") String token, @Path("id") String classId, @Body EnrollRequest request);
    
    @DELETE("classes/{id}/enroll/{studentId}")
    Call<Void> unenrollStudent(@Header("Authorization") String token, @Path("id") String classId, @Path("studentId") String studentId);
    
    // Attendance Management
    @GET("attendance")
    Call<List<Attendance>> getAttendance(@Header("Authorization") String token, 
                                       @Query("classId") String classId, 
                                       @Query("date") String date,
                                       @Query("studentId") String studentId);
    
    @GET("attendance/{id}")
    Call<Attendance> getAttendanceRecord(@Header("Authorization") String token, @Path("id") String attendanceId);
    
    @POST("attendance")
    Call<Attendance> createAttendance(@Header("Authorization") String token, @Body Attendance attendance);
    
    @PUT("attendance/{id}")
    Call<Attendance> updateAttendance(@Header("Authorization") String token, @Path("id") String attendanceId, @Body Attendance attendance);
    
    @DELETE("attendance/{id}")
    Call<Void> deleteAttendance(@Header("Authorization") String token, @Path("id") String attendanceId);
    
    @POST("attendance/bulk")
    Call<List<Attendance>> createBulkAttendance(@Header("Authorization") String token, @Body BulkAttendanceRequest request);
    
    // Reports
    @GET("reports/attendance")
    Call<AttendanceReport> getAttendanceReport(@Header("Authorization") String token, 
                                             @Query("classId") String classId,
                                             @Query("startDate") String startDate,
                                             @Query("endDate") String endDate,
                                             @Query("studentId") String studentId);
    
    @GET("reports/student/{studentId}")
    Call<StudentReport> getStudentReport(@Header("Authorization") String token, @Path("studentId") String studentId);
    
    @GET("reports/class/{classId}")
    Call<ClassReport> getClassReport(@Header("Authorization") String token, @Path("classId") String classId);
    
    // Dashboard
    @GET("dashboard/overview")
    Call<DashboardData> getDashboardOverview(@Header("Authorization") String token);
    
    @GET("dashboard/upcoming")
    Call<List<Class>> getUpcomingClasses(@Header("Authorization") String token);
    
    @GET("dashboard/notifications")
    Call<List<Notification>> getNotifications(@Header("Authorization") String token);

    Call<UpdateProfileResponse> updateProfile(String token, UpdateProfileRequest updateRequest);

    // Request/Response Models
    class AuthResponse {
        public String accessToken;
        public String refreshToken;
        public String tokenType;
        public long expiresIn;
        public User user;
    }
    
    class LoginRequest {
        public String username;
        public String password;
    }
    
    class RegisterRequest {
        public String username;
        public String email;
        public String password;
        public String firstName;
        public String lastName;
        public String role;
    }
    
    class ChangePasswordRequest {
        public String currentPassword;
        public String newPassword;
    }
    
    class EnrollRequest {
        public String studentId;
    }
    
    class BulkAttendanceRequest {
        public String classId;
        public String date;
        public List<Attendance> records;
    }
    
    class AttendanceReport {
        public String classId;
        public String className;
        public String startDate;
        public String endDate;
        public int totalSessions;
        public int totalStudents;
        public List<AttendanceSummary> summaries;
    }
    
    class AttendanceSummary {
        public String studentId;
        public String studentName;
        public int present;
        public int absent;
        public int late;
        public int excused;
        public double attendancePercentage;
    }
    
    class StudentReport {
        public String studentId;
        public String studentName;
        public String semester;
        public String academicYear;
        public List<ClassAttendance> classAttendance;
        public double overallAttendance;
    }
    
    class ClassAttendance {
        public String classId;
        public String className;
        public String subject;
        public int totalSessions;
        public int present;
        public int absent;
        public int late;
        public int excused;
        public double attendancePercentage;
    }
    
    class ClassReport {
        public String classId;
        public String className;
        public String subject;
        public String teacherName;
        public String semester;
        public String academicYear;
        public int totalStudents;
        public int totalSessions;
        public List<StudentAttendance> studentAttendance;
        public double averageAttendance;
    }
    
    class StudentAttendance {
        public String studentId;
        public String studentName;
        public int present;
        public int absent;
        public int late;
        public int excused;
        public double attendancePercentage;
    }
    
    class DashboardData {
        public int totalClasses;
        public int totalStudents;
        public int todayAttendance;
        public double averageAttendance;
        public List<RecentActivity> recentActivities;
    }
    
    class RecentActivity {
        public String type;
        public String description;
        public String timestamp;
        public String userId;
        public String userName;
    }
    
    class Notification {
        public String id;
        public String title;
        public String message;
        public String type;
        public String timestamp;
        public boolean isRead;
        public String targetId;
    }
    
    // Additional methods needed
    @GET("classes/{id}/details")
    Call<Class> getClassDetails(@Header("Authorization") String token, @Path("id") String classId);
    
    @GET("classes/{id}/students")
    Call<List<Student>> getClassStudents(@Header("Authorization") String token, @Path("id") String classId);
    
    @GET("classes/{id}/attendance")
    Call<List<Attendance>> getClassAttendance(@Header("Authorization") String token, 
                                            @Path("id") String classId,
                                            @Query("startDate") String startDate,
                                            @Query("endDate") String endDate);
    
    @POST("attendance/mark")
    Call<MarkAttendanceResponse> markAttendance(@Header("Authorization") String token, @Body MarkAttendanceRequest request);
    
    class MarkAttendanceRequest {
        public String classId;
        public String date;
        public List<Attendance> attendanceRecords;
    }
    
    class MarkAttendanceResponse {
        public int successCount;
        public int failureCount;
        public List<String> errors;
    }
    
    class UpdateProfileRequest {
        public String firstName;
        public String lastName;
        public String email;
        public String phoneNumber;
        public String department;
    }
    
    class UpdateProfileResponse {
        public User user;
        public String message;
    }
    
    class Student {
        public String id;
        public String fullName;
        public String email;
        public String studentId;
    }
}
