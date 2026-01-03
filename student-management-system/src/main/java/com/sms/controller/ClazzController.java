package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.model.Clazz;
import com.sms.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @GetMapping
    public ApiResponse<List<Clazz>> getAll() {
        return ApiResponse.success(clazzService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Clazz> getById(@PathVariable Long id) {
        return clazzService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("班级不存在"));
    }

    @GetMapping("/code/{classCode}")
    public ApiResponse<Clazz> getByClassCode(@PathVariable String classCode) {
        return clazzService.findByClassCode(classCode)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("班级不存在"));
    }

    @GetMapping("/dept/{deptId}")
    public ApiResponse<List<Clazz>> getByDepartment(@PathVariable Long deptId) {
        return ApiResponse.success(clazzService.findByDepartmentId(deptId));
    }

    @GetMapping("/grade/{grade}")
    public ApiResponse<List<Clazz>> getByGrade(@PathVariable String grade) {
        return ApiResponse.success(clazzService.findByGrade(grade));
    }

    @GetMapping("/dept/{deptId}/grade/{grade}")
    public ApiResponse<List<Clazz>> getByDeptAndGrade(@PathVariable Long deptId, @PathVariable String grade) {
        return ApiResponse.success(clazzService.findByDeptAndGrade(deptId, grade));
    }

    @GetMapping("/grades")
    public ApiResponse<List<String>> getAllGrades() {
        return ApiResponse.success(clazzService.findAllGrades());
    }

    @GetMapping("/majors/{deptId}")
    public ApiResponse<List<String>> getMajorsByDept(@PathVariable Long deptId) {
        return ApiResponse.success(clazzService.findMajorsByDeptId(deptId));
    }

    @PostMapping
    public ApiResponse<Clazz> create(@RequestBody Clazz clazz) {
        if (clazz.getClassCode() != null && clazzService.existsByClassCode(clazz.getClassCode())) {
            return ApiResponse.error("班级编码已存在");
        }
        return ApiResponse.success(clazzService.save(clazz));
    }

    @PutMapping("/{id}")
    public ApiResponse<Clazz> update(@PathVariable Long id, @RequestBody Clazz clazz) {
        if (!clazzService.findById(id).isPresent()) {
            return ApiResponse.error("班级不存在");
        }
        clazz.setId(id);
        return ApiResponse.success(clazzService.save(clazz));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        clazzService.deleteById(id);
        return ApiResponse.success("删除成功");
    }
}
