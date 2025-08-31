package com.example.studentattendance.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DashboardDto {
    
    // Main Dashboard Data
    public static class DashboardData {
        private int totalClasses;
        private int totalStudents;
        private int todayAttendance;
        private double averageAttendance;
        private List<RecentActivity> recentActivities;
        private QuickStats quickStats;
        private AttendanceTrends attendanceTrends;
        
        // Getters and Setters
        public int getTotalClasses() { return totalClasses; }
        public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getTodayAttendance() { return todayAttendance; }
        public void setTodayAttendance(int todayAttendance) { this.todayAttendance = todayAttendance; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
        
        public List<RecentActivity> getRecentActivities() { return recentActivities; }
        public void setRecentActivities(List<RecentActivity> recentActivities) { this.recentActivities = recentActivities; }
        
        public QuickStats getQuickStats() { return quickStats; }
        public void setQuickStats(QuickStats quickStats) { this.quickStats = quickStats; }
        
        public AttendanceTrends getAttendanceTrends() { return attendanceTrends; }
        public void setAttendanceTrends(AttendanceTrends attendanceTrends) { this.attendanceTrends = attendanceTrends; }
    }
    
    // Quick Statistics
    public static class QuickStats {
        private int presentToday;
        private int absentToday;
        private int lateToday;
        private int classesToday;
        
        // Getters and Setters
        public int getPresentToday() { return presentToday; }
        public void setPresentToday(int presentToday) { this.presentToday = presentToday; }
        
        public int getAbsentToday() { return absentToday; }
        public void setAbsentToday(int absentToday) { this.absentToday = absentToday; }
        
        public int getLateToday() { return lateToday; }
        public void setLateToday(int lateToday) { this.lateToday = lateToday; }
        
        public int getClassesToday() { return classesToday; }
        public void setClassesToday(int classesToday) { this.classesToday = classesToday; }
    }
    
    // Attendance Trends
    public static class AttendanceTrends {
        private List<Double> weeklyTrend;
        private List<Double> monthlyTrend;
        
        // Getters and Setters
        public List<Double> getWeeklyTrend() { return weeklyTrend; }
        public void setWeeklyTrend(List<Double> weeklyTrend) { this.weeklyTrend = weeklyTrend; }
        
        public List<Double> getMonthlyTrend() { return monthlyTrend; }
        public void setMonthlyTrend(List<Double> monthlyTrend) { this.monthlyTrend = monthlyTrend; }
    }
    
    // Recent Activity
    public static class RecentActivity {
        private String type;
        private String description;
        private String userId;
        private String userName;
        private String timestamp;
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
    
    // Teacher Dashboard Data
    public static class TeacherDashboardData {
        private List<com.example.studentattendance.models.Class> classes;
        private List<com.example.studentattendance.models.Class> todayClasses;
        private TeacherAttendanceSummary attendanceSummary;
        private List<RecentAttendanceActivity> recentActivities;
        
        // Getters and Setters
        public List<com.example.studentattendance.models.Class> getClasses() { return classes; }
        public void setClasses(List<com.example.studentattendance.models.Class> classes) { this.classes = classes; }
        
        public List<com.example.studentattendance.models.Class> getTodayClasses() { return todayClasses; }
        public void setTodayClasses(List<com.example.studentattendance.models.Class> todayClasses) { this.todayClasses = todayClasses; }
        
        public TeacherAttendanceSummary getAttendanceSummary() { return attendanceSummary; }
        public void setAttendanceSummary(TeacherAttendanceSummary attendanceSummary) { this.attendanceSummary = attendanceSummary; }
        
        public List<RecentAttendanceActivity> getRecentActivities() { return recentActivities; }
        public void setRecentActivities(List<RecentAttendanceActivity> recentActivities) { this.recentActivities = recentActivities; }
    }
    
    // Teacher Attendance Summary
    public static class TeacherAttendanceSummary {
        private int totalStudents;
        private int presentToday;
        private int absentToday;
        private double averageAttendance;
        
        // Getters and Setters
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getPresentToday() { return presentToday; }
        public void setPresentToday(int presentToday) { this.presentToday = presentToday; }
        
        public int getAbsentToday() { return absentToday; }
        public void setAbsentToday(int absentToday) { this.absentToday = absentToday; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
    }
    
    // Recent Attendance Activity
    public static class RecentAttendanceActivity {
        private Long id;
        private String studentName;
        private String className;
        private String status;
        private String date;
        private String timeIn;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getTimeIn() { return timeIn; }
        public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
    }
    
    // Student Dashboard Data
    public static class StudentDashboardData {
        private List<com.example.studentattendance.models.Class> enrolledClasses;
        private List<com.example.studentattendance.models.Class> todayClasses;
        private StudentAttendanceSummary attendanceSummary;
        private List<AttendanceHistoryItem> attendanceHistory;
        
        // Getters and Setters
        public List<com.example.studentattendance.models.Class> getEnrolledClasses() { return enrolledClasses; }
        public void setEnrolledClasses(List<com.example.studentattendance.models.Class> enrolledClasses) { this.enrolledClasses = enrolledClasses; }
        
        public List<com.example.studentattendance.models.Class> getTodayClasses() { return todayClasses; }
        public void setTodayClasses(List<com.example.studentattendance.models.Class> todayClasses) { this.todayClasses = todayClasses; }
        
        public StudentAttendanceSummary getAttendanceSummary() { return attendanceSummary; }
        public void setAttendanceSummary(StudentAttendanceSummary attendanceSummary) { this.attendanceSummary = attendanceSummary; }
        
        public List<AttendanceHistoryItem> getAttendanceHistory() { return attendanceHistory; }
        public void setAttendanceHistory(List<AttendanceHistoryItem> attendanceHistory) { this.attendanceHistory = attendanceHistory; }
    }
    
    // Student Attendance Summary
    public static class StudentAttendanceSummary {
        private int totalClasses;
        private int presentThisWeek;
        private int absentThisWeek;
        private double attendancePercentage;
        
        // Getters and Setters
        public int getTotalClasses() { return totalClasses; }
        public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
        
        public int getPresentThisWeek() { return presentThisWeek; }
        public void setPresentThisWeek(int presentThisWeek) { this.presentThisWeek = presentThisWeek; }
        
        public int getAbsentThisWeek() { return absentThisWeek; }
        public void setAbsentThisWeek(int absentThisWeek) { this.absentThisWeek = absentThisWeek; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
    
    // Attendance History Item
    public static class AttendanceHistoryItem {
        private Long id;
        private String className;
        private String status;
        private String date;
        private String timeIn;
        private String timeOut;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getTimeIn() { return timeIn; }
        public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
        
        public String getTimeOut() { return timeOut; }
        public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
    }
    
    // Admin Dashboard Data
    public static class AdminDashboardData {
        private SystemOverview systemOverview;
        private UserRoleStats userRoleStats;
        private List<SystemActivity> systemActivities;
        private PerformanceMetrics performanceMetrics;
        
        // Getters and Setters
        public SystemOverview getSystemOverview() { return systemOverview; }
        public void setSystemOverview(SystemOverview systemOverview) { this.systemOverview = systemOverview; }
        
        public UserRoleStats getUserRoleStats() { return userRoleStats; }
        public void setUserRoleStats(UserRoleStats userRoleStats) { this.userRoleStats = userRoleStats; }
        
        public List<SystemActivity> getSystemActivities() { return systemActivities; }
        public void setSystemActivities(List<SystemActivity> systemActivities) { this.systemActivities = systemActivities; }
        
        public PerformanceMetrics getPerformanceMetrics() { return performanceMetrics; }
        public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) { this.performanceMetrics = performanceMetrics; }
    }
    
    // System Overview
    public static class SystemOverview {
        private long totalUsers;
        private long activeUsers;
        private long totalClasses;
        private long activeClasses;
        private long totalAttendanceRecords;
        
        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        
        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
        
        public long getTotalClasses() { return totalClasses; }
        public void setTotalClasses(long totalClasses) { this.totalClasses = totalClasses; }
        
        public long getActiveClasses() { return activeClasses; }
        public void setActiveClasses(long activeClasses) { this.activeClasses = activeClasses; }
        
        public long getTotalAttendanceRecords() { return totalAttendanceRecords; }
        public void setTotalAttendanceRecords(long totalAttendanceRecords) { this.totalAttendanceRecords = totalAttendanceRecords; }
    }
    
    // User Role Statistics
    public static class UserRoleStats {
        private long studentsCount;
        private long teachersCount;
        private long adminsCount;
        
        // Getters and Setters
        public long getStudentsCount() { return studentsCount; }
        public void setStudentsCount(long studentsCount) { this.studentsCount = studentsCount; }
        
        public long getTeachersCount() { return teachersCount; }
        public void setTeachersCount(long teachersCount) { this.teachersCount = teachersCount; }
        
        public long getAdminsCount() { return adminsCount; }
        public void setAdminsCount(long adminsCount) { this.adminsCount = adminsCount; }
    }
    
    // System Activity
    public static class SystemActivity {
        private String type;
        private String description;
        private String level;
        private String timestamp;
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
    
    // Performance Metrics
    public static class PerformanceMetrics {
        private double averageResponseTime;
        private long systemUptime;
        private int databaseConnections;
        
        // Getters and Setters
        public double getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }
        
        public long getSystemUptime() { return systemUptime; }
        public void setSystemUptime(long systemUptime) { this.systemUptime = systemUptime; }
        
        public int getDatabaseConnections() { return databaseConnections; }
        public void setDatabaseConnections(int databaseConnections) { this.databaseConnections = databaseConnections; }
    }
    
    // Real-time Data
    public static class RealTimeData {
        private LocalDateTime currentTime;
        private int activeSessions;
        private int todayAttendanceUpdates;
        private List<String> recentLogins;
        private List<SystemAlert> systemAlerts;
        
        // Getters and Setters
        public LocalDateTime getCurrentTime() { return currentTime; }
        public void setCurrentTime(LocalDateTime currentTime) { this.currentTime = currentTime; }
        
        public int getActiveSessions() { return activeSessions; }
        public void setActiveSessions(int activeSessions) { this.activeSessions = activeSessions; }
        
        public int getTodayAttendanceUpdates() { return todayAttendanceUpdates; }
        public void setTodayAttendanceUpdates(int todayAttendanceUpdates) { this.todayAttendanceUpdates = todayAttendanceUpdates; }
        
        public List<String> getRecentLogins() { return recentLogins; }
        public void setRecentLogins(List<String> recentLogins) { this.recentLogins = recentLogins; }
        
        public List<SystemAlert> getSystemAlerts() { return systemAlerts; }
        public void setSystemAlerts(List<SystemAlert> systemAlerts) { this.systemAlerts = systemAlerts; }
    }
    
    // System Alert
    public static class SystemAlert {
        private String type;
        private String message;
        private String severity;
        private String timestamp;
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
    
    // Widgets Data
    public static class WidgetsData {
        private AttendanceChartData attendanceChart;
        private ClassDistributionChart classDistribution;
        private UserActivityChart userActivity;
        
        // Getters and Setters
        public AttendanceChartData getAttendanceChart() { return attendanceChart; }
        public void setAttendanceChart(AttendanceChartData attendanceChart) { this.attendanceChart = attendanceChart; }
        
        public ClassDistributionChart getClassDistribution() { return classDistribution; }
        public void setClassDistribution(ClassDistributionChart classDistribution) { this.classDistribution = classDistribution; }
        
        public UserActivityChart getUserActivity() { return userActivity; }
        public void setUserActivity(UserActivityChart userActivity) { this.userActivity = userActivity; }
    }
    
    // Attendance Chart Data
    public static class AttendanceChartData {
        private List<String> labels;
        private List<Integer> presentData;
        private List<Integer> absentData;
        
        // Getters and Setters
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
        
        public List<Integer> getPresentData() { return presentData; }
        public void setPresentData(List<Integer> presentData) { this.presentData = presentData; }
        
        public List<Integer> getAbsentData() { return absentData; }
        public void setAbsentData(List<Integer> absentData) { this.absentData = absentData; }
    }
    
    // Class Distribution Chart
    public static class ClassDistributionChart {
        private List<String> labels;
        private List<Integer> data;
        
        // Getters and Setters
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
        
        public List<Integer> getData() { return data; }
        public void setData(List<Integer> data) { this.data = data; }
    }
    
    // User Activity Chart
    public static class UserActivityChart {
        private List<String> labels;
        private List<Integer> data;
        
        // Getters and Setters
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
        
        public List<Integer> getData() { return data; }
        public void setData(List<Integer> data) { this.data = data; }
    }
    
    // Dashboard Summary
    public static class DashboardSummary {
        private int totalUsers;
        private int totalClasses;
        private int totalAttendanceRecords;
        private double averageAttendance;
        private List<RecentActivity> recentActivities;
        private Map<String, Object> additionalStats;
        
        // Getters and Setters
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        
        public int getTotalClasses() { return totalClasses; }
        public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
        
        public int getTotalAttendanceRecords() { return totalAttendanceRecords; }
        public void setTotalAttendanceRecords(int totalAttendanceRecords) { this.totalAttendanceRecords = totalAttendanceRecords; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
        
        public List<RecentActivity> getRecentActivities() { return recentActivities; }
        public void setRecentActivities(List<RecentActivity> recentActivities) { this.recentActivities = recentActivities; }
        
        public Map<String, Object> getAdditionalStats() { return additionalStats; }
        public void setAdditionalStats(Map<String, Object> additionalStats) { this.additionalStats = additionalStats; }
    }
}
