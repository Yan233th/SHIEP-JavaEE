import { useState, useEffect } from 'react';
import { Card, Table, Button, message, Tag, Space, Modal } from 'antd';
import { BookOutlined, DeleteOutlined } from '@ant-design/icons';
import { getMyEnrollments, dropCourse } from '@/api/enrollment';
import { useAuthStore } from '@/store';

interface Enrollment {
  id: number;
  course: {
    id: number;
    name: string;
    credits: number;
    description: string;
    semester: string;
    teacher: {
      id: number;
      username: string;
      nickname: string;
    };
  };
  enrollmentTime: string;
  status: string;
}

const MyCoursesPage = () => {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [loading, setLoading] = useState(false);
  const { user } = useAuthStore();

  useEffect(() => {
    fetchMyEnrollments();
  }, []);

  const fetchMyEnrollments = async () => {
    if (!user?.studentId) return;

    setLoading(true);
    try {
      const res = await getMyEnrollments(user.studentId);
      if (res.data.success) {
        setEnrollments(res.data.data || []);
      }
    } catch (error) {
      message.error('获取选课记录失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDrop = async (courseId: number, courseName: string) => {
    if (!user?.studentId) return;

    Modal.confirm({
      title: '确认退课',
      content: `确定要退选课程《${courseName}》吗？`,
      okText: '确定',
      cancelText: '取消',
      okType: 'danger',
      onOk: async () => {
        try {
          const res = await dropCourse(user.studentId, courseId);
          if (res.data.success) {
            message.success('退课成功');
            fetchMyEnrollments();
          } else {
            message.error(res.data.message || '退课失败');
          }
        } catch (error) {
          message.error('退课失败');
        }
      },
    });
  };

  const columns = [
    {
      title: '课程名称',
      dataIndex: ['course', 'name'],
      key: 'name',
      render: (text: string) => (
        <Space>
          <BookOutlined />
          <span style={{ fontWeight: 500 }}>{text}</span>
        </Space>
      ),
    },
    {
      title: '学分',
      dataIndex: ['course', 'credits'],
      key: 'credits',
      width: 80,
      render: (credits: number) => <Tag color="blue">{credits} 学分</Tag>,
    },
    {
      title: '授课教师',
      dataIndex: ['course', 'teacher'],
      key: 'teacher',
      render: (teacher: any) => teacher?.nickname || teacher?.username || '-',
    },
    {
      title: '学期',
      dataIndex: ['course', 'semester'],
      key: 'semester',
      width: 120,
    },
    {
      title: '选课时间',
      dataIndex: 'enrollmentTime',
      key: 'enrollmentTime',
      width: 180,
      render: (time: string) => new Date(time).toLocaleString('zh-CN'),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: any, record: Enrollment) => (
        <Button
          type="link"
          danger
          size="small"
          icon={<DeleteOutlined />}
          onClick={() => handleDrop(record.course.id, record.course.name)}
        >
          退课
        </Button>
      ),
    },
  ];

  return (
    <Card
      title="我的课程"
      bordered={false}
      extra={
        <Tag color="green">
          已选 {enrollments.length} 门课程
        </Tag>
      }
    >
      <Table
        columns={columns}
        dataSource={enrollments}
        rowKey="id"
        loading={loading}
        pagination={{
          pageSize: 10,
          showTotal: (total) => `共 ${total} 门课程`,
        }}
      />
    </Card>
  );
};

export default MyCoursesPage;
