import request from '@/utils/request';

// 查询学生的课表
export const getStudentSchedule = (studentId: number, semester: string) => {
  return request.get(`/schedules/student/${studentId}`, {
    params: { semester }
  });
};

// 查询所有课表
export const getAllSchedules = () => {
  return request.get('/schedules');
};

// 创建课表
export const createSchedule = (data: any) => {
  return request.post('/schedules', data);
};

// 更新课表
export const updateSchedule = (id: number, data: any) => {
  return request.put(`/schedules/${id}`, data);
};

// 删除课表
export const deleteSchedule = (id: number) => {
  return request.delete(`/schedules/${id}`);
};

// 按学期查询课表
export const getSchedulesBySemester = (semester: string) => {
  return request.get(`/schedules/semester/${semester}`);
};

// 按课程查询课表
export const getSchedulesByCourse = (courseId: number) => {
  return request.get(`/schedules/course/${courseId}`);
};
