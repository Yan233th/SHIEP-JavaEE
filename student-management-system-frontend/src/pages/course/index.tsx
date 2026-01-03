import { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, InputNumber, message, Popconfirm, Tag, Upload } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, UploadOutlined, FileOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { Course } from '@/types';
import { courseApi } from '@/api/course';

const { TextArea } = Input;

const CoursePage: React.FC = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingCourse, setEditingCourse] = useState<Course | null>(null);
  const [form] = Form.useForm();

  const fetchCourses = async () => {
    setLoading(true);
    try {
      const response = await courseApi.getAll();
      setCourses(response.data);
    } catch {
      message.error('获取课程列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  const handleAdd = () => {
    setEditingCourse(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Course) => {
    setEditingCourse(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await courseApi.delete(id);
      message.success('删除成功');
      fetchCourses();
    } catch {
      message.error('删除失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingCourse) {
        await courseApi.update(editingCourse.id, values);
        message.success('更新成功');
      } else {
        await courseApi.create(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      fetchCourses();
    } catch {
      message.error('操作失败');
    }
  };

  const handleUploadAttachment = async (courseId: number, file: File) => {
    try {
      await courseApi.uploadAttachment(courseId, file);
      message.success('上传成功');
      fetchCourses();
    } catch {
      message.error('上传失败');
    }
    return false;
  };

  const columns: ColumnsType<Course> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '课程名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '学分',
      dataIndex: 'credits',
      key: 'credits',
      width: 80,
    },
    {
      title: '学期',
      dataIndex: 'semester',
      key: 'semester',
    },
    {
      title: '授课教师',
      dataIndex: ['teacher', 'nickname'],
      key: 'teacher',
    },
    {
      title: '附件',
      key: 'attachments',
      render: (_, record) => (
        <Space>
          {record.attachments?.map((att) => (
            <Tag key={att.id} icon={<FileOutlined />}>
              <a href={att.fileUrl} target="_blank" rel="noopener noreferrer">
                {att.fileName}
              </a>
            </Tag>
          ))}
          <Upload
            showUploadList={false}
            beforeUpload={(file) => handleUploadAttachment(record.id, file)}
          >
            <Button size="small" icon={<UploadOutlined />} />
          </Upload>
        </Space>
      ),
    },
    {
      title: '选课人数',
      key: 'studentCount',
      render: (_, record) => record.students?.length || 0,
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
            title="确定删除该课程吗？"
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
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h2>课程管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增课程
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={courses}
        rowKey="id"
        loading={loading}
      />
      <Modal
        title={editingCourse ? '编辑课程' : '新增课程'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="课程名称"
            rules={[{ required: true, message: '请输入课程名称' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="credits"
            label="学分"
            rules={[{ required: true, message: '请输入学分' }]}
          >
            <InputNumber min={1} max={10} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="semester" label="学期">
            <Input placeholder="例如：2024-2025-1" />
          </Form.Item>
          <Form.Item name="description" label="课程描述">
            <TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default CoursePage;
