package com.sms.controller;

import com.sms.document.AttachmentDocument;
import com.sms.document.StudentDocument;
import com.sms.dto.ApiResponse;
import com.sms.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/students")
    public ApiResponse<List<StudentDocument>> searchStudents(@RequestParam String keyword) {
        List<StudentDocument> results = searchService.searchStudents(keyword);
        return ApiResponse.success(results);
    }

    @GetMapping("/students/nickname")
    public ApiResponse<List<StudentDocument>> searchByNickname(@RequestParam String nickname) {
        List<StudentDocument> results = searchService.searchByNickname(nickname);
        return ApiResponse.success(results);
    }

    @GetMapping("/students/studentNumber")
    public ApiResponse<List<StudentDocument>> searchByStudentNumber(@RequestParam String studentNumber) {
        List<StudentDocument> results = searchService.searchByStudentNumber(studentNumber);
        return ApiResponse.success(results);
    }

    @GetMapping("/students/className")
    public ApiResponse<List<StudentDocument>> searchByClassName(@RequestParam String className) {
        List<StudentDocument> results = searchService.searchByClassName(className);
        return ApiResponse.success(results);
    }

    @GetMapping("/attachments")
    public ApiResponse<List<AttachmentDocument>> searchAttachments(@RequestParam String keyword) {
        List<AttachmentDocument> results = searchService.searchAttachments(keyword);
        return ApiResponse.success(results);
    }
}
