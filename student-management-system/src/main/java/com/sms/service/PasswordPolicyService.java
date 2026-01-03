package com.sms.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordPolicyService {

    // 密码强度等级
    public enum PasswordStrength {
        WEAK("弱"),
        MEDIUM("中"),
        STRONG("强");

        private final String label;

        PasswordStrength(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * 检查密码强度
     */
    public PasswordStrength checkStrength(String password) {
        if (password == null || password.length() < 6) {
            return PasswordStrength.WEAK;
        }

        int score = 0;

        // 长度检查
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // 包含小写字母
        if (Pattern.compile("[a-z]").matcher(password).find()) score++;

        // 包含大写字母
        if (Pattern.compile("[A-Z]").matcher(password).find()) score++;

        // 包含数字
        if (Pattern.compile("[0-9]").matcher(password).find()) score++;

        // 包含特殊字符
        if (Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) score++;

        if (score >= 5) {
            return PasswordStrength.STRONG;
        } else if (score >= 3) {
            return PasswordStrength.MEDIUM;
        } else {
            return PasswordStrength.WEAK;
        }
    }

    /**
     * 验证密码是否符合策略
     */
    public List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("密码不能为空");
            return errors;
        }

        if (password.length() < 6) {
            errors.add("密码长度不能少于6位");
        }

        if (password.length() > 50) {
            errors.add("密码长度不能超过50位");
        }

        if (!Pattern.compile("[a-zA-Z]").matcher(password).find()) {
            errors.add("密码必须包含字母");
        }

        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            errors.add("密码必须包含数字");
        }

        // 检查常见弱密码
        String[] weakPasswords = {"123456", "password", "12345678", "qwerty", "abc123", "111111", "123123"};
        for (String weak : weakPasswords) {
            if (password.toLowerCase().contains(weak)) {
                errors.add("密码过于简单，请使用更复杂的密码");
                break;
            }
        }

        return errors;
    }

    /**
     * 检查密码是否为强密码（困难档要求）
     */
    public boolean isStrongPassword(String password) {
        return checkStrength(password) == PasswordStrength.STRONG;
    }
}
