package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.model.Student;
import com.sms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{studentNumber}")
    public ApiResponse<Student> getByStudentNumber(@PathVariable String studentNumber) {
        return studentService.getStudentByStudentNumber(studentNumber)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("学生不存在"));
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<List<Student>> getByClassId(@PathVariable Long classId) {
        return ApiResponse.success(studentService.getStudentsByClassId(classId));
    }

    @GetMapping("/dept/{deptId}")
    public ApiResponse<List<Student>> getByDeptId(@PathVariable Long deptId) {
        return ApiResponse.success(studentService.getStudentsByDeptId(deptId));
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        try {
            return ResponseEntity.ok(studentService.updateStudent(id, student));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 上传学生头像
     */
    @PostMapping("/{id}/avatar")
    public ApiResponse<String> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = studentService.uploadAvatar(id, file);
            return ApiResponse.success(avatarUrl);
        } catch (Exception e) {
            return ApiResponse.error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 重建ES搜索索引
     */
    @PostMapping("/rebuild-index")
    public ApiResponse<String> rebuildSearchIndex() {
        studentService.rebuildSearchIndex();
        return ApiResponse.success("索引重建完成");
    }
}
