import request from '@/utils/request';

// 全文搜索学生
export const searchStudents = (keyword: string) => {
  return request.get('/search/students', { params: { keyword } });
};

// 按昵称搜索
export const searchByNickname = (nickname: string) => {
  return request.get('/search/students/nickname', { params: { nickname } });
};

// 按学号搜索
export const searchByStudentNumber = (studentNumber: string) => {
  return request.get('/search/students/studentNumber', { params: { studentNumber } });
};

// 按班级搜索
export const searchByClassName = (className: string) => {
  return request.get('/search/students/className', { params: { className } });
};

// 全文搜索课程附件
export const searchAttachments = (keyword: string) => {
  return request.get('/search/attachments', { params: { keyword } });
};
