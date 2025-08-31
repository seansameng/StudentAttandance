package com.example.studentattendance.controllers;

import com.example.studentattendance.dto.ReportsDto;
import com.example.studentattendance.services.AttendanceService;
import com.example.studentattendance.services.ClassService;
import com.example.studentattendance.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
@Validated
public class ReportsController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    private final AttendanceService attendanceService;
    private final ClassService classService;
    private final UserService userService;

    public ReportsController(AttendanceService attendanceService,
            ClassService classService,
            UserService userService) {
        this.attendanceService = attendanceService;
        this.classService = classService;
        this.userService = userService;
    }

    // Helper method for date validation
    private boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        if (startDate.isAfter(endDate)) {
            return false;
        }
        if (startDate.isBefore(LocalDate.now().minusYears(2))) {
            return false;
        }
        return true;
    }

    // Helper method for ID validation
    private boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    // Get attendance summary report
    @GetMapping("/attendance/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ReportsDto.AttendanceSummaryReport> getAttendanceSummaryReport(
            @RequestParam(defaultValue = "30") @Min(value = 1, message = "Days must be at least 1") int days,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long studentId) {
        try {
            logger.info("Generating attendance summary report for {} days, classId: {}, studentId: {}",
                    days, classId, studentId);

            if (days > 365) {
                logger.warn("Days parameter {} exceeds maximum allowed (365)", days);
                return ResponseEntity.badRequest().build();
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            ReportsDto.AttendanceSummaryReport report = attendanceService.generateAttendanceSummaryReport(
                    startDate, endDate, classId, studentId);

            logger.info("Successfully generated attendance summary report");
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for attendance summary report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating attendance summary report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get daily attendance report
    @GetMapping("/attendance/daily")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<ReportsDto.DailyAttendanceReport>> getDailyAttendanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate endDate,
            @RequestParam(required = false) Long classId) {
        try {
            logger.info("Generating daily attendance report from {} to {}, classId: {}",
                    startDate, endDate, classId);

            if (startDate.isAfter(endDate)) {
                logger.warn("Start date {} is after end date {}", startDate, endDate);
                return ResponseEntity.badRequest().build();
            }

            if (startDate.isBefore(LocalDate.now().minusYears(1))) {
                logger.warn("Start date {} is more than 1 year ago", startDate);
                return ResponseEntity.badRequest().build();
            }

            List<ReportsDto.DailyAttendanceReport> reports = attendanceService.generateDailyAttendanceReport(
                    startDate, endDate, classId);

            logger.info("Successfully generated daily attendance report with {} records", reports.size());
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for daily attendance report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating daily attendance report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get student performance report
    @GetMapping("/student/performance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or #studentId == authentication.principal.id")
    public ResponseEntity<ReportsDto.StudentPerformanceReport> getStudentPerformanceReport(
            @RequestParam @NotNull Long studentId,
            @RequestParam(defaultValue = "90") @Min(value = 1, message = "Days must be at least 1") int days) {
        try {
            logger.info("Generating student performance report for studentId: {}, days: {}", studentId, days);

            if (studentId <= 0) {
                logger.warn("Invalid student ID: {}", studentId);
                return ResponseEntity.badRequest().build();
            }

            if (days > 365) {
                logger.warn("Days parameter {} exceeds maximum allowed (365)", days);
                return ResponseEntity.badRequest().build();
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            ReportsDto.StudentPerformanceReport report = attendanceService.generateStudentPerformanceReport(
                    studentId, startDate, endDate);

            logger.info("Successfully generated student performance report for student: {}", studentId);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for student performance report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating student performance report for student: {}", studentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get class performance report
    @GetMapping("/class/performance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ReportsDto.ClassPerformanceReport> getClassPerformanceReport(
            @RequestParam @NotNull Long classId,
            @RequestParam(defaultValue = "30") @Min(value = 1, message = "Days must be at least 1") int days) {
        try {
            logger.info("Generating class performance report for classId: {}, days: {}", classId, days);

            if (!isValidId(classId)) {
                logger.warn("Invalid class ID: {}", classId);
                return ResponseEntity.badRequest().build();
            }

            if (days > 365) {
                logger.warn("Days parameter {} exceeds maximum allowed (365)", days);
                return ResponseEntity.badRequest().build();
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            ReportsDto.ClassPerformanceReport report = attendanceService.generateClassPerformanceReport(
                    classId, startDate, endDate);

            logger.info("Successfully generated class performance report for class: {}", classId);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for class performance report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating class performance report for class: {}", classId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get comparative analysis report
    @GetMapping("/comparative")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ReportsDto.ComparativeAnalysisReport> getComparativeAnalysisReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate period1Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate period1End,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate period2Start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate period2End,
            @RequestParam(required = false) Long classId) {
        try {
            logger.info("Generating comparative analysis report for periods: {}-{} vs {}-{}, classId: {}",
                    period1Start, period1End, period2Start, period2End, classId);

            if (!isValidDateRange(period1Start, period1End) || !isValidDateRange(period2Start, period2End)) {
                logger.warn("Invalid date ranges provided");
                return ResponseEntity.badRequest().build();
            }

            ReportsDto.ComparativeAnalysisReport report = attendanceService.generateComparativeAnalysisReport(
                    period1Start, period1End, period2Start, period2End, classId);

            logger.info("Successfully generated comparative analysis report");
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for comparative analysis report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating comparative analysis report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get trend analysis report
    @GetMapping("/trends")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ReportsDto.TrendAnalysisReport> getTrendAnalysisReport(
            @RequestParam(defaultValue = "12") @Min(value = 1, message = "Months must be at least 1") int months,
            @RequestParam(required = false) Long classId) {
        try {
            logger.info("Generating trend analysis report for {} months, classId: {}", months, classId);

            if (months > 24) {
                logger.warn("Months parameter {} exceeds maximum allowed (24)", months);
                return ResponseEntity.badRequest().build();
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(months);

            ReportsDto.TrendAnalysisReport report = attendanceService.generateTrendAnalysisReport(
                    startDate, endDate, classId);

            logger.info("Successfully generated trend analysis report");
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for trend analysis report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating trend analysis report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get attendance heatmap report
    @GetMapping("/heatmap")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ReportsDto.AttendanceHeatmapReport> getAttendanceHeatmapReport(
            @RequestParam(defaultValue = "30") @Min(value = 1, message = "Days must be at least 1") int days,
            @RequestParam(required = false) Long classId) {
        try {
            logger.info("Generating attendance heatmap report for {} days, classId: {}", days, classId);

            if (days > 365) {
                logger.warn("Days parameter {} exceeds maximum allowed (365)", days);
                return ResponseEntity.badRequest().build();
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            ReportsDto.AttendanceHeatmapReport report = attendanceService.generateAttendanceHeatmapReport(
                    startDate, endDate, classId);

            logger.info("Successfully generated attendance heatmap report");
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for attendance heatmap report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating attendance heatmap report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get student ranking report
    @GetMapping("/ranking/students")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<ReportsDto.StudentRankingReport>> getStudentRankingReport(
            @RequestParam(defaultValue = "30") @Min(value = 1, message = "Days must be at least 1") int days,
            @RequestParam(required = false) Long classId,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Limit must be at least 1") int limit) {
        try {
            logger.info("Generating student ranking report for {} days, classId: {}, limit: {}",
                    days, classId, limit);

            if (days > 365) {
                logger.warn("Days parameter {} exceeds maximum allowed (365)", days);
                return ResponseEntity.badRequest().build();
            }

            if (limit > 100) {
                logger.warn("Limit parameter {} exceeds maximum allowed (100)", limit);
                return ResponseEntity.badRequest().build();
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            List<ReportsDto.StudentRankingReport> reports = attendanceService.generateStudentRankingReport(
                    startDate, endDate, classId, limit);

            logger.info("Successfully generated student ranking report with {} records", reports.size());
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for student ranking report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating student ranking report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get class ranking report
    @GetMapping("/ranking/classes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<ReportsDto.ClassRankingReport>> getClassRankingReport(
            @RequestParam(defaultValue = "30") @Min(value = 1, message = "Days must be at least 1") int days,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Limit must be at least 1") int limit) {
        try {
            logger.info("Generating class ranking report for {} days, limit: {}", days, limit);

            if (days > 365) {
                logger.warn("Days parameter {} exceeds maximum allowed (365)", days);
                return ResponseEntity.badRequest().build();
            }

            if (limit > 100) {
                logger.warn("Limit parameter {} exceeds maximum allowed (100)", limit);
                return ResponseEntity.badRequest().build();
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            List<ReportsDto.ClassRankingReport> reports = attendanceService.generateClassRankingReport(
                    startDate, endDate, limit);

            logger.info("Successfully generated class ranking report with {} records", reports.size());
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for class ranking report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating class ranking report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get attendance export report (CSV/Excel format)
    @GetMapping("/export/attendance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ReportsDto.AttendanceExportReport> getAttendanceExportReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate endDate,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(defaultValue = "csv") String format) {
        try {
            logger.info("Generating attendance export report from {} to {}, classId: {}, studentId: {}, format: {}",
                    startDate, endDate, classId, studentId, format);

            if (!isValidDateRange(startDate, endDate)) {
                logger.warn("Invalid date range: {} to {}", startDate, endDate);
                return ResponseEntity.badRequest().build();
            }

            if (format != null && !format.matches("^(csv|excel|xlsx)$")) {
                logger.warn("Invalid export format: {}", format);
                return ResponseEntity.badRequest().build();
            }

            ReportsDto.AttendanceExportReport report = attendanceService.generateAttendanceExportReport(
                    startDate, endDate, classId, studentId, format);

            logger.info("Successfully generated attendance export report");
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for attendance export report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating attendance export report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get system health report
    @GetMapping("/system/health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportsDto.SystemHealthReport> getSystemHealthReport() {
        try {
            logger.info("Generating system health report");

            ReportsDto.SystemHealthReport report = new ReportsDto.SystemHealthReport();

            // System statistics
            report.setTotalUsers(userService.countAll());
            report.setActiveUsers(userService.countActiveUsers());
            report.setTotalClasses(classService.countAll());
            report.setActiveClasses(classService.countActiveClasses());
            report.setTotalAttendanceRecords(attendanceService.countAll());

            // Performance metrics
            report.setAverageResponseTime(attendanceService.getAverageResponseTime());
            report.setSystemUptime(attendanceService.getSystemUptime());
            report.setDatabaseConnections(attendanceService.getDatabaseConnections());

            logger.info("Successfully generated system health report");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            logger.error("Error generating system health report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get custom report
    @PostMapping("/custom")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ReportsDto.CustomReportResponse> generateCustomReport(
            @RequestBody @Valid ReportsDto.CustomReportRequest request) {
        try {
            logger.info("Generating custom report of type: {}", request.getReportType());

            if (request == null || request.getReportType() == null || request.getReportType().trim().isEmpty()) {
                logger.warn("Invalid custom report request");
                return ResponseEntity.badRequest().build();
            }

            ReportsDto.CustomReportResponse report = attendanceService.generateCustomReport(request);

            logger.info("Successfully generated custom report");
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for custom report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error generating custom report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get report templates
    @GetMapping("/templates")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<ReportsDto.ReportTemplate>> getReportTemplates() {
        try {
            logger.info("Retrieving report templates");

            List<ReportsDto.ReportTemplate> templates = attendanceService.getReportTemplates();

            logger.info("Successfully retrieved {} report templates", templates.size());
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            logger.error("Error retrieving report templates", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Schedule a report
    @PostMapping("/schedule")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ReportsDto.ScheduledReportResponse> scheduleReport(
            @RequestBody @Valid ReportsDto.ScheduleReportRequest request) {
        try {
            logger.info("Scheduling report of type: {}", request.getReportType());

            if (request == null || request.getReportType() == null || request.getReportType().trim().isEmpty()) {
                logger.warn("Invalid schedule report request");
                return ResponseEntity.badRequest().build();
            }

            ReportsDto.ScheduledReportResponse response = attendanceService.scheduleReport(request);

            logger.info("Successfully scheduled report with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for scheduling report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error scheduling report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get scheduled reports
    @GetMapping("/scheduled")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<ReportsDto.ScheduledReportResponse>> getScheduledReports() {
        try {
            logger.info("Retrieving scheduled reports");

            List<ReportsDto.ScheduledReportResponse> reports = attendanceService.getScheduledReports();

            logger.info("Successfully retrieved {} scheduled reports", reports.size());
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            logger.error("Error retrieving scheduled reports", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Cancel scheduled report
    @DeleteMapping("/scheduled/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Void> cancelScheduledReport(@PathVariable @NotNull Long id) {
        try {
            logger.info("Cancelling scheduled report with ID: {}", id);

            if (!isValidId(id)) {
                logger.warn("Invalid scheduled report ID: {}", id);
                return ResponseEntity.badRequest().build();
            }

            attendanceService.cancelScheduledReport(id);

            logger.info("Successfully cancelled scheduled report: {}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid ID for cancelling scheduled report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error cancelling scheduled report: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Exception handler for validation errors
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException e) {
        logger.error("Validation error: {}", e.getMessage());

        StringBuilder message = new StringBuilder("Validation failed: ");
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            message.append(violation.getMessage()).append("; ");
        }

        return ResponseEntity.badRequest().body(message.toString());
    }
}