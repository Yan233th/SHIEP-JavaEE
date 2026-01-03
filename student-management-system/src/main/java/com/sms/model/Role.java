package com.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sys_role_menu",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    @JsonIgnoreProperties("roles")
    private Set<Menu> menus = new HashSet<>();
}
