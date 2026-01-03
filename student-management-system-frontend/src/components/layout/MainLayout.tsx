import { useState } from 'react';
import { Layout, Menu, Dropdown, Avatar, theme } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  UserOutlined,
  TeamOutlined,
  BookOutlined,
  SettingOutlined,
  LogoutOutlined,
  SearchOutlined,
  FileExcelOutlined,
  CloudUploadOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/store';
import type { MenuProps } from 'antd';

const { Header, Sider, Content } = Layout;

const menuItems: MenuProps['items'] = [
  {
    key: '/',
    icon: <DashboardOutlined />,
    label: '仪表盘',
  },
  {
    key: '/system',
    icon: <SettingOutlined />,
    label: '系统管理',
    children: [
      { key: '/system/user', label: '用户管理' },
      { key: '/system/role', label: '角色管理' },
      { key: '/system/menu', label: '菜单管理' },
      { key: '/system/department', label: '部门管理' },
    ],
  },
  {
    key: '/student',
    icon: <TeamOutlined />,
    label: '学生管理',
  },
  {
    key: '/course',
    icon: <BookOutlined />,
    label: '课程管理',
  },
  {
    key: '/search',
    icon: <SearchOutlined />,
    label: '全文搜索',
  },
  {
    key: '/excel',
    icon: <FileExcelOutlined />,
    label: 'Excel导入导出',
  },
  {
    key: '/file',
    icon: <CloudUploadOutlined />,
    label: '文件上传',
  },
];

const MainLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuthStore();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const handleMenuClick = (e: { key: string }) => {
    navigate(e.key);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人信息',
    },
    {
      type: 'divider',
    },
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
        <div style={{
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontSize: collapsed ? 16 : 18,
          fontWeight: 'bold',
        }}>
          {collapsed ? 'SMS' : '学生管理系统'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          defaultOpenKeys={['/system']}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header style={{
          padding: '0 16px',
          background: colorBgContainer,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}>
          <div
            onClick={() => setCollapsed(!collapsed)}
            style={{ cursor: 'pointer', fontSize: 18 }}
          >
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </div>
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Avatar icon={<UserOutlined />} />
              <span>{user?.nickname || user?.username || '用户'}</span>
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

export default MainLayout;
