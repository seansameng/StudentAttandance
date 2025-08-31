package com.example.studentattendance.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studentattendance.models.Class;
import com.example.studentattendance.models.User;
import com.example.studentattendance.repositories.ClassRepository;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    public List<Class> findAll() {
        return classRepository.findAll();
    }

    public List<Class> findActiveClasses() {
        return classRepository.findByIsActiveTrue();
    }

    public List<Class> findByTeacher(User teacher) {
        return classRepository.findByTeacher(teacher);
    }

    public List<Class> findByTeacherId(Long teacherId) {
        return classRepository.findByTeacherId(teacherId);
    }

    public List<Class> findUpcomingClasses(LocalDate startDate, LocalDate endDate) {
        return classRepository.findUpcomingClasses(startDate, endDate);
    }

    public Optional<Class> findById(Long id) {
        return classRepository.findById(id);
    }

    public Class save(Class classObj) {
        return classRepository.save(classObj);
    }

    public void deleteById(Long id) {
        classRepository.deleteById(id);
    }

    public long countAll() {
        return classRepository.count();
    }

    public long countActiveClasses() {
        return classRepository.countByIsActiveTrue();
    }

    public long countClassesByTeacher(Long teacherId) {
        return classRepository.countByTeacherId(teacherId);
    }

    public List<Class> searchClasses(String searchTerm) {
        return classRepository.searchClasses(searchTerm);
    }

    public List<Class> findBySemesterAndAcademicYear(String semester, String academicYear) {
        return classRepository.findBySemesterAndAcademicYear(semester, academicYear);
    }
}
