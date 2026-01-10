import { createBrowserRouter, Navigate } from 'react-router-dom';
import MainLayout from '@/components/layout/MainLayout';
import StudentLayout from '@/components/layout/StudentLayout';
import AuthGuard from './AuthGuard';
import AdminGuard from './AdminGuard';
import LoginPage from '@/pages/login';
import RegisterPage from '@/pages/register';
import DashboardPage from '@/pages/dashboard';
import UserPage from '@/pages/system/user';
import RolePage from '@/pages/system/role';
import MenuPage from '@/pages/system/menu';
import DepartmentPage from '@/pages/system/department';
import ClassPage from '@/pages/system/class';
import SchedulePage from '@/pages/system/schedule';
import NotificationManagePage from '@/pages/system/notification';
import StudentPage from '@/pages/student';
import CoursePage from '@/pages/course';
import SearchPage from '@/pages/search';
import AvailableCoursesPage from '@/pages/student-portal/AvailableCoursesPage';
import MyCoursesPage from '@/pages/student-portal/MyCoursesPage';
import MySchedulePage from '@/pages/student-portal/MySchedulePage';
import NotificationPage from '@/pages/student-portal/NotificationPage';

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
      <AdminGuard>
        <MainLayout />
      </AdminGuard>
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
        path: 'system/class',
        element: <ClassPage />,
      },
      {
        path: 'system/schedule',
        element: <SchedulePage />,
      },
      {
        path: 'system/notification',
        element: <NotificationManagePage />,
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
    ],
  },
  {
    path: '/portal',
    element: (
      <AuthGuard>
        <StudentLayout />
      </AuthGuard>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/portal/courses" replace />,
      },
      {
        path: 'courses',
        element: <AvailableCoursesPage />,
      },
      {
        path: 'my-courses',
        element: <MyCoursesPage />,
      },
      {
        path: 'schedule',
        element: <MySchedulePage />,
      },
      {
        path: 'notifications',
        element: <NotificationPage />,
      },
    ],
  },
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);

export default router;
