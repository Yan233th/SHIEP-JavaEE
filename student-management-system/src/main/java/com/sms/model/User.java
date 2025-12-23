package com.sms.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nickname;

    private String email;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sys_user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
