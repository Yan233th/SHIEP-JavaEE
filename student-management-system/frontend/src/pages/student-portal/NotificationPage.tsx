import { useState, useEffect } from 'react';
import { Card, List, Badge, Button, Empty, message, Tag, Space } from 'antd';
import { BellOutlined, CheckOutlined, DeleteOutlined } from '@ant-design/icons';
import { useAuthStore } from '@/store';
import { notificationApi } from '@/api/notification';

interface Notification {
  id: number;
  title: string;
  content: string;
  type: 'course' | 'system' | 'grade';
  isRead: boolean;
  createTime: string;
}

const NotificationPage = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(false);
  const { user } = useAuthStore();

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    if (!user?.id) return;

    setLoading(true);
    try {
      const res = await notificationApi.getMyNotifications(user.id);
      setNotifications(res.data.data || []);
    } catch (error) {
      message.error('获取通知失败');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationApi.markAsRead(id);
      setNotifications(prev =>
        prev.map(n => (n.id === id ? { ...n, isRead: true } : n))
      );
      message.success('已标记为已读');
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleMarkAllAsRead = async () => {
    if (!user?.id) return;

    try {
      await notificationApi.markAllAsRead(user.id);
      setNotifications(prev =>
        prev.map(n => ({ ...n, isRead: true }))
      );
      message.success('已全部标记为已读');
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await notificationApi.delete(id);
      setNotifications(prev => prev.filter(n => n.id !== id));
      message.success('删除成功');
    } catch (error) {
      message.error('删除失败');
    }
  };

  const getTypeTag = (type: string) => {
    const typeMap = {
      course: { color: 'blue', text: '课程' },
      system: { color: 'orange', text: '系统' },
      grade: { color: 'green', text: '成绩' },
    };
    const config = typeMap[type as keyof typeof typeMap] || { color: 'default', text: '其他' };
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  const unreadCount = notifications.filter(n => !n.isRead).length;

  return (
    <Card
      title={
        <Space>
          <BellOutlined />
          <span>通知中心</span>
          {unreadCount > 0 && (
            <Badge count={unreadCount} style={{ marginLeft: 8 }} />
          )}
        </Space>
      }
      extra={
        unreadCount > 0 && (
          <Button
            type="link"
            icon={<CheckOutlined />}
            onClick={handleMarkAllAsRead}
          >
            全部标记为已读
          </Button>
        )
      }
    >
      {notifications.length === 0 ? (
        <Empty description="暂无通知" />
      ) : (
        <List
          dataSource={notifications}
          loading={loading}
          renderItem={(item) => (
            <List.Item
              style={{
                backgroundColor: item.isRead ? 'transparent' : '#f0f7ff',
                padding: '16px',
                borderRadius: '4px',
                marginBottom: '8px',
              }}
              actions={[
                !item.isRead && (
                  <Button
                    type="link"
                    size="small"
                    icon={<CheckOutlined />}
                    onClick={() => handleMarkAsRead(item.id)}
                  >
                    标记已读
                  </Button>
                ),
                <Button
                  type="link"
                  danger
                  size="small"
                  icon={<DeleteOutlined />}
                  onClick={() => handleDelete(item.id)}
                >
                  删除
                </Button>,
              ].filter(Boolean)}
            >
              <List.Item.Meta
                title={
                  <Space>
                    {getTypeTag(item.type)}
                    <span style={{ fontWeight: item.isRead ? 'normal' : 'bold' }}>
                      {item.title}
                    </span>
                    {!item.isRead && (
                      <Badge status="processing" text="未读" />
                    )}
                  </Space>
                }
                description={
                  <div>
                    <div style={{ marginBottom: 8 }}>{item.content}</div>
                    <span style={{ color: '#999', fontSize: '12px' }}>
                      {new Date(item.createTime).toLocaleString('zh-CN')}
                    </span>
                  </div>
                }
              />
            </List.Item>
          )}
        />
      )}
    </Card>
  );
};

export default NotificationPage;
