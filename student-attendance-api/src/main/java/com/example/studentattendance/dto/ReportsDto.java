package com.example.studentattendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReportsDto {
    
    // Attendance Summary Report
    public static class AttendanceSummaryReport {
        private LocalDate startDate;
        private LocalDate endDate;
        private int totalRecords;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double averageAttendance;
        private Map<String, Integer> attendanceByClass;
        private Map<String, Integer> attendanceByDate;
        
        // Getters and Setters
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
        
        public Map<String, Integer> getAttendanceByClass() { return attendanceByClass; }
        public void setAttendanceByClass(Map<String, Integer> attendanceByClass) { this.attendanceByClass = attendanceByClass; }
        
        public Map<String, Integer> getAttendanceByDate() { return attendanceByDate; }
        public void setAttendanceByDate(Map<String, Integer> attendanceByDate) { this.attendanceByDate = attendanceByDate; }
    }
    
    // Daily Attendance Report
    public static class DailyAttendanceReport {
        private LocalDate date;
        private int totalStudents;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double attendancePercentage;
        private List<DailyClassAttendance> classAttendances;
        
        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
        
        public List<DailyClassAttendance> getClassAttendances() { return classAttendances; }
        public void setClassAttendances(List<DailyClassAttendance> classAttendances) { this.classAttendances = classAttendances; }
    }
    
    // Daily Class Attendance
    public static class DailyClassAttendance {
        private Long classId;
        private String className;
        private String teacherName;
        private int totalStudents;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double attendancePercentage;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
    
    // Student Performance Report
    public static class StudentPerformanceReport {
        private Long studentId;
        private String studentName;
        private LocalDate startDate;
        private LocalDate endDate;
        private int totalClasses;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double attendancePercentage;
        private List<ClassPerformance> classPerformances;
        private Map<String, Double> weeklyTrends;
        
        // Getters and Setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public int getTotalClasses() { return totalClasses; }
        public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
        
        public List<ClassPerformance> getClassPerformances() { return classPerformances; }
        public void setClassPerformances(List<ClassPerformance> classPerformances) { this.classPerformances = classPerformances; }
        
        public Map<String, Double> getWeeklyTrends() { return weeklyTrends; }
        public void setWeeklyTrends(Map<String, Double> weeklyTrends) { this.weeklyTrends = weeklyTrends; }
    }
    
    // Class Performance
    public static class ClassPerformance {
        private Long classId;
        private String className;
        private String subject;
        private int totalSessions;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double attendancePercentage;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public int getTotalSessions() { return totalSessions; }
        public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
    
    // Class Performance Report
    public static class ClassPerformanceReport {
        private Long classId;
        private String className;
        private String subject;
        private String teacherName;
        private LocalDate startDate;
        private LocalDate endDate;
        private int totalStudents;
        private int totalSessions;
        private double averageAttendance;
        private List<StudentAttendanceSummary> studentSummaries;
        private Map<String, Double> dailyTrends;
        
        // Getters and Setters
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getTotalSessions() { return totalSessions; }
        public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
        
        public List<StudentAttendanceSummary> getStudentSummaries() { return studentSummaries; }
        public void setStudentSummaries(List<StudentAttendanceSummary> studentSummaries) { this.studentSummaries = studentSummaries; }
        
        public Map<String, Double> getDailyTrends() { return dailyTrends; }
        public void setDailyTrends(Map<String, Double> dailyTrends) { this.dailyTrends = dailyTrends; }
    }
    
    // Student Attendance Summary
    public static class StudentAttendanceSummary {
        private Long studentId;
        private String studentName;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private double attendancePercentage;
        
        // Getters and Setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
    
    // Comparative Analysis Report
    public static class ComparativeAnalysisReport {
        private LocalDate period1Start;
        private LocalDate period1End;
        private LocalDate period2Start;
        private LocalDate period2End;
        private PeriodComparison period1;
        private PeriodComparison period2;
        private ComparisonMetrics comparison;
        
        // Getters and Setters
        public LocalDate getPeriod1Start() { return period1Start; }
        public void setPeriod1Start(LocalDate period1Start) { this.period1Start = period1Start; }
        
        public LocalDate getPeriod1End() { return period1End; }
        public void setPeriod1End(LocalDate period1End) { this.period1End = period1End; }
        
        public LocalDate getPeriod2Start() { return period2Start; }
        public void setPeriod2Start(LocalDate period2Start) { this.period2Start = period2Start; }
        
        public LocalDate getPeriod2End() { return period2End; }
        public void setPeriod2End(LocalDate period2End) { this.period2End = period2End; }
        
        public PeriodComparison getPeriod1() { return period1; }
        public void setPeriod1(PeriodComparison period1) { this.period1 = period1; }
        
        public PeriodComparison getPeriod2() { return period2; }
        public void setPeriod2(PeriodComparison period2) { this.period2 = period2; }
        
        public ComparisonMetrics getComparison() { return comparison; }
        public void setComparison(ComparisonMetrics comparison) { this.comparison = comparison; }
    }
    
    // Period Comparison
    public static class PeriodComparison {
        private LocalDate startDate;
        private LocalDate endDate;
        private int totalRecords;
        private double averageAttendance;
        private Map<String, Integer> attendanceByStatus;
        
        // Getters and Setters
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
        
        public Map<String, Integer> getAttendanceByStatus() { return attendanceByStatus; }
        public void setAttendanceByStatus(Map<String, Integer> attendanceByStatus) { this.attendanceByStatus = attendanceByStatus; }
    }
    
    // Comparison Metrics
    public static class ComparisonMetrics {
        private double attendanceChange;
        private double presentChange;
        private double absentChange;
        private double lateChange;
        private String trend;
        
        // Getters and Setters
        public double getAttendanceChange() { return attendanceChange; }
        public void setAttendanceChange(double attendanceChange) { this.attendanceChange = attendanceChange; }
        
        public double getPresentChange() { return presentChange; }
        public void setPresentChange(double presentChange) { this.presentChange = presentChange; }
        
        public double getAbsentChange() { return absentChange; }
        public void setAbsentChange(double absentChange) { this.absentChange = absentChange; }
        
        public double getLateChange() { return lateChange; }
        public void setLateChange(double lateChange) { this.lateChange = lateChange; }
        
        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
    }
    
    // Trend Analysis Report
    public static class TrendAnalysisReport {
        private LocalDate startDate;
        private LocalDate endDate;
        private List<MonthlyTrend> monthlyTrends;
        private List<WeeklyTrend> weeklyTrends;
        private String overallTrend;
        private double trendSlope;
        
        // Getters and Setters
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public List<MonthlyTrend> getMonthlyTrends() { return monthlyTrends; }
        public void setMonthlyTrends(List<MonthlyTrend> monthlyTrends) { this.monthlyTrends = monthlyTrends; }
        
        public List<WeeklyTrend> getWeeklyTrends() { return weeklyTrends; }
        public void setWeeklyTrends(List<WeeklyTrend> weeklyTrends) { this.weeklyTrends = weeklyTrends; }
        
        public String getOverallTrend() { return overallTrend; }
        public void setOverallTrend(String overallTrend) { this.overallTrend = overallTrend; }
        
        public double getTrendSlope() { return trendSlope; }
        public void setTrendSlope(double trendSlope) { this.trendSlope = trendSlope; }
    }
    
    // Monthly Trend
    public static class MonthlyTrend {
        private String month;
        private int year;
        private double attendancePercentage;
        private int totalRecords;
        
        // Getters and Setters
        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
        
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
    }
    
    // Weekly Trend
    public static class WeeklyTrend {
        private String week;
        private int year;
        private double attendancePercentage;
        private int totalRecords;
        
        // Getters and Setters
        public String getWeek() { return week; }
        public void setWeek(String week) { this.week = week; }
        
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
        
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
    }
    
    // Attendance Heatmap Report
    public static class AttendanceHeatmapReport {
        private LocalDate startDate;
        private LocalDate endDate;
        private Map<String, Map<String, Integer>> heatmapData;
        private List<String> timeSlots;
        private List<String> daysOfWeek;
        
        // Getters and Setters
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public Map<String, Map<String, Integer>> getHeatmapData() { return heatmapData; }
        public void setHeatmapData(Map<String, Map<String, Integer>> heatmapData) { this.heatmapData = heatmapData; }
        
        public List<String> getTimeSlots() { return timeSlots; }
        public void setTimeSlots(List<String> timeSlots) { this.timeSlots = timeSlots; }
        
        public List<String> getDaysOfWeek() { return daysOfWeek; }
        public void setDaysOfWeek(List<String> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    }
    
    // Student Ranking Report
    public static class StudentRankingReport {
        private int rank;
        private Long studentId;
        private String studentName;
        private String department;
        private double attendancePercentage;
        private int totalClasses;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        
        // Getters and Setters
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
        
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
        
        public int getTotalClasses() { return totalClasses; }
        public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
        
        public int getPresentCount() { return presentCount; }
        public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
        
        public int getAbsentCount() { return absentCount; }
        public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }
        
        public int getLateCount() { return lateCount; }
        public void setLateCount(int lateCount) { this.lateCount = lateCount; }
    }
    
    // Class Ranking Report
    public static class ClassRankingReport {
        private int rank;
        private Long classId;
        private String className;
        private String subject;
        private String teacherName;
        private double averageAttendance;
        private int totalStudents;
        private int totalSessions;
        
        // Getters and Setters
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
        
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        
        public double getAverageAttendance() { return averageAttendance; }
        public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getTotalSessions() { return totalSessions; }
        public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
    }
    
    // Attendance Export Report
    public static class AttendanceExportReport {
        private String format;
        private String downloadUrl;
        private LocalDateTime generatedAt;
        private int totalRecords;
        private String fileName;
        
        // Getters and Setters
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
    }
    
    // System Health Report
    public static class SystemHealthReport {
        private long totalUsers;
        private long activeUsers;
        private long totalClasses;
        private long activeClasses;
        private long totalAttendanceRecords;
        private double averageResponseTime;
        private long systemUptime;
        private int databaseConnections;
        private List<String> recentLogins;
        private List<String> recentAttendanceMarks;
        
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
        
        public double getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }
        
        public long getSystemUptime() { return systemUptime; }
        public void setSystemUptime(long systemUptime) { this.systemUptime = systemUptime; }
        
        public int getDatabaseConnections() { return databaseConnections; }
        public void setDatabaseConnections(int databaseConnections) { this.databaseConnections = databaseConnections; }
        
        public List<String> getRecentLogins() { return recentLogins; }
        public void setRecentLogins(List<String> recentLogins) { this.recentLogins = recentLogins; }
        
        public List<String> getRecentAttendanceMarks() { return recentAttendanceMarks; }
        public void setRecentAttendanceMarks(List<String> recentAttendanceMarks) { this.recentAttendanceMarks = recentAttendanceMarks; }
    }
    
    // Custom Report Request
    public static class CustomReportRequest {
        private String reportType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Long classId;
        private Long studentId;
        private List<String> metrics;
        private String format;
        private Map<String, Object> parameters;
        
        // Getters and Setters
        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public List<String> getMetrics() { return metrics; }
        public void setMetrics(List<String> metrics) { this.metrics = metrics; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    // Custom Report Response
    public static class CustomReportResponse {
        private String reportId;
        private String reportType;
        private String status;
        private String downloadUrl;
        private LocalDateTime generatedAt;
        private Map<String, Object> data;
        
        // Getters and Setters
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        
        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }
    
    // Report Template
    public static class ReportTemplate {
        private String name;
        private String description;
        private List<String> parameters;
        private String category;
        private boolean isCustomizable;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getParameters() { return parameters; }
        public void setParameters(List<String> parameters) { this.parameters = parameters; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public boolean isCustomizable() { return isCustomizable; }
        public void setCustomizable(boolean customizable) { isCustomizable = customizable; }
    }
    
    // Schedule Report Request
    public static class ScheduleReportRequest {
        private String reportType;
        private String schedule; // cron expression
        private String email;
        private Map<String, Object> parameters;
        private boolean isActive;
        
        // Getters and Setters
        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
        
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
    
    // Scheduled Report Response
    public static class ScheduledReportResponse {
        private Long id;
        private String reportType;
        private String schedule;
        private String email;
        private String status;
        private LocalDateTime nextRun;
        private LocalDateTime lastRun;
        private boolean isActive;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
        
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getNextRun() { return nextRun; }
        public void setNextRun(LocalDateTime nextRun) { this.nextRun = nextRun; }
        
        public LocalDateTime getLastRun() { return lastRun; }
        public void setLastRun(LocalDateTime lastRun) { this.lastRun = lastRun; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
}
