package com.sms.controller;

import com.sms.dto.NotificationMessage;
import com.sms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 处理客户端发送的消息并广播
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/notifications")
    public NotificationMessage broadcast(NotificationMessage message) {
        return message;
    }

    /**
     * 处理心跳消息
     */
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String ping() {
        return "pong";
    }
}
