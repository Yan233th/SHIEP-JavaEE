package com.sms.rabbitmq;

import com.sms.config.RabbitMQConfig;
import com.sms.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendNotification(Notification notification) {
        try {
            // 只发送通知ID，避免实体序列化问题
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, notification.getId());
            log.info("通知已发送到队列: ID={}, 标题={}", notification.getId(), notification.getTitle());
        } catch (Exception e) {
            log.error("发送通知到队列失败: {}", e.getMessage());
        }
    }
}
