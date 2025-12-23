package com.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sms.model.Schedule;
import com.sms.repository.ScheduleRepository;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/teacher/{teacherId}")
    public List<Schedule> getByTeacher(@PathVariable Long teacherId) {
        return scheduleRepository.findByTeacherId(teacherId);
    }
}
