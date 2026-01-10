package com.sms.rabbitmq;

import com.sms.config.RabbitMQConfig;
import com.sms.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void receiveNotification(Long notificationId) {
        try {
            log.info("收到通知消息: ID={}", notificationId);
            // 这里可以添加额外的处理逻辑，比如：
            // - 发送邮件
            // - 发送短信
            // - WebSocket 推送
            // - 记录日志等
            log.info("通知处理完成: ID={}", notificationId);
        } catch (Exception e) {
            log.error("处理通知失败: {}", e.getMessage());
        }
    }
}
