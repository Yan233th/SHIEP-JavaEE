package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sys_role")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
