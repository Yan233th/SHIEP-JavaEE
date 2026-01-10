import { useState, useEffect } from 'react';
import { Card, Table, Button, Modal, Form, Input, Select, message, Space, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SendOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { userApi } from '@/api/user';
import { studentApi } from '@/api/student';
import { notificationApi } from '@/api/notification';

const { TextArea } = Input;

interface User {
  id: number;
  username: string;
  nickname?: string;
}

interface NotificationForm {
  userId: number;
  title: string;
  content: string;
  type: string;
}

const NotificationManagePage = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await studentApi.getAll();
      // 将学生数据转换为用户格式，使用学生的user.id
      const studentUsers = (res.data || []).map((student: any) => ({
        id: student.user?.id,
        username: student.studentNumber,
        nickname: student.name,
      })).filter((u: any) => u.id); // 过滤掉没有关联用户的学生
      setUsers(studentUsers);
    } catch (error) {
      message.error('获取学生列表失败');
    }
  };

  const handleSend = () => {
    form.resetFields();
    setModalVisible(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      // 构造通知对象
      const notificationData = {
        user: { id: values.userId },
        title: values.title,
        content: values.content,
        type: values.type,
      };

      await notificationApi.create(notificationData);
      setModalVisible(false);
      message.success('通知发送成功');
    } catch (error) {
      message.error('发送失败');
    }
  };

  return (
    <Card
      title="通知管理"
      extra={
        <Button type="primary" icon={<SendOutlined />} onClick={handleSend}>
          发送通知
        </Button>
      }
    >
      <Modal
        title="发送通知"
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="userId"
            label="接收用户"
            rules={[{ required: true, message: '请选择接收用户' }]}
          >
            <Select
              placeholder="请选择用户"
              showSearch
              optionFilterProp="children"
            >
              {users.map(user => (
                <Select.Option key={user.id} value={user.id}>
                  {user.nickname || user.username} ({user.username})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="type"
            label="通知类型"
            rules={[{ required: true, message: '请选择通知类型' }]}
          >
            <Select placeholder="请选择类型">
              <Select.Option value="course">课程通知</Select.Option>
              <Select.Option value="system">系统通知</Select.Option>
              <Select.Option value="grade">成绩通知</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="title"
            label="通知标题"
            rules={[{ required: true, message: '请输入通知标题' }]}
          >
            <Input placeholder="例如：课程开课通知" />
          </Form.Item>

          <Form.Item
            name="content"
            label="通知内容"
            rules={[{ required: true, message: '请输入通知内容' }]}
          >
            <TextArea rows={4} placeholder="请输入通知的详细内容" />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default NotificationManagePage;
