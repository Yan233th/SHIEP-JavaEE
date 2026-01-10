package com.sms.service;

import com.sms.dto.RegisterDTO;
import com.sms.model.User;
import com.sms.repository.UserRepository;
import com.sms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int PASSWORD_EXPIRE_DAYS = 90;

    /**
     * 登录（带密码安全策略）
     */
    @Transactional
    public Map<String, Object> login(String username, String password, String captcha, String captchaKey) {
        Map<String, Object> result = new HashMap<>();

        // 验证验证码
        if (captcha != null && captchaKey != null) {
            if (!captchaService.verifyCaptcha(captchaKey, captcha)) {
                result.put("success", false);
                result.put("message", "验证码错误");
                return result;
            }
        }

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        // 检查账户是否锁定
        if (user.isLocked()) {
            result.put("success", false);
            result.put("message", "账户已锁定，请30分钟后再试");
            return result;
        }

        // 检查账户状态
        if (user.getStatus() != null && user.getStatus() == 2) {
            result.put("success", false);
            result.put("message", "账户已禁用");
            return result;
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 增加失败次数
            int failCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
            user.setLoginFailCount(failCount);

            if (failCount >= MAX_LOGIN_FAIL_COUNT) {
                user.setLockTime(LocalDateTime.now());
                user.setStatus(1);
                userRepository.save(user);
                result.put("success", false);
                result.put("message", "密码错误次数过多，账户已锁定30分钟");
                return result;
            }

            userRepository.save(user);
            result.put("success", false);
            result.put("message", "密码错误，还剩" + (MAX_LOGIN_FAIL_COUNT - failCount) + "次机会");
            return result;
        }

        // 登录成功，重置失败次数
        user.setLoginFailCount(0);
        user.setLockTime(null);
        if (user.getStatus() != null && user.getStatus() == 1) {
            user.setStatus(0);
        }
        userRepository.save(user);

        // 检查密码是否过期
        if (user.isPasswordExpired()) {
            result.put("success", false);
            result.put("message", "密码已过期，请修改密码");
            result.put("passwordExpired", true);
            return result;
        }

        String token = jwtUtil.generateToken(username, user.getId());
        result.put("success", true);
        result.put("token", token);
        result.put("username", username);
        return result;
    }

    /**
     * 简单登录（兼容原有接口）
     */
    public String login(String username, String password) {
        Map<String, Object> result = login(username, password, null, null);
        if ((boolean) result.get("success")) {
            return (String) result.get("token");
        }
        throw new RuntimeException((String) result.get("message"));
    }

    /**
     * 注册
     */
    @Transactional
    public Map<String, Object> register(RegisterDTO dto) {
        Map<String, Object> result = new HashMap<>();

        // 验证验证码
        if (dto.getCaptcha() != null && dto.getCaptchaKey() != null) {
            if (!captchaService.verifyCaptcha(dto.getCaptchaKey(), dto.getCaptcha())) {
                result.put("success", false);
                result.put("message", "验证码错误");
                return result;
            }
        }

        // 检查用户名是否已存在
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }

        // 检查两次密码是否一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.put("success", false);
            result.put("message", "两次密码不一致");
            return result;
        }

        // 验证密码强度
        List<String> passwordErrors = passwordPolicyService.validatePassword(dto.getPassword());
        if (!passwordErrors.isEmpty()) {
            result.put("success", false);
            result.put("message", String.join("; ", passwordErrors));
            return result;
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setCreateTime(LocalDateTime.now());
        user.setPasswordUpdateTime(LocalDateTime.now());
        user.setPasswordExpireTime(LocalDateTime.now().plusDays(PASSWORD_EXPIRE_DAYS));
        user.setLoginFailCount(0);
        user.setStatus(0);

        userRepository.save(user);

        result.put("success", true);
        result.put("message", "注册成功");
        return result;
    }

    /**
     * 修改密码
     */
    @Transactional
    public Map<String, Object> changePassword(Long userId, String oldPassword, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            result.put("success", false);
            result.put("message", "原密码错误");
            return result;
        }

        // 验证新密码强度
        List<String> passwordErrors = passwordPolicyService.validatePassword(newPassword);
        if (!passwordErrors.isEmpty()) {
            result.put("success", false);
            result.put("message", String.join("; ", passwordErrors));
            return result;
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordUpdateTime(LocalDateTime.now());
        user.setPasswordExpireTime(LocalDateTime.now().plusDays(PASSWORD_EXPIRE_DAYS));
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "密码修改成功");
        return result;
    }
}
