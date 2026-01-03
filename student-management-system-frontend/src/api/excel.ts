import request from '@/utils/request';

// 导出学生数据
export const exportStudents = () => {
  return request.get('/excel/students/export', {
    responseType: 'blob'
  });
};

// 导入学生数据
export const importStudents = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/excel/students/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// 下载导入模板
export const downloadTemplate = () => {
  return request.get('/excel/students/template', {
    responseType: 'blob'
  });
};
