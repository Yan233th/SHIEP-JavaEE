package com.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 通知类型: course-课程, system-系统, grade-成绩
    @Column(nullable = false)
    private String type = "system";

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"notifications", "password", "roles"})
    private User user;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
