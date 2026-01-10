import request from '@/utils/request';
import type { Student, ApiResponse } from '@/types';

export const studentApi = {
  getAll: () =>
    request.get<Student[]>('/students'),

  getById: (id: number) =>
    request.get<Student>(`/students/${id}`),

  getByStudentNumber: (studentNumber: string) =>
    request.get<ApiResponse<Student>>(`/students/number/${studentNumber}`),

  getByClassId: (classId: number) =>
    request.get<ApiResponse<Student[]>>(`/students/class/${classId}`),

  getByDeptId: (deptId: number) =>
    request.get<ApiResponse<Student[]>>(`/students/dept/${deptId}`),

  create: (data: Partial<Student>) =>
    request.post<Student>('/students', data),

  update: (id: number, data: Partial<Student>) =>
    request.put<Student>(`/students/${id}`, data),

  delete: (id: number) =>
    request.delete(`/students/${id}`),

  import: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return request.post('/students/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  uploadAvatar: (id: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return request.post<ApiResponse<string>>(`/students/${id}/avatar`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  rebuildIndex: () =>
    request.post<ApiResponse<string>>('/students/rebuild-index'),
};
