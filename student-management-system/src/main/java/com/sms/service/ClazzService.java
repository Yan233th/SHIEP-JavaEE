package com.sms.service;

import com.sms.model.Clazz;
import com.sms.repository.ClazzRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClazzService {

    @Autowired
    private ClazzRepository clazzRepository;

    public List<Clazz> findAll() {
        return clazzRepository.findAll();
    }

    public Optional<Clazz> findById(Long id) {
        return clazzRepository.findById(id);
    }

    public Optional<Clazz> findByClassCode(String classCode) {
        return clazzRepository.findByClassCode(classCode);
    }

    public List<Clazz> findByDepartmentId(Long deptId) {
        return clazzRepository.findByDepartmentId(deptId);
    }

    public List<Clazz> findByGrade(String grade) {
        return clazzRepository.findByGrade(grade);
    }

    public List<Clazz> findByDeptAndGrade(Long deptId, String grade) {
        return clazzRepository.findByDeptAndGrade(deptId, grade);
    }

    public List<String> findAllGrades() {
        return clazzRepository.findAllGrades();
    }

    public List<String> findMajorsByDeptId(Long deptId) {
        return clazzRepository.findMajorsByDeptId(deptId);
    }

    public Clazz save(Clazz clazz) {
        if (clazz.getId() == null) {
            clazz.setCreateTime(LocalDateTime.now());
        }
        return clazzRepository.save(clazz);
    }

    public void deleteById(Long id) {
        clazzRepository.deleteById(id);
    }

    public boolean existsByClassCode(String classCode) {
        return clazzRepository.existsByClassCode(classCode);
    }

    public void updateStudentCount(Long classId, int delta) {
        clazzRepository.findById(classId).ifPresent(clazz -> {
            int count = clazz.getStudentCount() == null ? 0 : clazz.getStudentCount();
            clazz.setStudentCount(Math.max(0, count + delta));
            clazzRepository.save(clazz);
        });
    }
}
