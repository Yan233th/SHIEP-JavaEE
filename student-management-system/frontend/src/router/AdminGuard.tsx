import { Navigate } from 'react-router-dom';
import { useAuthStore } from '@/store';

interface AdminGuardProps {
  children: React.ReactNode;
}

const AdminGuard: React.FC<AdminGuardProps> = ({ children }) => {
  const { isAuthenticated, user } = useAuthStore();

  // 如果未登录，重定向到登录页
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // 检查用户是否有管理员角色
  const isAdmin = user?.roles?.some(role =>
    role.name === 'ADMIN' || role.name === '管理员'
  );

  // 如果不是管理员，重定向到学生选课系统
  if (!isAdmin) {
    return <Navigate to="/portal/courses" replace />;
  }

  return <>{children}</>;
};

export default AdminGuard;
