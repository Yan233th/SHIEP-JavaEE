package com.sms.controller;

import com.sms.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxyFile(@RequestParam String url) {
        try {
            String path = url.replace("http://localhost:42003/", "")
                            .replace("http://minio:9000/", "");
            String[] parts = path.split("/", 2);
            if (parts.length < 2) {
                return ResponseEntity.badRequest().build();
            }

            String bucketName = parts[0];
            String objectName = parts[1];

            byte[] data = fileService.downloadFile(bucketName, objectName);

            // 根据文件扩展名设置Content-Type
            String contentType = "application/octet-stream";
            if (objectName.endsWith(".png")) {
                contentType = "image/png";
            } else if (objectName.endsWith(".jpg") || objectName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (objectName.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (objectName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (objectName.endsWith(".txt")) {
                contentType = "text/plain; charset=utf-8";
            } else if (objectName.endsWith(".html") || objectName.endsWith(".htm")) {
                contentType = "text/html; charset=utf-8";
            } else if (objectName.endsWith(".json")) {
                contentType = "application/json; charset=utf-8";
            } else if (objectName.endsWith(".xml")) {
                contentType = "application/xml; charset=utf-8";
            }

            // 设置Content-Disposition为inline，让浏览器预览而不是下载
            // 从objectName中提取文件名
            String fileName = objectName.substring(objectName.lastIndexOf("/") + 1);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
