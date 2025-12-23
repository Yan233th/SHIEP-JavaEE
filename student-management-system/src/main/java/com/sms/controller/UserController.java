package com.sms.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sms.dto.UserDTO;
import com.sms.model.*;
import com.sms.repository.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/test")
    public User createTestUser() {
        User user = new User();
        user.setUsername("test_user_" + System.currentTimeMillis());
        user.setPassword("123456");
        user.setNickname("testing");
        return userRepository.save(user);
    }

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/{userId}/assign-role/{roleId}")
    public String assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();

        user.getRoles().add(role); // 链式操作：直接往集合里塞
        userRepository.save(user); // JPA 会自动在 sys_user_role 表插入记录

        return "权限分配成功！";
    }

    @PostMapping("/resgiter")
    public User register(@RequestBody UserDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return userRepository.save(user);
    }
}
