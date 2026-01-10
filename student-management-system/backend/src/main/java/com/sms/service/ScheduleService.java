package com.sms.service;

import com.sms.dto.ApiResponse;
import com.sms.model.Schedule;
import com.sms.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 创建课表
     */
    @Transactional
    public ApiResponse<Schedule> createSchedule(Schedule schedule) {
        Schedule saved = scheduleRepository.save(schedule);
        return ApiResponse.success("课表创建成功", saved);
    }

    /**
     * 更新课表
     */
    @Transactional
    public ApiResponse<Schedule> updateSchedule(Long id, Schedule schedule) {
        if (!scheduleRepository.existsById(id)) {
            return ApiResponse.error("课表不存在");
        }
        schedule.setId(id);
        Schedule updated = scheduleRepository.save(schedule);
        return ApiResponse.success("课表更新成功", updated);
    }

    /**
     * 删除课表
     */
    @Transactional
    public ApiResponse<Void> deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            return ApiResponse.error("课表不存在");
        }
        scheduleRepository.deleteById(id);
        return ApiResponse.success("课表删除成功", null);
    }

    /**
     * 查询所有课表
     */
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    /**
     * 按教师查询课表
     */
    public List<Schedule> getSchedulesByTeacher(Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId);
    }

    /**
     * 按课程查询课表
     */
    public List<Schedule> getSchedulesByCourse(Long courseId) {
        return scheduleRepository.findByCourseId(courseId);
    }

    /**
     * 按学期查询课表
     */
    public List<Schedule> getSchedulesBySemester(String semester) {
        return scheduleRepository.findBySemester(semester);
    }

    /**
     * 查询学生的课表
     */
    public List<Schedule> getStudentSchedules(Long studentId, String semester) {
        return scheduleRepository.findByStudentIdAndSemester(studentId, semester);
    }
}
