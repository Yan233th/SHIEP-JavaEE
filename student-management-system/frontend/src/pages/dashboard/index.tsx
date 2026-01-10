import { useState, useEffect } from 'react';
import { Card, Col, Row, Statistic, Spin } from 'antd';
import { UserOutlined, TeamOutlined, BookOutlined } from '@ant-design/icons';
import { userApi } from '@/api/user';
import { studentApi } from '@/api/student';
import { courseApi } from '@/api/course';

interface DashboardStats {
  userCount: number;
  studentCount: number;
  courseCount: number;
}

const DashboardPage: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    userCount: 0,
    studentCount: 0,
    courseCount: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    setLoading(true);
    try {
      // 并行获取所有统计数据
      const [usersRes, studentsRes, coursesRes] = await Promise.all([
        userApi.getAll().catch(() => ({ data: [] })),
        studentApi.getAll().catch(() => ({ data: [] })),
        courseApi.getAll().catch(() => ({ data: [] })),
      ]);

      setStats({
        userCount: Array.isArray(usersRes.data) ? usersRes.data.length : 0,
        studentCount: Array.isArray(studentsRes.data) ? studentsRes.data.length : 0,
        courseCount: Array.isArray(coursesRes.data) ? coursesRes.data.length : 0,
      });
    } catch (error) {
      console.error('获取统计数据失败', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>仪表盘</h2>
      <Spin spinning={loading}>
        <Row gutter={16}>
          <Col span={8}>
            <Card>
              <Statistic
                title="用户总数"
                value={stats.userCount}
                prefix={<UserOutlined />}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="学生总数"
                value={stats.studentCount}
                prefix={<TeamOutlined />}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="课程总数"
                value={stats.courseCount}
                prefix={<BookOutlined />}
              />
            </Card>
          </Col>
        </Row>
      </Spin>
    </div>
  );
};

export default DashboardPage;
