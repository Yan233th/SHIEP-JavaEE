package com.sms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sms.model.Score;
import com.sms.repository.ScoreRepository;

@RestController
public class ScoreController {
    @Autowired
    private ScoreRepository scoreRepository;

    @GetMapping("/report-card/{studentId}")
    public String getReportCard(@PathVariable Long studentId) {
        List<Score> scores = scoreRepository.findByStudentId(studentId);

        return scores.stream()
                .map(s -> "课程：" + s.getCourse().getName() + " | 分数：" + s.getValue())
                .collect(Collectors.joining("\n"));
    }
}
