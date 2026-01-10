package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.model.CourseEnrollment;
import com.sms.service.CourseEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private CourseEnrollmentService enrollmentService;

    /**
     * 学生选课
     */
    @PostMapping("/enroll")
    public ApiResponse<CourseEnrollment> enrollCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        return enrollmentService.enrollCourse(studentId, courseId);
    }

    /**
     * 学生退课
     */
    @PostMapping("/drop")
    public ApiResponse<Void> dropCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        return enrollmentService.dropCourse(studentId, courseId);
    }

    /**
     * 查询学生的选课记录
     */
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<CourseEnrollment>> getStudentEnrollments(
            @PathVariable Long studentId) {
        List<CourseEnrollment> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ApiResponse.success(enrollments);
    }

    /**
     * 查询课程的选课学生
     */
    @GetMapping("/course/{courseId}")
    public ApiResponse<List<CourseEnrollment>> getCourseEnrollments(
            @PathVariable Long courseId) {
        List<CourseEnrollment> enrollments = enrollmentService.getCourseEnrollments(courseId);
        return ApiResponse.success(enrollments);
    }

    /**
     * 检查学生是否已选某门课程
     */
    @GetMapping("/check")
    public ApiResponse<Boolean> checkEnrollment(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        boolean isEnrolled = enrollmentService.isEnrolled(studentId, courseId);
        return ApiResponse.success(isEnrolled);
    }
}
