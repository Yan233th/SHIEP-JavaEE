package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "uploads") String folder) {
        try {
            String url = fileService.uploadFile(file, folder);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/avatar/{userId}")
    public ApiResponse<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long userId) {
        try {
            String url = fileService.uploadAvatar(file, userId);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("头像上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ApiResponse<Void> deleteFile(@RequestParam String objectName) {
        try {
            fileService.deleteFile(objectName);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("文件删除失败: " + e.getMessage());
        }
    }
}
