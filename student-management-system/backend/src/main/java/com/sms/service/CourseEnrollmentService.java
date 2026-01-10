package com.sms.service;

import com.sms.dto.ApiResponse;
import com.sms.model.Course;
import com.sms.model.CourseEnrollment;
import com.sms.model.CourseEnrollment.EnrollmentStatus;
import com.sms.model.Student;
import com.sms.repository.CourseEnrollmentRepository;
import com.sms.repository.CourseRepository;
import com.sms.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseEnrollmentService {

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 学生选课
     */
    @Transactional
    public ApiResponse<CourseEnrollment> enrollCourse(Long studentId, Long courseId) {
        // 检查学生是否存在
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("学生不存在"));

        // 检查课程是否存在
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 检查是否已经选过该课程
        Optional<CourseEnrollment> existing = enrollmentRepository
            .findByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ENROLLED);

        if (existing.isPresent()) {
            return ApiResponse.error("您已经选过该课程");
        }

        // 创建选课记录
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentTime(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        CourseEnrollment saved = enrollmentRepository.save(enrollment);
        return ApiResponse.success("选课成功", saved);
    }

    /**
     * 退课
     */
    @Transactional
    public ApiResponse<Void> dropCourse(Long studentId, Long courseId) {
        Optional<CourseEnrollment> enrollment = enrollmentRepository
            .findByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ENROLLED);

        if (enrollment.isEmpty()) {
            return ApiResponse.error("未找到选课记录");
        }

        CourseEnrollment record = enrollment.get();
        record.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(record);

        return ApiResponse.success("退课成功", null);
    }

    /**
     * 查询学生的所有选课记录
     */
    public List<CourseEnrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ENROLLED);
    }

    /**
     * 查询课程的所有选课学生
     */
    public List<CourseEnrollment> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    /**
     * 检查学生是否已选某门课程
     */
    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository
            .findByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ENROLLED)
            .isPresent();
    }
}
