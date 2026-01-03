package com.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_dict_data")
@Data
public class DictData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_code", nullable = false)
    private String typeCode;

    @Column(name = "dict_label", nullable = false)
    private String dictLabel;

    @Column(name = "dict_value", nullable = false)
    private String dictValue;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "status")
    private Integer status = 0; // 0-正常, 1-禁用

    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
