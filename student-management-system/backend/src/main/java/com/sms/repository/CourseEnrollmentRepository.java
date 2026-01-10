package com.sms.repository;

import com.sms.model.CourseEnrollment;
import com.sms.model.CourseEnrollment.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    // 查询学生的所有选课记录
    List<CourseEnrollment> findByStudentId(Long studentId);

    // 查询学生的指定状态选课记录
    List<CourseEnrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    // 查询课程的所有选课记录
    List<CourseEnrollment> findByCourseId(Long courseId);

    // 查询学生是否已选某门课程
    Optional<CourseEnrollment> findByStudentIdAndCourseIdAndStatus(
        Long studentId, Long courseId, EnrollmentStatus status);

    // 检查学生是否已选某门课程（任何状态）
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}
