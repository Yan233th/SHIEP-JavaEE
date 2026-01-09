package com.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sms.dto.ApiResponse;
import com.sms.model.Attachment;
import com.sms.model.Course;
import com.sms.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            return ResponseEntity.ok(courseService.updateCourse(id, course));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseId}/enroll/{studentId}")
    public ResponseEntity<String> enrollStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        try {
            courseService.enrollStudent(courseId, studentId);
            return ResponseEntity.ok("Student enrolled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/enroll/{studentId}")
    public ResponseEntity<String> unenrollStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        try {
            courseService.unenrollStudent(courseId, studentId);
            return ResponseEntity.ok("Student unenrolled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 上传课程附件
     */
    @PostMapping("/{courseId}/attachments")
    public ApiResponse<Attachment> uploadAttachment(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = courseService.uploadAttachment(courseId, file);
            return ApiResponse.success(attachment);
        } catch (Exception e) {
            return ApiResponse.error("附件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程附件列表
     */
    @GetMapping("/{courseId}/attachments")
    public ApiResponse<List<Attachment>> getCourseAttachments(@PathVariable Long courseId) {
        List<Attachment> attachments = courseService.getCourseAttachments(courseId);
        return ApiResponse.success(attachments);
    }

    /**
     * 删除课程附件
     */
    @DeleteMapping("/attachments/{attachmentId}")
    public ApiResponse<String> deleteAttachment(@PathVariable Long attachmentId) {
        try {
            courseService.deleteAttachment(attachmentId);
            return ApiResponse.success("附件删除成功");
        } catch (Exception e) {
            return ApiResponse.error("附件删除失败: " + e.getMessage());
        }
    }
}
