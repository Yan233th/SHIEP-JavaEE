import { useState, useEffect, useRef } from 'react';
import { Table, Button, Space, Modal, Form, Input, InputNumber, message, Popconfirm, Tag, Upload } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, UploadOutlined, FileOutlined, DownloadOutlined, EyeOutlined } from '@ant-design/icons';
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

  // 预览相关状态
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewAttachment, setPreviewAttachment] = useState<any>(null);
  const videoRef = useRef<HTMLVideoElement>(null);
  const audioRef = useRef<HTMLAudioElement>(null);

  const fetchCourses = async () => {
    setLoading(true);
    try {
      const response = await courseApi.getAll();
      const courses = response.data;

      // 为每个课程加载附件数据
      const coursesWithAttachments = await Promise.all(
        courses.map(async (course) => {
          try {
            const attachmentsRes = await courseApi.getAttachments(course.id);
            return { ...course, attachments: attachmentsRes.data.data || [] };
          } catch {
            return { ...course, attachments: [] };
          }
        })
      );

      setCourses(coursesWithAttachments);
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

  // 预览附件
  const handlePreviewAttachment = (attachment: any) => {
    setPreviewAttachment(attachment);
    setPreviewVisible(true);
  };

  // 关闭预览
  const handleClosePreview = () => {
    // 暂停视频和音频
    if (videoRef.current) {
      videoRef.current.pause();
      videoRef.current.currentTime = 0;
    }
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
    }
    setPreviewVisible(false);
    setPreviewAttachment(null);
  };

  // 下载附件
  const handleDownloadAttachment = async (attachment: any) => {
    try {
      const response = await fetch(attachment.fileUrl);
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = attachment.fileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      message.success('下载成功');
    } catch {
      message.error('下载失败');
    }
  };

  // 删除附件
  const handleDeleteAttachment = async (attachmentId: number) => {
    try {
      await courseApi.deleteAttachment(attachmentId);
      message.success('删除成功');
      handleClosePreview();
      fetchCourses();
    } catch {
      message.error('删除失败');
    }
  };

  // 根据文件名判断文件类型
  const getFileTypeFromName = (fileName: string) => {
    const ext = fileName.toLowerCase().split('.').pop();
    if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext || '')) {
      return 'image';
    }
    if (ext === 'pdf') {
      return 'pdf';
    }
    if (['txt', 'log', 'md', 'json', 'xml', 'html', 'htm', 'css', 'js', 'ts', 'jsx', 'tsx'].includes(ext || '')) {
      return 'text';
    }
    if (['mp4', 'webm', 'ogg', 'avi', 'mov', 'wmv', 'flv', 'mkv'].includes(ext || '')) {
      return 'video';
    }
    if (['mp3', 'wav', 'ogg', 'aac', 'flac', 'm4a'].includes(ext || '')) {
      return 'audio';
    }
    return 'other';
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
            <Tag
              key={att.id}
              icon={<EyeOutlined />}
              style={{ cursor: 'pointer' }}
              onClick={() => handlePreviewAttachment(att)}
            >
              {att.fileName}
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

      {/* 附件预览Modal */}
      <Modal
        title="附件预览"
        open={previewVisible}
        onCancel={handleClosePreview}
        width={800}
        footer={[
          <Button
            key="download"
            type="primary"
            icon={<DownloadOutlined />}
            onClick={() => previewAttachment && handleDownloadAttachment(previewAttachment)}
          >
            下载
          </Button>,
          <Popconfirm
            key="delete"
            title="确定删除该附件吗？"
            onConfirm={() => previewAttachment && handleDeleteAttachment(previewAttachment.id)}
          >
            <Button danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>,
          <Button key="close" onClick={handleClosePreview}>
            关闭
          </Button>,
        ]}
      >
        {previewAttachment && (() => {
          const fileType = getFileTypeFromName(previewAttachment.fileName);
          return (
            <div style={{ maxHeight: '600px', overflow: 'auto' }}>
              {/* 图片预览 */}
              {fileType === 'image' && (
                <img
                  src={previewAttachment.fileUrl}
                  alt={previewAttachment.fileName}
                  style={{ width: '100%' }}
                />
              )}

              {/* PDF预览 */}
              {fileType === 'pdf' && (
                <iframe
                  src={previewAttachment.fileUrl}
                  style={{ width: '100%', height: '600px', border: 'none' }}
                  title={previewAttachment.fileName}
                />
              )}

              {/* 文本文件预览 */}
              {fileType === 'text' && (
                <div style={{ padding: '16px', background: '#f5f5f5', borderRadius: '4px' }}>
                  <iframe
                    src={previewAttachment.fileUrl}
                    style={{ width: '100%', height: '400px', border: 'none', background: 'white' }}
                    title={previewAttachment.fileName}
                  />
                </div>
              )}

              {/* 视频预览 */}
              {fileType === 'video' && (
                <video
                  ref={videoRef}
                  controls
                  style={{ width: '100%', maxHeight: '600px' }}
                  src={previewAttachment.fileUrl}
                >
                  您的浏览器不支持视频播放
                </video>
              )}

              {/* 音频预览 */}
              {fileType === 'audio' && (
                <div style={{ textAlign: 'center', padding: '40px' }}>
                  <audio
                    ref={audioRef}
                    controls
                    style={{ width: '100%' }}
                    src={previewAttachment.fileUrl}
                  >
                    您的浏览器不支持音频播放
                  </audio>
                  <p style={{ marginTop: '16px', fontSize: '16px' }}>
                    {previewAttachment.fileName}
                  </p>
                </div>
              )}

              {/* Office文档和其他文件 */}
              {fileType === 'other' && (
                <div style={{ textAlign: 'center', padding: '40px' }}>
                  <FileOutlined style={{ fontSize: '64px', color: '#1890ff' }} />
                  <p style={{ marginTop: '16px', fontSize: '16px' }}>
                    {previewAttachment.fileName}
                  </p>
                  <p style={{ color: '#999' }}>
                    此文件类型不支持在线预览，请点击下载按钮下载后查看
                  </p>
                </div>
              )}
            </div>
          );
        })()}
      </Modal>
    </div>
  );
};

export default CoursePage;
