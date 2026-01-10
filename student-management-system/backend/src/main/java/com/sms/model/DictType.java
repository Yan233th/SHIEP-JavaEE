package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_dict_type")
@Data
public class DictType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_code", unique = true, nullable = false)
    private String typeCode;

    @Column(name = "type_name", nullable = false)
    private String typeName;

    private String description;

    @Column(name = "status")
    private Integer status = 0; // 0-正常, 1-禁用

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
