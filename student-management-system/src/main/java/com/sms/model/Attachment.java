package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_attachment")
@Data
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    private Long fileSize;

    private String fileType;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime = LocalDateTime.now();
}
