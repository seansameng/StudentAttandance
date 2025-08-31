package com.example.studentattendance.controllers;

import com.example.studentattendance.dto.ClassDto;
import com.example.studentattendance.models.Class;
import com.example.studentattendance.models.User;
import com.example.studentattendance.services.ClassService;
import com.example.studentattendance.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classes")
@CrossOrigin(origins = "*")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    @Autowired
    private UserService userService;
    
    // Get all classes
    @GetMapping
    public ResponseEntity<List<ClassDto.ClassResponse>> getAllClasses() {
        try {
            List<Class> classes = classService.findAll();
            List<ClassDto.ClassResponse> classResponses = classes.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(classResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get active classes only
    @GetMapping("/active")
    public ResponseEntity<List<ClassDto.ClassResponse>> getActiveClasses() {
        try {
            List<Class> classes = classService.findActiveClasses();
            List<ClassDto.ClassResponse> classResponses = classes.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(classResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get class by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClassDto.ClassResponse> getClassById(@PathVariable Long id) {
        try {
            Class classObj = classService.findById(id).orElse(null);
            if (classObj != null) {
                return ResponseEntity.ok(convertToClassResponse(classObj));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get classes by teacher
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<ClassDto.ClassResponse>> getClassesByTeacher(@PathVariable Long teacherId) {
        try {
            User teacher = userService.findById(teacherId);
            if (teacher == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<Class> classes = classService.findByTeacher(teacher);
            List<ClassDto.ClassResponse> classResponses = classes.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(classResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get upcoming classes
    @GetMapping("/upcoming")
    public ResponseEntity<List<ClassDto.ClassResponse>> getUpcomingClasses(
            @RequestParam(defaultValue = "7") int days) {
        try {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(days);
            
            List<Class> classes = classService.findUpcomingClasses(startDate, endDate);
            List<ClassDto.ClassResponse> classResponses = classes.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(classResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Search classes
    @GetMapping("/search")
    public ResponseEntity<List<ClassDto.ClassResponse>> searchClasses(@RequestParam String query) {
        try {
            List<Class> classes = classService.searchClasses(query);
            List<ClassDto.ClassResponse> classResponses = classes.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(classResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get classes by semester and academic year
    @GetMapping("/semester")
    public ResponseEntity<List<ClassDto.ClassResponse>> getClassesBySemester(
            @RequestParam String semester, @RequestParam String academicYear) {
        try {
            List<Class> classes = classService.findBySemesterAndAcademicYear(semester, academicYear);
            List<ClassDto.ClassResponse> classResponses = classes.stream()
                .map(this::convertToClassResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(classResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Create new class (Admin/Teacher only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ClassDto.ClassResponse> createClass(@Valid @RequestBody ClassDto.CreateClassRequest request) {
        try {
            User teacher = userService.findById(request.getTeacherId());
            if (teacher == null || teacher.getRole() != User.UserRole.TEACHER) {
                return ResponseEntity.badRequest().body("Invalid teacher ID");
            }
            
            Class classObj = new Class();
            classObj.setClassName(request.getClassName());
            classObj.setSubject(request.getSubject());
            classObj.setTeacher(teacher);
            classObj.setSchedule(request.getSchedule());
            classObj.setRoom(request.getRoom());
            classObj.setMaxStudents(request.getMaxStudents());
            classObj.setDescription(request.getDescription());
            classObj.setSemester(request.getSemester());
            classObj.setAcademicYear(request.getAcademicYear());
            classObj.setActive(true);
            
            Class savedClass = classService.save(classObj);
            return ResponseEntity.ok(convertToClassResponse(savedClass));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Update class
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @classService.isTeacherOfClass(#id, authentication.principal.id)")
    public ResponseEntity<ClassDto.ClassResponse> updateClass(@PathVariable Long id, @Valid @RequestBody ClassDto.UpdateClassRequest request) {
        try {
            Class classObj = classService.findById(id).orElse(null);
            if (classObj == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (request.getClassName() != null) classObj.setClassName(request.getClassName());
            if (request.getSubject() != null) classObj.setSubject(request.getSubject());
            if (request.getSchedule() != null) classObj.setSchedule(request.getSchedule());
            if (request.getRoom() != null) classObj.setRoom(request.getRoom());
            if (request.getMaxStudents() != null) classObj.setMaxStudents(request.getMaxStudents());
            if (request.getDescription() != null) classObj.setDescription(request.getDescription());
            if (request.getSemester() != null) classObj.setSemester(request.getSemester());
            if (request.getAcademicYear() != null) classObj.setAcademicYear(request.getAcademicYear());
            
            Class updatedClass = classService.save(classObj);
            return ResponseEntity.ok(convertToClassResponse(updatedClass));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Deactivate class
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or @classService.isTeacherOfClass(#id, authentication.principal.id)")
    public ResponseEntity<?> deactivateClass(@PathVariable Long id) {
        try {
            Class classObj = classService.findById(id).orElse(null);
            if (classObj == null) {
                return ResponseEntity.notFound().build();
            }
            classObj.setActive(false);
            classService.save(classObj);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Activate class
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or @classService.isTeacherOfClass(#id, authentication.principal.id)")
    public ResponseEntity<?> activateClass(@PathVariable Long id) {
        try {
            Class classObj = classService.findById(id).orElse(null);
            if (classObj == null) {
                return ResponseEntity.notFound().build();
            }
            classObj.setActive(true);
            classService.save(classObj);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Delete class (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        try {
            classService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get class statistics
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('ADMIN') or @classService.isTeacherOfClass(#id, authentication.principal.id)")
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
            User student = userService.findById(request.getStudentId()).orElse(null);
            if (student == null || student.getRole() != User.UserRole.STUDENT) {
                return ResponseEntity.badRequest().build();
            }

            // Check if attendance already exists for this student, class, and date
            Attendance existingAttendance = attendanceService.findByStudentAndClassAndDate(
                    request.getStudentId(), request.getClassId(), request.getDate());

            if (existingAttendance != null) {
                // Update existing attendance
                existingAttendance.setStatus(request.getStatus());
                existingAttendance.setTimeIn(request.getTimeIn());
                existingAttendance.setTimeOut(request.getTimeOut());
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
                attendance.setTimeIn(request.getTimeIn());
                attendance.setTimeOut(request.getTimeOut());
                attendance.setNotes(request.getNotes());
                attendance.setMarkedBy(request.getMarkedBy());

                Attendance savedAttendance = attendanceService.save(attendance);
                return ResponseEntity.ok(convertToAttendanceResponse(savedAttendance));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Get all class statistics
    @GetMapping("/stats/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ClassDto.OverallClassStats> getOverallClassStats() {
        try {
            ClassDto.OverallClassStats stats = new ClassDto.OverallClassStats();
            stats.setTotalClasses(classService.countAll());
            stats.setActiveClasses(classService.countActiveClasses());
            stats.setTotalStudents(classService.countTotalEnrolledStudents());
            stats.setAverageClassSize(classService.getAverageClassSize());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private ClassDto.ClassResponse convertToClassResponse(Class classObj) {
        ClassDto.ClassResponse response = new ClassDto.ClassResponse();
        response.setId(classObj.getId());
        response.setClassName(classObj.getClassName());
        response.setSubject(classObj.getSubject());
        response.setTeacherId(classObj.getTeacher().getId());
        response.setTeacherName(classObj.getTeacher().getFirstName() + " " + classObj.getTeacher().getLastName());
        response.setSchedule(classObj.getSchedule());
        response.setRoom(classObj.getRoom());
        response.setMaxStudents(classObj.getMaxStudents());
        response.setDescription(classObj.getDescription());
        response.setSemester(classObj.getSemester());
        response.setAcademicYear(classObj.getAcademicYear());
        response.setActive(classObj.isActive());
        response.setCreatedAt(classObj.getCreatedAt());
        response.setUpdatedAt(classObj.getUpdatedAt());
        return response;
    }
}
