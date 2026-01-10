package com.sms.model;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String nickname;

    private String email;

    private String avatar;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    // 密码过期时间
    @Column(name = "password_expire_time")
    private LocalDateTime passwordExpireTime;

    // 密码最后修改时间
    @Column(name = "password_update_time")
    private LocalDateTime passwordUpdateTime;

    // 登录失败次数
    @Column(name = "login_fail_count")
    private Integer loginFailCount = 0;

    // 账户锁定时间
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    // 账户状态: 0-正常, 1-锁定, 2-禁用
    @Column(name = "status")
    private Integer status = 0;

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

    // 用于接收前端传递的角色ID列表（不持久化到数据库）
    @Transient
    private List<Long> roleIds;

    // 关联的学生ID（不持久化到数据库，用于前端显示）
    @Transient
    private Long studentId;

    // 检查密码是否过期
    public boolean isPasswordExpired() {
        if (passwordExpireTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(passwordExpireTime);
    }

    // 检查账户是否锁定
    public boolean isLocked() {
        if (lockTime == null) {
            return false;
        }
        // 锁定30分钟后自动解锁
        return LocalDateTime.now().isBefore(lockTime.plusMinutes(30));
    }
}
