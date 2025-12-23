package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bus_course")
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer credits;
}
