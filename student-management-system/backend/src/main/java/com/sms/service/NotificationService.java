package com.sms.service;

import com.sms.dto.NotificationMessage;
import com.sms.event.NotificationCreatedEvent;
import com.sms.model.Notification;
import com.sms.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 发送广播通知给所有用户
     */
    public void sendBroadcast(NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    /**
     * 发送通知给特定用户
     */
    public void sendToUser(String username, NotificationMessage message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }

    /**
     * 发送系统公告
     */
    public void sendAnnouncement(String title, String content) {
        NotificationMessage message = NotificationMessage.info(title, content);
        sendBroadcast(message);
    }

    /**
     * 发送操作成功通知
     */
    public void notifySuccess(String username, String title, String content) {
        NotificationMessage message = NotificationMessage.success(title, content);
        sendToUser(username, message);
    }

    /**
     * 发送警告通知
     */
    public void notifyWarning(String username, String title, String content) {
        NotificationMessage message = NotificationMessage.warning(title, content);
        sendToUser(username, message);
    }

    /**
     * 发送错误通知
     */
    public void notifyError(String username, String title, String content) {
        NotificationMessage message = NotificationMessage.error(title, content);
        sendToUser(username, message);
    }

    /**
     * 通知数据更新
     */
    public void notifyDataUpdate(String entityType, String action, Object data) {
        NotificationMessage message = new NotificationMessage();
        message.setType("data_update");
        message.setTitle(entityType + " " + action);
        message.setContent(entityType + "数据已" + action);
        message.setData(data);
        sendBroadcast(message);
    }

    // ==================== 持久化通知 CRUD 方法 ====================

    /**
     * 获取用户的所有通知
     */
    public List<Notification> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    /**
     * 获取用户的未读通知
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadOrderByCreateTimeDesc(userId, false);
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadOrderByCreateTimeDesc(userId, false);
        notifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    /**
     * 删除通知
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * 创建通知
     * 使用事件机制，确保在事务提交后才发送到 RabbitMQ
     */
    @Transactional
    public Notification createNotification(Notification notification) {
        System.out.println("=== 开始创建通知 ===");
        System.out.println("通知标题: " + notification.getTitle());
        System.out.println("通知内容: " + notification.getContent());

        Notification saved = notificationRepository.save(notification);
        System.out.println("通知已保存到数据库，ID: " + saved.getId());

        // 发布事件，事件监听器会在事务提交后发送到 RabbitMQ
        eventPublisher.publishEvent(new NotificationCreatedEvent(this, saved));
        System.out.println("事件已发布");

        return saved;
    }
}
