package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Table(name = "bus_schedule")
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    // 星期几 (1-7, 1=周一)
    private Integer weekDay;

    // 第几节课 (1-12)
    private Integer section;

    // 开始时间
    private LocalTime startTime;

    // 结束时间
    private LocalTime endTime;

    // 教室
    private String classroom;

    // 学期
    private String semester;
}
