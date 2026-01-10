package com.sms.dto;

import java.time.LocalDateTime;

public class NotificationMessage {

    private String type;
    private String title;
    private String content;
    private Long targetUserId;
    private LocalDateTime timestamp;
    private Object data;

    public NotificationMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public NotificationMessage(String type, String title, String content) {
        this();
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public static NotificationMessage info(String title, String content) {
        return new NotificationMessage("info", title, content);
    }

    public static NotificationMessage success(String title, String content) {
        return new NotificationMessage("success", title, content);
    }

    public static NotificationMessage warning(String title, String content) {
        return new NotificationMessage("warning", title, content);
    }

    public static NotificationMessage error(String title, String content) {
        return new NotificationMessage("error", title, content);
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
