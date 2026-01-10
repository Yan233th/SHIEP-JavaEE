package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.model.Notification;
import com.sms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取用户的所有通知
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Notification>> getMyNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getMyNotifications(userId);
        return ApiResponse.success(notifications);
    }

    /**
     * 获取用户的未读通知
     */
    @GetMapping("/user/{userId}/unread")
    public ApiResponse<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ApiResponse.success(notifications);
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("标记成功", null);
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/user/{userId}/read-all")
    public ApiResponse<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ApiResponse.success("全部标记成功", null);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 创建通知（管理员发送通知）
     */
    @PostMapping
    public ApiResponse<Notification> createNotification(@RequestBody Notification notification) {
        Notification created = notificationService.createNotification(notification);
        return ApiResponse.success("通知发送成功", created);
    }
}
