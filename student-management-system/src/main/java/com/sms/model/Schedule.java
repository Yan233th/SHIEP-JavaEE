package com.sms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bus_schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Course course;

    @ManyToOne
    private User teacher;

    private String weekDay;
    private String section;
    private String classroom;
}
