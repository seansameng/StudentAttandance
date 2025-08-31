package com.example.studentattendance.controllers;

import com.example.studentattendance.dto.DashboardDto;
import com.example.studentattendance.models.Class;
import com.example.studentattendance.models.User;
import com.example.studentattendance.services.AttendanceService;
import com.example.studentattendance.services.ClassService;
import com.example.studentattendance.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ClassService classService;
    
    @Autowired
    private AttendanceService attendanceService;
    
    // Get comprehensive dashboard overview
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<DashboardDto.DashboardData> getDashboardOverview() {
        try {
            DashboardDto.DashboardData dashboardData = new DashboardDto.DashboardData();
            
            // Get total classes
            dashboardData.setTotalClasses(classService.countActiveClasses());
            
            // Get total students
            dashboardData.setTotalStudents(userService.countByRole(User.UserRole.STUDENT));
            
            // Get today's attendance
            LocalDate today = LocalDate.now();
            int todayAttendance = attendanceService.getTodayAttendanceCount();
            dashboardData.setTodayAttendance(todayAttendance);
            
            // Get average attendance percentage
            double averageAttendance = attendanceService.getAverageAttendancePercentage(30); // Last 30 days
            dashboardData.setAverageAttendance(averageAttendance);
            
            // Get recent activities
            List<DashboardDto.RecentActivity> activities = getRecentActivities();
            dashboardData.setRecentActivities(activities);
            
            // Get quick stats
            DashboardDto.QuickStats quickStats = new DashboardDto.QuickStats();
            quickStats.setPresentToday(todayAttendance);
            quickStats.setAbsentToday(attendanceService.getTodayAbsentCount());
            quickStats.setLateToday(attendanceService.getTodayLateCount());
            quickStats.setClassesToday(classService.getTodayClassesCount());
            dashboardData.setQuickStats(quickStats);
            
            // Get attendance trends
            DashboardDto.AttendanceTrends trends = new DashboardDto.AttendanceTrends();
            trends.setWeeklyTrend(attendanceService.getWeeklyAttendanceTrend());
            trends.setMonthlyTrend(attendanceService.getMonthlyAttendanceTrend());
            dashboardData.setAttendanceTrends(trends);
            
            return ResponseEntity.ok(dashboardData);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get upcoming classes
    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<List<Class>> getUpcomingClasses() {
        try {
            // Get upcoming classes (next 7 days)
            LocalDate today = LocalDate.now();
            LocalDate nextWeek = today.plusDays(7);
            
            List<Class> upcomingClasses = classService.findUpcomingClasses(today, nextWeek);
            return ResponseEntity.ok(upcomingClasses);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get teacher-specific dashboard
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN') or #teacherId == authentication.principal.id")
    public ResponseEntity<DashboardDto.TeacherDashboardData> getTeacherDashboard(@PathVariable Long teacherId) {
        try {
            DashboardDto.TeacherDashboardData teacherDashboard = new DashboardDto.TeacherDashboardData();
            
            // Get teacher's classes
            List<Class> teacherClasses = classService.findByTeacherId(teacherId);
            teacherDashboard.setClasses(teacherClasses);
            
            // Get today's schedule
            List<Class> todayClasses = classService.getTodayClassesForTeacher(teacherId);
            teacherDashboard.setTodayClasses(todayClasses);
            
            // Get attendance summary for teacher's classes
            DashboardDto.TeacherAttendanceSummary attendanceSummary = new DashboardDto.TeacherAttendanceSummary();
            attendanceSummary.setTotalStudents(attendanceService.getTotalStudentsInTeacherClasses(teacherId));
            attendanceSummary.setPresentToday(attendanceService.getTodayPresentCountForTeacher(teacherId));
            attendanceSummary.setAbsentToday(attendanceService.getTodayAbsentCountForTeacher(teacherId));
            attendanceSummary.setAverageAttendance(attendanceService.getAverageAttendanceForTeacher(teacherId, 30));
            teacherDashboard.setAttendanceSummary(attendanceSummary);
            
            // Get recent attendance activities
            List<DashboardDto.RecentAttendanceActivity> recentActivities = attendanceService.getRecentAttendanceActivitiesForTeacher(teacherId, 10);
            teacherDashboard.setRecentActivities(recentActivities);
            
            return ResponseEntity.ok(teacherDashboard);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get student-specific dashboard
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or #studentId == authentication.principal.id")
    public ResponseEntity<DashboardDto.StudentDashboardData> getStudentDashboard(@PathVariable Long studentId) {
        try {
            DashboardDto.StudentDashboardData studentDashboard = new DashboardDto.StudentDashboardData();
            
            // Get student's enrolled classes
            List<Class> enrolledClasses = classService.getEnrolledClassesForStudent(studentId);
            studentDashboard.setEnrolledClasses(enrolledClasses);
            
            // Get today's classes
            List<Class> todayClasses = classService.getTodayClassesForStudent(studentId);
            studentDashboard.setTodayClasses(todayClasses);
            
            // Get attendance summary
            DashboardDto.StudentAttendanceSummary attendanceSummary = new DashboardDto.StudentAttendanceSummary();
            attendanceSummary.setTotalClasses(enrolledClasses.size());
            attendanceSummary.setPresentThisWeek(attendanceService.getWeeklyPresentCountForStudent(studentId));
            attendanceSummary.setAbsentThisWeek(attendanceService.getWeeklyAbsentCountForStudent(studentId));
            attendanceSummary.setAttendancePercentage(attendanceService.getAttendancePercentageForStudent(studentId, 30));
            studentDashboard.setAttendanceSummary(attendanceSummary);
            
            // Get attendance history
            List<DashboardDto.AttendanceHistoryItem> attendanceHistory = attendanceService.getAttendanceHistoryForStudent(studentId, 10);
            studentDashboard.setAttendanceHistory(attendanceHistory);
            
            return ResponseEntity.ok(studentDashboard);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get admin dashboard
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDto.AdminDashboardData> getAdminDashboard() {
        try {
            DashboardDto.AdminDashboardData adminDashboard = new DashboardDto.AdminDashboardData();
            
            // System overview
            DashboardDto.SystemOverview systemOverview = new DashboardDto.SystemOverview();
            systemOverview.setTotalUsers(userService.countAll());
            systemOverview.setActiveUsers(userService.countActiveUsers());
            systemOverview.setTotalClasses(classService.countAll());
            systemOverview.setActiveClasses(classService.countActiveClasses());
            systemOverview.setTotalAttendanceRecords(attendanceService.countAll());
            adminDashboard.setSystemOverview(systemOverview);
            
            // User statistics by role
            DashboardDto.UserRoleStats userRoleStats = new DashboardDto.UserRoleStats();
            userRoleStats.setStudentsCount(userService.countByRole(User.UserRole.STUDENT));
            userRoleStats.setTeachersCount(userService.countByRole(User.UserRole.TEACHER));
            userRoleStats.setAdminsCount(userService.countByRole(User.UserRole.ADMIN));
            adminDashboard.setUserRoleStats(userRoleStats);
            
            // Recent system activities
            List<DashboardDto.SystemActivity> systemActivities = getSystemActivities();
            adminDashboard.setSystemActivities(systemActivities);
            
            // Performance metrics
            DashboardDto.PerformanceMetrics performanceMetrics = new DashboardDto.PerformanceMetrics();
            performanceMetrics.setAverageResponseTime(attendanceService.getAverageResponseTime());
            performanceMetrics.setSystemUptime(attendanceService.getSystemUptime());
            performanceMetrics.setDatabaseConnections(attendanceService.getDatabaseConnections());
            adminDashboard.setPerformanceMetrics(performanceMetrics);
            
            return ResponseEntity.ok(adminDashboard);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get real-time dashboard updates
    @GetMapping("/realtime")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<DashboardDto.RealTimeData> getRealTimeData() {
        try {
            DashboardDto.RealTimeData realTimeData = new DashboardDto.RealTimeData();
            
            // Current time
            realTimeData.setCurrentTime(LocalDateTime.now());
            
            // Active sessions
            realTimeData.setActiveSessions(userService.getActiveSessionsCount());
            
            // Today's attendance updates
            realTimeData.setTodayAttendanceUpdates(attendanceService.getTodayAttendanceUpdatesCount());
            
            // Recent logins
            realTimeData.setRecentLogins(userService.getRecentLogins(1)); // Last hour
            
            // System alerts
            List<DashboardDto.SystemAlert> systemAlerts = getSystemAlerts();
            realTimeData.setSystemAlerts(systemAlerts);
            
            return ResponseEntity.ok(realTimeData);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get dashboard widgets data
    @GetMapping("/widgets")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<DashboardDto.WidgetsData> getWidgetsData() {
        try {
            DashboardDto.WidgetsData widgetsData = new DashboardDto.WidgetsData();
            
            // Attendance chart data
            DashboardDto.AttendanceChartData attendanceChart = new DashboardDto.AttendanceChartData();
            attendanceChart.setLabels(attendanceService.getAttendanceChartLabels(7)); // Last 7 days
            attendanceChart.setPresentData(attendanceService.getAttendanceChartPresentData(7));
            attendanceChart.setAbsentData(attendanceService.getAttendanceChartAbsentData(7));
            widgetsData.setAttendanceChart(attendanceChart);
            
            // Class distribution chart
            DashboardDto.ClassDistributionChart classDistribution = new DashboardDto.ClassDistributionChart();
            classDistribution.setLabels(classService.getClassDistributionLabels());
            classDistribution.setData(classService.getClassDistributionData());
            widgetsData.setClassDistribution(classDistribution);
            
            // User activity chart
            DashboardDto.UserActivityChart userActivity = new DashboardDto.UserActivityChart();
            userActivity.setLabels(userService.getUserActivityLabels(7));
            userActivity.setData(userService.getUserActivityData(7));
            widgetsData.setUserActivity(userActivity);
            
            return ResponseEntity.ok(widgetsData);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private List<DashboardDto.RecentActivity> getRecentActivities() {
        List<DashboardDto.RecentActivity> activities = new ArrayList<>();
        
        // Add sample activities (replace with real data from services)
        activities.add(createMockActivity("LOGIN", "User logged in", "user123", "John Doe"));
        activities.add(createMockActivity("ATTENDANCE", "Marked attendance for Math 101", "user456", "Jane Smith"));
        activities.add(createMockActivity("CLASS_CREATED", "New class Physics 201 created", "teacher001", "Dr. Johnson"));
        activities.add(createMockActivity("USER_REGISTERED", "New student registered", "student001", "Mike Wilson"));
        
        return activities;
    }
    
    private List<DashboardDto.SystemActivity> getSystemActivities() {
        List<DashboardDto.SystemActivity> activities = new ArrayList<>();
        
        // Add sample system activities
        activities.add(createMockSystemActivity("SYSTEM_STARTUP", "System started successfully", "INFO"));
        activities.add(createMockSystemActivity("BACKUP_COMPLETED", "Daily backup completed", "SUCCESS"));
        activities.add(createMockSystemActivity("USER_LOGIN", "Multiple user logins detected", "INFO"));
        activities.add(createMockSystemActivity("DATABASE_OPTIMIZATION", "Database optimization scheduled", "WARNING"));
        
        return activities;
    }
    
    private List<DashboardDto.SystemAlert> getSystemAlerts() {
        List<DashboardDto.SystemAlert> alerts = new ArrayList<>();
        
        // Add sample system alerts
        alerts.add(createMockSystemAlert("HIGH_ATTENDANCE", "High attendance rate detected today", "INFO"));
        alerts.add(createMockSystemAlert("SYSTEM_PERFORMANCE", "System performance is optimal", "SUCCESS"));
        
        return alerts;
    }
    
    private DashboardDto.RecentActivity createMockActivity(String type, String description, String userId, String userName) {
        DashboardDto.RecentActivity activity = new DashboardDto.RecentActivity();
        activity.setType(type);
        activity.setDescription(description);
        activity.setUserId(userId);
        activity.setUserName(userName);
        activity.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return activity;
    }
    
    private DashboardDto.SystemActivity createMockSystemActivity(String type, String description, String level) {
        DashboardDto.SystemActivity activity = new DashboardDto.SystemActivity();
        activity.setType(type);
        activity.setDescription(description);
        activity.setLevel(level);
        activity.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return activity;
    }
    
    private DashboardDto.SystemAlert createMockSystemAlert(String type, String message, String severity) {
        DashboardDto.SystemAlert alert = new DashboardDto.SystemAlert();
        alert.setType(type);
        alert.setMessage(message);
        alert.setSeverity(severity);
        alert.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return alert;
    }
}
