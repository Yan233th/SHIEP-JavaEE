package com.sms.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sms.dto.LoginDTO;
import com.sms.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = authService.login(dto.getUsername(), dto.getPassword());
            response.put("code", 200);
            response.put("token", token);
            response.put("username", dto.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 401);
            response.put("message", e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }
}
