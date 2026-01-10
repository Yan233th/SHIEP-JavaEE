package com.sms.event;

import com.sms.model.Notification;
import com.sms.rabbitmq.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationProducer notificationProducer;

    /**
     * 监听通知创建事件，在事务提交后发送到 RabbitMQ
     * 这样可以确保数据库中的通知记录已经可见，消费者查询时不会出现"通知不存在"的问题
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        System.out.println("=== 事件监听器被触发 ===");
        Notification notification = event.getNotification();
        System.out.println("事务已提交，准备发送通知到 RabbitMQ");
        System.out.println("通知 ID: " + notification.getId());
        System.out.println("通知标题: " + notification.getTitle());

        log.info("事务已提交，发送通知到 RabbitMQ: ID={}, Title={}",
                notification.getId(), notification.getTitle());

        try {
            notificationProducer.sendNotification(notification);
            System.out.println("已发送到 RabbitMQ");
        } catch (Exception e) {
            System.out.println("发送到 RabbitMQ 失败: " + e.getMessage());
            log.error("发送通知到 RabbitMQ 失败: ID={}", notification.getId(), e);
        }
    }
}
