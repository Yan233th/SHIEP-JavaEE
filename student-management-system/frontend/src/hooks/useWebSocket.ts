import { useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client, IMessage } from '@stomp/stompjs';

interface NotificationMessage {
  type: string;
  title: string;
  content: string;
  timestamp: string;
}

interface UseWebSocketOptions {
  onNotification?: (notification: NotificationMessage) => void;
  enabled?: boolean;
}

export const useWebSocket = (options: UseWebSocketOptions = {}) => {
  const { onNotification, enabled = true } = options;
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    if (!enabled) return;

    // 获取 JWT token
    const token = localStorage.getItem('token');

    // 创建 STOMP 客户端
    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'), // 使用相对路径，通过 Vite 代理转发
      connectHeaders: {
        Authorization: token ? `Bearer ${token}` : '',
      },
      debug: (str) => {
        console.log('[WebSocket]', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // 连接成功回调
    client.onConnect = () => {
      console.log('[WebSocket] 连接成功');

      // 订阅个人通知
      client.subscribe('/user/queue/notifications', (message: IMessage) => {
        try {
          const notification: NotificationMessage = JSON.parse(message.body);
          console.log('[WebSocket] 收到通知:', notification);
          onNotification?.(notification);
        } catch (error) {
          console.error('[WebSocket] 解析通知失败:', error);
        }
      });

      // 订阅广播通知
      client.subscribe('/topic/notifications', (message: IMessage) => {
        try {
          const notification: NotificationMessage = JSON.parse(message.body);
          console.log('[WebSocket] 收到广播通知:', notification);
          onNotification?.(notification);
        } catch (error) {
          console.error('[WebSocket] 解析广播通知失败:', error);
        }
      });
    };

    // 连接错误回调
    client.onStompError = (frame) => {
      console.error('[WebSocket] 连接错误:', frame);
    };

    // 激活连接
    client.activate();
    clientRef.current = client;

    // 清理函数
    return () => {
      if (clientRef.current) {
        console.log('[WebSocket] 断开连接');
        clientRef.current.deactivate();
      }
    };
  }, [enabled, onNotification]);

  return {
    client: clientRef.current,
  };
};
