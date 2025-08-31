package com.example.studentattendance.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.studentattendance.dto.AttendanceDto;
import com.example.studentattendance.dto.ReportsDto;
import com.example.studentattendance.models.Attendance;
import com.example.studentattendance.repositories.AttendanceRepository;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // Basic CRUD operations
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }

    public Attendance save(Attendance attendance) {
        if (attendance.getCreatedAt() == null) {
            attendance.setCreatedAt(LocalDateTime.now());
        }
        attendance.setUpdatedAt(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    public void deleteById(Long id) {
        attendanceRepository.deleteById(id);
    }

    // Find attendance by student and class and date
    public Attendance findByStudentAndClassAndDate(Long studentId, Long classId, LocalDate date) {
        return attendanceRepository.findByStudentIdAndClassIdAndDate(studentId, classId, date);
    }

    // Find attendance by student and date range
    public List<Attendance> findByStudentAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByStudentIdAndDateBetween(studentId, startDate, endDate);
    }

    // Find attendance by class and date range
    public List<Attendance> findByClassAndDateRange(Long classId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByClassIdAndDateBetween(classId, startDate, endDate);
    }

    // Find attendance by class and specific date
    public List<Attendance> findByClassAndDate(Long classId, LocalDate date) {
        return attendanceRepository.findByClassIdAndDate(classId, date);
    }

    // Mark attendance
    public Attendance markAttendance(Long studentId, Long classId, LocalDate date,
            Attendance.AttendanceStatus status, String remarks) {
        Attendance attendance = findByStudentAndClassAndDate(studentId, classId, date);

        if (attendance == null) {
            attendance = new Attendance();
            attendance.setStudentId(studentId);
            attendance.setClassId(classId);
            attendance.setDate(date);
        }

        attendance.setStatus(status);
        attendance.setRemarks(remarks);

        if (status == Attendance.AttendanceStatus.PRESENT || status == Attendance.AttendanceStatus.LATE) {
            attendance.setTimeIn(LocalDateTime.now());
        }

        return save(attendance);
    }

    // Get attendance statistics for a student
    public AttendanceDto.AttendanceStats getStudentAttendanceStats(Long studentId, LocalDate startDate,
            LocalDate endDate) {
        List<Attendance> attendances = findByStudentAndDateRange(studentId, startDate, endDate);

        AttendanceDto.AttendanceStats stats = new AttendanceDto.AttendanceStats();
        stats.setTotalClasses(attendances.size());
        stats.setPresentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count());
        stats.setAbsentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count());
        stats.setLateCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count());

        if (stats.getTotalClasses() > 0) {
            stats.setAttendancePercentage((double) stats.getPresentCount() / stats.getTotalClasses() * 100);
        }

        return stats;
    }

    // Get attendance statistics for a class
    public AttendanceDto.AttendanceStats getClassAttendanceStats(Long classId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = findByClassAndDateRange(classId, startDate, endDate);

        AttendanceDto.AttendanceStats stats = new AttendanceDto.AttendanceStats();
        stats.setTotalClasses(attendances.size());
        stats.setPresentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count());
        stats.setAbsentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count());
        stats.setLateCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count());

        if (stats.getTotalClasses() > 0) {
            stats.setAttendancePercentage((double) stats.getPresentCount() / stats.getTotalClasses() * 100);
        }

        return stats;
    }

    // Get recent attendance activities
    public List<AttendanceDto.RecentAttendanceActivity> getRecentAttendanceActivities(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Attendance> recentAttendances = attendanceRepository.findRecentAttendances(pageable);

        return recentAttendances.stream()
                .map(this::convertToRecentActivity)
                .collect(Collectors.toList());
    }

    // Get attendance history for a student
    public List<AttendanceDto.AttendanceHistoryItem> getStudentAttendanceHistory(Long studentId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Attendance> attendanceHistory = attendanceRepository.findByStudentIdOrderByDateDesc(studentId, pageable);

        return attendanceHistory.stream()
                .map(this::convertToHistoryItem)
                .collect(Collectors.toList());
    }

    // Dashboard methods
    public long getTotalAttendanceRecords() {
        return attendanceRepository.count();
    }

    public long getTodayAttendanceCount() {
        return attendanceRepository.countByDate(LocalDate.now());
    }

    public double getTodayAttendanceRate() {
        long totalToday = getTodayAttendanceCount();
        if (totalToday == 0)
            return 0.0;

        long presentToday = attendanceRepository.countByDateAndStatus(LocalDate.now(),
                Attendance.AttendanceStatus.PRESENT);
        return (double) presentToday / totalToday * 100;
    }

    public List<AttendanceDto.AttendanceTrend> getAttendanceTrends(int days) {
        List<AttendanceDto.AttendanceTrend> trends = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            long totalForDay = attendanceRepository.countByDate(currentDate);
            long presentForDay = attendanceRepository.countByDateAndStatus(currentDate,
                    Attendance.AttendanceStatus.PRESENT);

            AttendanceDto.AttendanceTrend trend = new AttendanceDto.AttendanceTrend();
            trend.setDate(currentDate);
            trend.setTotalRecords((int) totalForDay);
            trend.setPresentCount((int) presentForDay);
            trend.setAttendanceRate(totalForDay > 0 ? (double) presentForDay / totalForDay * 100 : 0.0);

            trends.add(trend);
            currentDate = currentDate.plusDays(1);
        }

        return trends;
    }

    // System health methods
    public long countAll() {
        return attendanceRepository.count();
    }

    public double getAverageResponseTime() {
        // Mock implementation - would measure actual response times
        return 150.0; // milliseconds
    }

    public String getSystemUptime() {
        // Mock implementation - would calculate actual uptime
        return "99.9%";
    }

    public int getDatabaseConnections() {
        // Mock implementation - would get actual connection count
        return 5;
    }

    public List<Integer> getRecentAttendanceMarks(int hours) {
        // Mock implementation - would get actual recent marks
        return Arrays.asList(10, 15, 8, 12, 20);
    }

    // Report generation methods
    public ReportsDto.AttendanceSummaryReport generateAttendanceSummaryReport(LocalDate startDate, LocalDate endDate,
            Long classId, Long studentId) {
        ReportsDto.AttendanceSummaryReport report = new ReportsDto.AttendanceSummaryReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        List<Attendance> attendances;
        if (classId != null && studentId != null) {
            attendances = attendanceRepository.findByClassIdAndStudentIdAndDateBetween(classId, studentId, startDate,
                    endDate);
        } else if (classId != null) {
            attendances = findByClassAndDateRange(classId, startDate, endDate);
        } else if (studentId != null) {
            attendances = findByStudentAndDateRange(studentId, startDate, endDate);
        } else {
            attendances = attendanceRepository.findByDateBetween(startDate, endDate);
        }

        report.setTotalRecords(attendances.size());
        report.setPresentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count());
        report.setAbsentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count());
        report.setLateCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count());

        if (!attendances.isEmpty()) {
            report.setAverageAttendance((double) report.getPresentCount() / attendances.size() * 100);
        }

        return report;
    }

    public List<ReportsDto.DailyAttendanceReport> generateDailyAttendanceReport(LocalDate startDate, LocalDate endDate,
            Long classId) {
        List<ReportsDto.DailyAttendanceReport> reports = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            ReportsDto.DailyAttendanceReport dailyReport = new ReportsDto.DailyAttendanceReport();
            dailyReport.setDate(currentDate);

            List<Attendance> dayAttendances;
            if (classId != null) {
                dayAttendances = findByClassAndDate(classId, currentDate);
            } else {
                dayAttendances = attendanceRepository.findByDate(currentDate);
            }

            dailyReport.setTotalStudents(dayAttendances.size());
            dailyReport.setPresentCount((int) dayAttendances.stream()
                    .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count());
            dailyReport.setAbsentCount((int) dayAttendances.stream()
                    .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count());
            dailyReport.setLateCount((int) dayAttendances.stream()
                    .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count());

            if (dailyReport.getTotalStudents() > 0) {
                dailyReport.setAttendancePercentage(
                        (double) dailyReport.getPresentCount() / dailyReport.getTotalStudents() * 100);
            }

            reports.add(dailyReport);
            currentDate = currentDate.plusDays(1);
        }

        return reports;
    }

    public ReportsDto.StudentPerformanceReport generateStudentPerformanceReport(Long studentId, LocalDate startDate,
            LocalDate endDate) {
        ReportsDto.StudentPerformanceReport report = new ReportsDto.StudentPerformanceReport();

        List<Attendance> attendances = findByStudentAndDateRange(studentId, startDate, endDate);
        report.setTotalClasses(attendances.size());
        report.setPresentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count());
        report.setAbsentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count());
        report.setLateCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count());

        if (!attendances.isEmpty()) {
            report.setAttendancePercentage((double) report.getPresentCount() / attendances.size() * 100);
        }

        return report;
    }

    public ReportsDto.ClassPerformanceReport generateClassPerformanceReport(Long classId, LocalDate startDate,
            LocalDate endDate) {
        ReportsDto.ClassPerformanceReport report = new ReportsDto.ClassPerformanceReport();

        List<Attendance> attendances = findByClassAndDateRange(classId, startDate, endDate);
        report.setTotalRecords(attendances.size());
        report.setPresentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count());
        report.setAbsentCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT).count());
        report.setLateCount((int) attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE).count());

        if (!attendances.isEmpty()) {
            report.setAverageAttendance((double) report.getPresentCount() / attendances.size() * 100);
        }

        return report;
    }

    public ReportsDto.ComparativeAnalysisReport generateComparativeAnalysisReport(LocalDate period1Start,
            LocalDate period1End, LocalDate period2Start, LocalDate period2End, Long classId) {
        ReportsDto.ComparativeAnalysisReport report = new ReportsDto.ComparativeAnalysisReport();
        // Implementation would compare two periods
        return report;
    }

    public ReportsDto.TrendAnalysisReport generateTrendAnalysisReport(LocalDate startDate, LocalDate endDate,
            Long classId) {
        ReportsDto.TrendAnalysisReport report = new ReportsDto.TrendAnalysisReport();
        // Implementation would analyze trends over time
        return report;
    }

    public ReportsDto.AttendanceHeatmapReport generateAttendanceHeatmapReport(LocalDate startDate, LocalDate endDate,
            Long classId) {
        ReportsDto.AttendanceHeatmapReport report = new ReportsDto.AttendanceHeatmapReport();
        // Implementation would generate heatmap data
        return report;
    }

    public List<ReportsDto.StudentRankingReport> generateStudentRankingReport(LocalDate startDate, LocalDate endDate,
            Long classId, int limit) {
        List<ReportsDto.StudentRankingReport> reports = new ArrayList<>();
        // Implementation would rank students by attendance
        return reports;
    }

    public List<ReportsDto.ClassRankingReport> generateClassRankingReport(LocalDate startDate, LocalDate endDate,
            int limit) {
        List<ReportsDto.ClassRankingReport> reports = new ArrayList<>();
        // Implementation would rank classes by attendance
        return reports;
    }

    public ReportsDto.AttendanceExportReport generateAttendanceExportReport(LocalDate startDate, LocalDate endDate,
            Long classId, Long studentId, String format) {
        ReportsDto.AttendanceExportReport report = new ReportsDto.AttendanceExportReport();
        // Implementation would generate export data
        return report;
    }

    public ReportsDto.CustomReportResponse generateCustomReport(ReportsDto.CustomReportRequest request) {
        ReportsDto.CustomReportResponse response = new ReportsDto.CustomReportResponse();
        // Implementation would generate custom reports
        return response;
    }

    public List<ReportsDto.ReportTemplate> getReportTemplates() {
        // Implementation would return available templates
        return new ArrayList<>();
    }

    public ReportsDto.ScheduledReportResponse scheduleReport(ReportsDto.ScheduleReportRequest request) {
        ReportsDto.ScheduledReportResponse response = new ReportsDto.ScheduledReportResponse();
        // Implementation would schedule reports
        return response;
    }

    public List<ReportsDto.ScheduledReportResponse> getScheduledReports() {
        // Implementation would return scheduled reports
        return new ArrayList<>();
    }

    public void cancelScheduledReport(Long id) {
        // Implementation would cancel scheduled reports
    }

    // Helper methods for conversion
    private AttendanceDto.RecentAttendanceActivity convertToRecentActivity(Attendance attendance) {
        AttendanceDto.RecentAttendanceActivity activity = new AttendanceDto.RecentAttendanceActivity();
        activity.setId(attendance.getId());
        activity.setStudentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName());
        activity.setClassName(attendance.getClassObj().getClassName());
        activity.setStatus(attendance.getStatus());
        activity.setDate(attendance.getDate());
        if (attendance.getTimeIn() != null) {
            activity.setTimeIn(attendance.getTimeIn().toString());
        }
        return activity;
    }

    private AttendanceDto.AttendanceHistoryItem convertToHistoryItem(Attendance attendance) {
        AttendanceDto.AttendanceHistoryItem item = new AttendanceDto.AttendanceHistoryItem();
        item.setId(attendance.getId());
        item.setClassName(attendance.getClassObj().getClassName());
        item.setStatus(attendance.getStatus());
        item.setDate(attendance.getDate());
        if (attendance.getTimeIn() != null) {
            item.setTimeIn(attendance.getTimeIn().toString());
        }
        if (attendance.getTimeOut() != null) {
            item.setTimeOut(attendance.getTimeOut().toString());
        }
        return item;
    }
}