package com.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sms.dto.EnrollmentDTO;
import com.sms.mapper.ScoreMapper;
import com.sms.model.Score;
import com.sms.repository.ScoreRepository;

@RestController
@RequestMapping("/api/enroll")
public class EnrollmentController {
    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ScoreMapper scoreMapper;

    @PostMapping
    public Score enroll(@RequestBody EnrollmentDTO dto) {
        Score score = scoreMapper.toEntity(dto);
        return scoreRepository.save(score);
    }
}
