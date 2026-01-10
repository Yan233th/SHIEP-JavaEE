package com.sms.mapper;

import org.mapstruct.*;

import com.sms.dto.EnrollmentDTO;
import com.sms.model.Score;

@Mapper(componentModel = "spring")
public interface ScoreMapper {
    @Mapping(source = "studentId", target = "student.id")
    @Mapping(source = "courseId", target = "course.id")
    Score toEntity(EnrollmentDTO dto);
}
