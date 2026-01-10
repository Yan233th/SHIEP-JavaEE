package com.sms.rabbitmq;

import com.sms.config.RabbitMQConfig;
import com.sms.dto.NotificationMessage;
import com.sms.model.Notification;
import com.sms.repository.NotificationRepository;
import com.sms.service.WebSocketMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @Autowired
    private NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void receiveNotification(Long notificationId) {
        try {
            log.info("收到通知消息: ID={}", notificationId);

            // 从数据库获取通知详情
            Notification notification = notificationRepository.findById(notificationId)
                    .orElse(null);

            if (notification == null) {
                log.warn("通知不存在: ID={}", notificationId);
                return;
            }

            // 转换为 DTO
            NotificationMessage message = new NotificationMessage();
            message.setTitle(notification.getTitle());
            message.setContent(notification.getContent());
            message.setType(notification.getType());
            message.setTimestamp(notification.getCreateTime());

            // 通过 WebSocket 推送给目标用户
            if (notification.getUser() != null) {
                String username = notification.getUser().getUsername();
                webSocketMessageService.sendNotificationToUser(username, message);
                log.info("通知已推送: 用户={}, 标题={}", username, notification.getTitle());
            } else {
                // 如果没有指定用户，广播给所有人
                webSocketMessageService.broadcastNotification(message);
                log.info("通知已广播: 标题={}", notification.getTitle());
            }

            log.info("通知处理完成: ID={}", notificationId);
        } catch (Exception e) {
            log.error("处理通知失败: {}", e.getMessage(), e);
        }
    }
}
