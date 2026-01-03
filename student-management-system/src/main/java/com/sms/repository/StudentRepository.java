package com.sms.repository;

import com.sms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);

    List<Student> findByClazzId(Long classId);

    List<Student> findByDepartmentId(Long deptId);

    List<Student> findByStatus(String status);

    List<Student> findByNameContaining(String name);

    boolean existsByStudentNumber(String studentNumber);
}