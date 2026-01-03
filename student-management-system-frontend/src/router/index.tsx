import { createBrowserRouter, Navigate } from 'react-router-dom';
import MainLayout from '@/components/layout/MainLayout';
import AuthGuard from './AuthGuard';
import LoginPage from '@/pages/login';
import RegisterPage from '@/pages/register';
import DashboardPage from '@/pages/dashboard';
import UserPage from '@/pages/system/user';
import RolePage from '@/pages/system/role';
import MenuPage from '@/pages/system/menu';
import DepartmentPage from '@/pages/system/department';
import StudentPage from '@/pages/student';
import CoursePage from '@/pages/course';
import SearchPage from '@/pages/search';
import ExcelPage from '@/pages/excel';
import FilePage from '@/pages/file';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    path: '/',
    element: (
      <AuthGuard>
        <MainLayout />
      </AuthGuard>
    ),
    children: [
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: 'system/user',
        element: <UserPage />,
      },
      {
        path: 'system/role',
        element: <RolePage />,
      },
      {
        path: 'system/menu',
        element: <MenuPage />,
      },
      {
        path: 'system/department',
        element: <DepartmentPage />,
      },
      {
        path: 'student',
        element: <StudentPage />,
      },
      {
        path: 'course',
        element: <CoursePage />,
      },
      {
        path: 'search',
        element: <SearchPage />,
      },
      {
        path: 'excel',
        element: <ExcelPage />,
      },
      {
        path: 'file',
        element: <FilePage />,
      },
    ],
  },
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);

export default router;
