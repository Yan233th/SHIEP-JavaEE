import { useState, useEffect } from 'react';
import { Card, Table, Button, Modal, Form, Input, Select, InputNumber, message, Space, Popconfirm, TimePicker } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import * as scheduleApi from '@/api/schedule';
import { courseApi } from '@/api/course';
import dayjs from 'dayjs';

interface Course {
  id: number;
  name: string;
  teacher?: {
    id: number;
    username: string;
    nickname: string;
  };
}

interface Schedule {
  id: number;
  course: Course;
  teacher: {
    id: number;
    username: string;
    nickname: string;
  };
  weekDay: number;
  section: number;
  startTime: string;
  endTime: string;
  classroom: string;
  semester: string;
}

const weekDayMap: { [key: number]: string } = {
  1: '周一',
  2: '周二',
  3: '周三',
  4: '周四',
  5: '周五',
  6: '周六',
  7: '周日',
};

const SchedulePage = () => {
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingSchedule, setEditingSchedule] = useState<Schedule | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchSchedules();
    fetchCourses();
  }, []);

  const fetchSchedules = async () => {
    setLoading(true);
    try {
      const response = await scheduleApi.getAllSchedules();
      setSchedules(response.data.data || []);
    } catch (error) {
      message.error('获取课表列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchCourses = async () => {
    try {
      const response = await courseApi.getAll();
      setCourses(response.data || []);
    } catch (error) {
      message.error('获取课程列表失败');
    }
  };

  const handleAdd = () => {
    setEditingSchedule(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Schedule) => {
    setEditingSchedule(record);
    form.setFieldsValue({
      courseId: record.course.id,
      semester: record.semester,
      weekDay: record.weekDay,
      section: record.section,
      startTime: record.startTime ? dayjs(record.startTime, 'HH:mm') : null,
      endTime: record.endTime ? dayjs(record.endTime, 'HH:mm') : null,
      classroom: record.classroom,
    });
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await scheduleApi.deleteSchedule(id);
      message.success('删除成功');
      fetchSchedules();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      const submitData = {
        course: { id: values.courseId },
        semester: values.semester,
        weekDay: values.weekDay,
        section: values.section,
        startTime: values.startTime ? values.startTime.format('HH:mm') : null,
        endTime: values.endTime ? values.endTime.format('HH:mm') : null,
        classroom: values.classroom,
      };

      if (editingSchedule) {
        await scheduleApi.updateSchedule(editingSchedule.id, submitData);
        message.success('更新成功');
      } else {
        await scheduleApi.createSchedule(submitData);
        message.success('创建成功');
      }

      setModalVisible(false);
      fetchSchedules();
    } catch (error) {
      message.error('操作失败');
    }
  };

  const columns: ColumnsType<Schedule> = [
    {
      title: '课程名称',
      dataIndex: ['course', 'name'],
      key: 'courseName',
    },
    {
      title: '授课教师',
      dataIndex: ['teacher', 'nickname'],
      key: 'teacher',
      render: (text, record) => record.teacher?.nickname || record.teacher?.username || '-',
    },
    {
      title: '学期',
      dataIndex: 'semester',
      key: 'semester',
      width: 120,
    },
    {
      title: '星期',
      dataIndex: 'weekDay',
      key: 'weekDay',
      width: 80,
      render: (weekDay: number) => weekDayMap[weekDay] || '-',
    },
    {
      title: '节次',
      dataIndex: 'section',
      key: 'section',
      width: 80,
      render: (section: number) => `第${section}节`,
    },
    {
      title: '上课时间',
      key: 'time',
      width: 150,
      render: (_, record) => `${record.startTime || '-'} ~ ${record.endTime || '-'}`,
    },
    {
      title: '教室',
      dataIndex: 'classroom',
      key: 'classroom',
      width: 120,
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除该课表吗？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card
      title="课表管理"
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增课表
        </Button>
      }
    >
      <Table
        columns={columns}
        dataSource={schedules}
        rowKey="id"
        loading={loading}
      />

      <Modal
        title={editingSchedule ? '编辑课表' : '新增课表'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="courseId"
            label="课程"
            rules={[{ required: true, message: '请选择课程' }]}
          >
            <Select
              placeholder="请选择课程"
              showSearch
              optionFilterProp="children"
            >
              {courses.map(course => (
                <Select.Option key={course.id} value={course.id}>
                  {course.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="semester"
            label="学期"
            rules={[{ required: true, message: '请输入学期' }]}
          >
            <Input placeholder="例如：2024-2025-1" />
          </Form.Item>

          <Form.Item
            name="weekDay"
            label="星期"
            rules={[{ required: true, message: '请选择星期' }]}
          >
            <Select placeholder="请选择星期">
              {Object.entries(weekDayMap).map(([key, value]) => (
                <Select.Option key={key} value={Number(key)}>
                  {value}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="section"
            label="节次"
            rules={[{ required: true, message: '请输入节次' }]}
          >
            <InputNumber min={1} max={12} placeholder="第几节课" style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="startTime"
            label="开始时间"
            rules={[{ required: true, message: '请选择开始时间' }]}
          >
            <TimePicker format="HH:mm" style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="endTime"
            label="结束时间"
            rules={[{ required: true, message: '请选择结束时间' }]}
          >
            <TimePicker format="HH:mm" style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="classroom"
            label="教室"
            rules={[{ required: true, message: '请输入教室' }]}
          >
            <Input placeholder="例如：A101" />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default SchedulePage;
