package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.model.Student;
import com.sms.repository.StudentRepository;
import com.sms.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * 导出所有学生数据
     */
    @GetMapping("/students/export")
    public ResponseEntity<byte[]> exportStudents() throws IOException {
        List<Student> students = studentRepository.findAll();
        byte[] data = excelService.exportStudents(students);

        String filename = URLEncoder.encode("学生信息.xlsx", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    /**
     * 导入学生数据
     */
    @PostMapping("/students/import")
    public ApiResponse<List<Student>> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("请选择要导入的文件");
            }

            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return ApiResponse.error("请上传Excel文件(.xlsx或.xls)");
            }

            List<Student> students = excelService.importStudents(file);
            return ApiResponse.success(students);
        } catch (IOException e) {
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/students/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] data = excelService.generateTemplate();

        String filename = URLEncoder.encode("学生信息导入模板.xlsx", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }
}
