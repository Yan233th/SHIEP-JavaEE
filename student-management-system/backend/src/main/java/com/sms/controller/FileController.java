package com.sms.controller;

import com.sms.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxyFile(
            @RequestParam String url,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
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
            long fileSize = data.length;

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
            } else if (objectName.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (objectName.endsWith(".webm")) {
                contentType = "video/webm";
            } else if (objectName.endsWith(".ogg")) {
                contentType = "video/ogg";
            } else if (objectName.endsWith(".mp3")) {
                contentType = "audio/mpeg";
            } else if (objectName.endsWith(".wav")) {
                contentType = "audio/wav";
            }

            // 设置Content-Disposition为inline，让浏览器预览而不是下载
            // 从objectName中提取文件名
            String fileName = objectName.substring(objectName.lastIndexOf("/") + 1);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

            // 处理Range请求
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                long start = Long.parseLong(ranges[0]);
                long end = ranges.length > 1 && !ranges[1].isEmpty()
                    ? Long.parseLong(ranges[1])
                    : fileSize - 1;

                if (start >= fileSize || end >= fileSize || start > end) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                            .build();
                }

                long contentLength = end - start + 1;
                byte[] rangeData = Arrays.copyOfRange(data, (int) start, (int) end + 1);

                headers.setContentLength(contentLength);
                headers.add(HttpHeaders.CONTENT_RANGE,
                    String.format("bytes %d-%d/%d", start, end, fileSize));

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .body(rangeData);
            }

            // 没有Range请求，返回完整文件
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
