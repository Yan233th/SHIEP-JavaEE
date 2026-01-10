import { useState, useEffect } from 'react';
import { Layout, Menu, Dropdown, Avatar, theme, notification } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  BookOutlined,
  CalendarOutlined,
  BellOutlined,
  UserOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/store';
import { useWebSocket } from '@/hooks/useWebSocket';
import type { MenuProps } from 'antd';

const { Header, Sider, Content } = Layout;

const StudentLayout = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuthStore();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  // WebSocket 实时通知
  useWebSocket({
    enabled: !!user, // 只有登录用户才连接
    onNotification: (notif) => {
      notification.info({
        message: notif.title,
        description: notif.content,
        placement: 'topRight',
        duration: 4.5,
      });
    },
  });

  // 检查用户是否是管理员
  const isAdmin = user?.roles?.some(role =>
    role.name === 'ADMIN' || role.name === '管理员'
  );

  // 如果是管理员，重定向到管理系统
  useEffect(() => {
    if (isAdmin) {
      navigate('/', { replace: true });
    }
  }, [isAdmin, navigate]);

  // 动态生成菜单项
  const menuItems: MenuProps['items'] = [
    {
      key: '/portal/courses',
      icon: <BookOutlined />,
      label: '选课中心',
    },
    {
      key: '/portal/my-courses',
      icon: <BookOutlined />,
      label: '我的课程',
    },
    {
      key: '/portal/schedule',
      icon: <CalendarOutlined />,
      label: '我的课表',
    },
    {
      key: '/portal/notifications',
      icon: <BellOutlined />,
      label: '通知中心',
    },
  ];

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div
          style={{
            height: 32,
            margin: 16,
            color: 'white',
            fontSize: 18,
            fontWeight: 'bold',
            textAlign: 'center',
          }}
        >
          {collapsed ? '学生' : '学生选课系统'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 16px', background: colorBgContainer, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div
            onClick={() => setCollapsed(!collapsed)}
            style={{ fontSize: 18, cursor: 'pointer' }}
          >
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </div>
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Avatar src={user?.avatar} icon={<UserOutlined />} />
              <span>{user?.username || '学生'}</span>
            </div>
          </Dropdown>
        </Header>
        <Content
          style={{
            margin: '24px 16px',
            padding: 24,
            minHeight: 280,
            background: colorBgContainer,
            borderRadius: borderRadiusLG,
          }}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default StudentLayout;
