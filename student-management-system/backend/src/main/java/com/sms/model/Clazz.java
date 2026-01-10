package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "edu_class")
@Data
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "class_code", unique = true)
    private String classCode;

    @Column(name = "grade")
    private String grade; // 年级，如 2024

    @Column(name = "major")
    private String major; // 专业

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department; // 所属院系

    @Column(name = "teacher_name")
    private String teacherName; // 班主任

    @Column(name = "student_count")
    private Integer studentCount = 0;

    @Column(name = "status")
    private Integer status = 0; // 0-正常, 1-已毕业

    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
