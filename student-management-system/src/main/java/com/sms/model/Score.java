package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bus_score", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_id", "course_id" })
})
@Data
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double value;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
