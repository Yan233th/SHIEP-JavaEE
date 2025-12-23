package com.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sms.model.Course;
import com.sms.repository.CourseRepository;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public List<Course> list() {
        return courseRepository.findAll();
    }

    @PostMapping
    public Course create(@RequestBody Course course) {
        return courseRepository.save(course);
    }
}
