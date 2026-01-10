import request from '@/utils/request';

// 获取所有可选课程
export const getAvailableCourses = () => {
  return request.get('/courses');
};

// 学生选课
export const enrollCourse = (studentId: number, courseId: number) => {
  return request.post('/enrollments/enroll', null, {
    params: { studentId, courseId }
  });
};

// 学生退课
export const dropCourse = (studentId: number, courseId: number) => {
  return request.post('/enrollments/drop', null, {
    params: { studentId, courseId }
  });
};

// 查询学生的选课记录
export const getMyEnrollments = (studentId: number) => {
  return request.get(`/enrollments/student/${studentId}`);
};

// 检查是否已选课
export const checkEnrollment = (studentId: number, courseId: number) => {
  return request.get('/enrollments/check', {
    params: { studentId, courseId }
  });
};
