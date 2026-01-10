package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_course_enrollment")
@Data
public class CourseEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 选课时间
    @Column(nullable = false)
    private LocalDateTime enrollmentTime;

    // 状态: ENROLLED(已选课), DROPPED(已退课), COMPLETED(已完成)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    // 成绩（可选，课程完成后填写）
    private Double grade;

    // 备注
    private String remarks;

    public enum EnrollmentStatus {
        ENROLLED,    // 已选课
        DROPPED,     // 已退课
        COMPLETED    // 已完成
    }
}
