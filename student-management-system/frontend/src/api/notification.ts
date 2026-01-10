import request from '@/utils/request';

export const notificationApi = {
  // 获取用户的所有通知
  getMyNotifications: (userId: number) =>
    request.get(`/notifications/user/${userId}`),

  // 获取用户的未读通知
  getUnreadNotifications: (userId: number) =>
    request.get(`/notifications/user/${userId}/unread`),

  // 标记通知为已读
  markAsRead: (notificationId: number) =>
    request.put(`/notifications/${notificationId}/read`),

  // 标记所有通知为已读
  markAllAsRead: (userId: number) =>
    request.put(`/notifications/user/${userId}/read-all`),

  // 删除通知
  delete: (notificationId: number) =>
    request.delete(`/notifications/${notificationId}`),

  // 创建通知（管理员发送通知）
  create: (data: any) =>
    request.post('/notifications', data),
};
