package com.example.studentattendance.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentattendance.dto.AttendanceDto;
import com.example.studentattendance.models.Attendance;
import com.example.studentattendance.models.Class;
import com.example.studentattendance.models.User;
import com.example.studentattendance.services.AttendanceService;
import com.example.studentattendance.services.ClassService;
import com.example.studentattendance.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ClassService classService;

    @Autowired
    private UserService userService;

    // Mark attendance for a single student
    @PostMapping("/mark")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<AttendanceDto.AttendanceResponse> markAttendance(
            @Valid @RequestBody AttendanceDto.MarkAttendanceRequest request) {
        try {
            // Verify class exists and teacher has access
            Class classObj = classService.findById(request.getClassId()).orElse(null);
            if (classObj == null) {
                return ResponseEntity.notFound().build();
            }

            // Verify student exists
            User student = userService.findById(request.getStudentId());
            if (student == null || student.getRole() != User.UserRole.STUDENT) {
                return ResponseEntity.badRequest().build();
            }

            // Check if attendance already exists for this student, class, and date
            Attendance existingAttendance = attendanceService.findByStudentAndClassAndDate(
                    request.getStudentId(), request.getClassId(), request.getDate());

            if (existingAttendance != null) {
                // Update existing attendance
                existingAttendance.setStatus(request.getStatus());
                existingAttendance.setTimeIn(parseTimeString(request.getTimeIn()));
                existingAttendance.setTimeOut(parseTimeString(request.getTimeOut()));
                existingAttendance.setNotes(request.getNotes());
                existingAttendance.setMarkedBy(request.getMarkedBy());
                existingAttendance.setUpdatedAt(LocalDateTime.now());

                Attendance updatedAttendance = attendanceService.save(existingAttendance);
                return ResponseEntity.ok(convertToAttendanceResponse(updatedAttendance));
            } else {
                // Create new attendance record
                Attendance attendance = new Attendance();
                attendance.setStudent(student);
                attendance.setClassObj(classObj);
                attendance.setDate(request.getDate());
                attendance.setStatus(request.getStatus());
                attendance.setTimeIn(parseTimeString(request.getTimeIn()));
                attendance.setTimeOut(parseTimeString(request.getTimeOut()));
                attendance.setNotes(request.getNotes());
                attendance.setMarkedBy(request.getMarkedBy());

                Attendance savedAttendance = attendanceService.save(attendance);
                return ResponseEntity.ok(convertToAttendanceResponse(savedAttendance));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Mark attendance for multiple students (bulk operation)
    @PostMapping("/mark/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<AttendanceDto.AttendanceResponse>> markBulkAttendance(
            @Valid @RequestBody AttendanceDto.BulkAttendanceRequest request) {
        try {
            // Verify class exists
            Class classObj = classService.findById(request.getClassId()).orElse(null);
            if (classObj == null) {
                return ResponseEntity.notFound().build();
            }

            List<AttendanceDto.AttendanceResponse> responses = request.getAttendances().stream()
                    .map(attendanceRequest -> {
                        try {
                            // Verify student exists
                            User student = userService.findById(attendanceRequest.getStudentId());
                            if (student == null || student.getRole() != User.UserRole.STUDENT) {
                                return null;
                            }

                            // Check if attendance already exists
                            Attendance existingAttendance = attendanceService.findByStudentAndClassAndDate(
                                    attendanceRequest.getStudentId(), request.getClassId(), request.getDate());

                            if (existingAttendance != null) {
                                // Update existing
                                existingAttendance.setStatus(attendanceRequest.getStatus());
                                existingAttendance.setTimeIn(parseTimeString(attendanceRequest.getTimeIn()));
                                existingAttendance.setTimeOut(parseTimeString(attendanceRequest.getTimeOut()));
                                existingAttendance.setNotes(attendanceRequest.getNotes());
                                existingAttendance.setMarkedBy(request.getMarkedBy());
                                existingAttendance.setUpdatedAt(LocalDateTime.now());

                                Attendance updatedAttendance = attendanceService.save(existingAttendance);
                                return convertToAttendanceResponse(updatedAttendance);
                            } else {
                                // Create new
                                Attendance attendance = new Attendance();
                                attendance.setStudent(student);
                                attendance.setClassObj(classObj);
                                attendance.setDate(request.getDate());
                                attendance.setStatus(attendanceRequest.getStatus());
                                attendance.setTimeIn(parseTimeString(attendanceRequest.getTimeIn()));
                                attendance.setTimeOut(parseTimeString(attendanceRequest.getTimeOut()));
                                attendance.setNotes(attendanceRequest.getNotes());
                                attendance.setMarkedBy(request.getMarkedBy());

                                Attendance savedAttendance = attendanceService.save(attendance);
                                return convertToAttendanceResponse(savedAttendance);
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(response -> response != null)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get attendance by class and date
    @GetMapping("/class/{classId}/date/{date}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or @classService.isStudentEnrolled(#classId, authentication.principal.id)")
    public ResponseEntity<List<AttendanceDto.AttendanceResponse>> getAttendanceByClassAndDate(
            @PathVariable Long classId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Attendance> attendances = attendanceService.findByClassAndDate(classId, date);
            List<AttendanceDto.AttendanceResponse> responses = attendances.stream()
                    .map(this::convertToAttendanceResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get attendance by student and date range
    @GetMapping("/student/{studentId}/range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or #studentId == authentication.principal.id")
    public ResponseEntity<List<AttendanceDto.AttendanceResponse>> getAttendanceByStudentAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Attendance> attendances = attendanceService.findByStudentAndDateRange(studentId, startDate, endDate);
            List<AttendanceDto.AttendanceResponse> responses = attendances.stream()
                    .map(this::convertToAttendanceResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get attendance by class and date range
    @GetMapping("/class/{classId}/range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or @classService.isStudentEnrolled(#classId, authentication.principal.id)")
    public ResponseEntity<List<AttendanceDto.AttendanceResponse>> getAttendanceByClassAndDateRange(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Attendance> attendances = attendanceService.findByClassAndDateRange(classId, startDate, endDate);
            List<AttendanceDto.AttendanceResponse> responses = attendances.stream()
                    .map(this::convertToAttendanceResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get today's attendance for a class
    @GetMapping("/class/{classId}/today")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or @classService.isStudentEnrolled(#classId, authentication.principal.id)")
    public ResponseEntity<List<AttendanceDto.AttendanceResponse>> getTodayAttendance(@PathVariable Long classId) {
        try {
            LocalDate today = LocalDate.now();
            List<Attendance> attendances = attendanceService.findByClassAndDate(classId, today);
            List<AttendanceDto.AttendanceResponse> responses = attendances.stream()
                    .map(this::convertToAttendanceResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Update attendance record
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<AttendanceDto.AttendanceResponse> updateAttendance(
            @PathVariable Long id, @Valid @RequestBody AttendanceDto.UpdateAttendanceRequest request) {
        try {
            Attendance attendance = attendanceService.findById(id).orElse(null);
            if (attendance == null) {
                return ResponseEntity.notFound().build();
            }

            if (request.getStatus() != null)
                attendance.setStatus(request.getStatus());
            if (request.getTimeIn() != null)
                attendance.setTimeIn(parseTimeString(request.getTimeIn()));
            if (request.getTimeOut() != null)
                attendance.setTimeOut(parseTimeString(request.getTimeOut()));
            if (request.getNotes() != null)
                attendance.setNotes(request.getNotes());

            attendance.setUpdatedAt(LocalDateTime.now());

            Attendance updatedAttendance = attendanceService.save(attendance);
            return ResponseEntity.ok(convertToAttendanceResponse(updatedAttendance));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete attendance record (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAttendance(@PathVariable Long id) {
        try {
            attendanceService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get attendance statistics for a class
    @GetMapping("/class/{classId}/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or @classService.isStudentEnrolled(#classId, authentication.principal.id)")
    public ResponseEntity<AttendanceDto.ClassAttendanceStats> getClassAttendanceStats(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            AttendanceDto.ClassAttendanceStats stats = attendanceService.getClassAttendanceStats(classId, startDate,
                    endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get student attendance summary
    @GetMapping("/student/{studentId}/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or #studentId == authentication.principal.id")
    public ResponseEntity<AttendanceDto.StudentAttendanceSummary> getStudentAttendanceSummary(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            AttendanceDto.StudentAttendanceSummary summary = attendanceService.getStudentAttendanceSummary(studentId,
                    startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get overall attendance statistics
    @GetMapping("/stats/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<AttendanceDto.OverallAttendanceStats> getOverallAttendanceStats(
            @RequestParam(defaultValue = "30") int days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days);

            AttendanceDto.OverallAttendanceStats stats = attendanceService.getOverallAttendanceStats(startDate,
                    endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Helper method to parse time string to LocalTime
    private LocalTime parseTimeString(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        try {
            // Try parsing with different formats
            if (timeString.length() == 5) { // HH:mm format
                return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
            } else if (timeString.length() == 8) { // HH:mm:ss format
                return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
            } else {
                // Try default parsing
                return LocalTime.parse(timeString);
            }
        } catch (DateTimeParseException e) {
            // Log the error and return null
            System.err.println("Failed to parse time string: " + timeString + " - " + e.getMessage());
            return null;
        }
    }

    // Helper method to format LocalTime to String
    private String formatTimeToString(LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private AttendanceDto.AttendanceResponse convertToAttendanceResponse(Attendance attendance) {
        AttendanceDto.AttendanceResponse response = new AttendanceDto.AttendanceResponse();
        response.setId(attendance.getId());
        response.setStudentId(attendance.getStudent().getId());
        response.setStudentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName());
        response.setClassId(attendance.getClassObj().getId());
        response.setClassName(attendance.getClassObj().getClassName());
        response.setDate(attendance.getDate());
        response.setStatus(attendance.getStatus());
        response.setTimeIn(formatTimeToString(attendance.getTimeIn()));
        response.setTimeOut(formatTimeToString(attendance.getTimeOut()));
        response.setNotes(attendance.getNotes());
        response.setMarkedBy(attendance.getMarkedBy());
        response.setCreatedAt(attendance.getCreatedAt());
        response.setUpdatedAt(attendance.getUpdatedAt());
        return response;
    }
}
