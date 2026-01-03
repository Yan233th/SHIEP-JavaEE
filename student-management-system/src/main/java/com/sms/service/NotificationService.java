package com.sms.service;

import com.sms.dto.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
}
