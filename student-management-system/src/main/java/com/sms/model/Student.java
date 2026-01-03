package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "edu_student")
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_number", unique = true)
    private String studentNumber;

    private String name;

    // 性别: 1-男, 2-女 (使用数据字典)
    private String gender;

    // 民族 (使用数据字典)
    private String nation;

    // 政治面貌 (使用数据字典)
    private String political;

    @Column(name = "id_card")
    private String idCard; // 身份证号

    private String phone;

    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String address;

    // 头像URL
    private String avatar;

    // 入学日期
    @Column(name = "enroll_date")
    private LocalDate enrollDate;

    // 学生状态 (使用数据字典)
    private String status;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Clazz clazz; // 班级

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department; // 院系

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses = new HashSet<>();

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    private String remark;
}
