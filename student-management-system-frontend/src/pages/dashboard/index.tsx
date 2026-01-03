import { Card, Col, Row, Statistic } from 'antd';
import { UserOutlined, TeamOutlined, BookOutlined } from '@ant-design/icons';

const DashboardPage: React.FC = () => {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>仪表盘</h2>
      <Row gutter={16}>
        <Col span={8}>
          <Card>
            <Statistic
              title="用户总数"
              value={0}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="学生总数"
              value={0}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="课程总数"
              value={0}
              prefix={<BookOutlined />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardPage;
