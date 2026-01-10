package com.sms.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.sms.dto.LoginDTO;
import com.sms.dto.RegisterDTO;
import com.sms.dto.ApiResponse;
import com.sms.model.User;
import com.sms.model.Student;
import com.sms.service.AuthService;
import com.sms.service.CaptchaService;
import com.sms.service.PasswordPolicyService;
import com.sms.repository.UserRepository;
import com.sms.repository.StudentRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        Map<String, Object> result = authService.login(
                dto.getUsername(),
                dto.getPassword(),
                dto.getCaptcha(),
                dto.getCaptchaKey()
        );

        if ((boolean) result.get("success")) {
            result.put("code", 200);
            return ResponseEntity.ok(result);
        } else {
            result.put("code", 401);
            return ResponseEntity.status(401).body(result);
        }
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterDTO dto) {
        Map<String, Object> result = authService.register(dto);
        if ((boolean) result.get("success")) {
            return ApiResponse.success((String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @GetMapping("/captcha")
    public ApiResponse<Map<String, String>> getCaptcha() {
        Map<String, String> captcha = captchaService.generateCaptcha();
        return ApiResponse.success(captcha);
    }

    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return userRepository.findByUsername(userDetails.getUsername())
                .map(user -> {
                    // 查询关联的学生信息，设置studentId
                    studentRepository.findByUser_Id(user.getId())
                            .ifPresent(student -> user.setStudentId(student.getId()));
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        if (userDetails == null) {
            return ApiResponse.error("未登录");
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        Map<String, Object> result = authService.changePassword(user.getId(), oldPassword, newPassword);
        if ((boolean) result.get("success")) {
            return ApiResponse.success((String) result.get("message"));
        } else {
            return ApiResponse.error((String) result.get("message"));
        }
    }

    @GetMapping("/password-strength")
    public ApiResponse<Map<String, Object>> checkPasswordStrength(@RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("strength", passwordPolicyService.checkStrength(password).getLabel());
        result.put("errors", passwordPolicyService.validatePassword(password));
        return ApiResponse.success(result);
    }
}
