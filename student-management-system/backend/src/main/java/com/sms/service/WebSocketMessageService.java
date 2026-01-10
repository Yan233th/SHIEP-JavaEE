package com.sms.service;

import com.sms.dto.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebSocketMessageService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 向指定用户推送通知
     */
    public void sendNotificationToUser(String username, NotificationMessage message) {
        try {
            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                message
            );
            log.info("WebSocket推送成功: 用户={}, 通知={}", username, message.getTitle());
        } catch (Exception e) {
            log.error("WebSocket推送失败: 用户={}, 错误={}", username, e.getMessage());
        }
    }

    /**
     * 广播通知给所有用户
     */
    public void broadcastNotification(NotificationMessage message) {
        try {
            messagingTemplate.convertAndSend("/topic/notifications", message);
            log.info("WebSocket广播成功: 通知={}", message.getTitle());
        } catch (Exception e) {
            log.error("WebSocket广播失败: 错误={}", e.getMessage());
        }
    }
}
