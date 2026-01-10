package com.sms.controller;

import java.util.List;

import com.sms.dto.ApiResponse;
import com.sms.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sms.model.Schedule;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 创建课表
     */
    @PostMapping
    public ApiResponse<Schedule> createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.createSchedule(schedule);
    }

    /**
     * 更新课表
     */
    @PutMapping("/{id}")
    public ApiResponse<Schedule> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
        return scheduleService.updateSchedule(id, schedule);
    }

    /**
     * 删除课表
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSchedule(@PathVariable Long id) {
        return scheduleService.deleteSchedule(id);
    }

    /**
     * 查询所有课表
     */
    @GetMapping
    public ApiResponse<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ApiResponse.success(schedules);
    }

    /**
     * 按教师查询课表
     */
    @GetMapping("/teacher/{teacherId}")
    public ApiResponse<List<Schedule>> getSchedulesByTeacher(@PathVariable Long teacherId) {
        List<Schedule> schedules = scheduleService.getSchedulesByTeacher(teacherId);
        return ApiResponse.success(schedules);
    }

    /**
     * 按课程查询课表
     */
    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Schedule>> getSchedulesByCourse(@PathVariable Long courseId) {
        List<Schedule> schedules = scheduleService.getSchedulesByCourse(courseId);
        return ApiResponse.success(schedules);
    }

    /**
     * 按学期查询课表
     */
    @GetMapping("/semester/{semester}")
    public ApiResponse<List<Schedule>> getSchedulesBySemester(@PathVariable String semester) {
        List<Schedule> schedules = scheduleService.getSchedulesBySemester(semester);
        return ApiResponse.success(schedules);
    }

    /**
     * 查询学生的课表
     */
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Schedule>> getStudentSchedules(
            @PathVariable Long studentId,
            @RequestParam String semester) {
        List<Schedule> schedules = scheduleService.getStudentSchedules(studentId, semester);
        return ApiResponse.success(schedules);
    }
}
