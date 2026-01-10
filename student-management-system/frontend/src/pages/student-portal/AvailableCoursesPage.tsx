import { useState, useEffect } from 'react';
import { Card, Table, Button, message, Space, Modal, Tag } from 'antd';
import { BookOutlined } from '@ant-design/icons';
import { getAvailableCourses, enrollCourse, dropCourse, checkEnrollment } from '@/api/enrollment';
import { useAuthStore } from '@/store';

interface Course {
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
}

const AvailableCoursesPage = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(false);
  const [enrolledCourses, setEnrolledCourses] = useState<Set<number>>(new Set());
  const { user } = useAuthStore();

  useEffect(() => {
    fetchCourses();
  }, []);

  const fetchCourses = async () => {
    setLoading(true);
    try {
      const res = await getAvailableCourses();
      // 后端直接返回课程数组
      const courseList = res.data || [];
      setCourses(courseList);
      // 检查每门课程是否已选
      await checkEnrolledCourses(courseList);
    } catch (error) {
      message.error('获取课程列表失败');
    } finally {
      setLoading(false);
    }
  };

  const checkEnrolledCourses = async (courseList: Course[]) => {
    if (!user?.studentId) return;

    const enrolled = new Set<number>();
    for (const course of courseList) {
      try {
        const res = await checkEnrollment(user.studentId, course.id);
        if (res.data.success && res.data.data) {
          enrolled.add(course.id);
        }
      } catch (error) {
        console.error('检查选课状态失败', error);
      }
    }
    setEnrolledCourses(enrolled);
  };

  const handleEnroll = async (courseId: number, courseName: string) => {
    if (!user?.studentId) {
      message.error('请先登录');
      return;
    }

    Modal.confirm({
      title: '确认选课',
      content: `确定要选择课程《${courseName}》吗？`,
      onOk: async () => {
        try {
          const res = await enrollCourse(user.studentId, courseId);
          if (res.data.success) {
            message.success('选课成功');
            setEnrolledCourses(prev => new Set(prev).add(courseId));
          } else {
            message.error(res.data.message || '选课失败');
          }
        } catch (error) {
          message.error('选课失败');
        }
      },
    });
  };

  const handleDrop = async (courseId: number, courseName: string) => {
    if (!user?.studentId) {
      message.error('请先登录');
      return;
    }

    Modal.confirm({
      title: '确认退课',
      content: `确定要退选课程《${courseName}》吗？`,
      okType: 'danger',
      onOk: async () => {
        try {
          const res = await dropCourse(user.studentId, courseId);
          if (res.data.success) {
            message.success('退课成功');
            setEnrolledCourses(prev => {
              const newSet = new Set(prev);
              newSet.delete(courseId);
              return newSet;
            });
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
      dataIndex: 'name',
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
      dataIndex: 'credits',
      key: 'credits',
      width: 80,
      render: (credits: number) => <Tag color="blue">{credits} 学分</Tag>,
    },
    {
      title: '授课教师',
      dataIndex: 'teacher',
      key: 'teacher',
      render: (teacher: any) => teacher?.nickname || teacher?.username || '-',
    },
    {
      title: '学期',
      dataIndex: 'semester',
      key: 'semester',
      width: 120,
    },
    {
      title: '课程描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_: any, record: Course) => {
        const isEnrolled = enrolledCourses.has(record.id);
        return isEnrolled ? (
          <Button
            danger
            size="small"
            onClick={() => handleDrop(record.id, record.name)}
          >
            退课
          </Button>
        ) : (
          <Button
            type="primary"
            size="small"
            onClick={() => handleEnroll(record.id, record.name)}
          >
            选课
          </Button>
        );
      },
    },
  ];

  return (
    <Card title="选课中心" bordered={false}>
      <Table
        columns={columns}
        dataSource={courses}
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

export default AvailableCoursesPage;
