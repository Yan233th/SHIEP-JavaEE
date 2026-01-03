import request from '@/utils/request';
import type { Clazz, ApiResponse } from '@/types';

export const clazzApi = {
  getAll: () =>
    request.get<ApiResponse<Clazz[]>>('/class'),

  getById: (id: number) =>
    request.get<ApiResponse<Clazz>>(`/class/${id}`),

  getByClassCode: (classCode: string) =>
    request.get<ApiResponse<Clazz>>(`/class/code/${classCode}`),

  getByDepartment: (deptId: number) =>
    request.get<ApiResponse<Clazz[]>>(`/class/dept/${deptId}`),

  getByGrade: (grade: string) =>
    request.get<ApiResponse<Clazz[]>>(`/class/grade/${grade}`),

  getByDeptAndGrade: (deptId: number, grade: string) =>
    request.get<ApiResponse<Clazz[]>>(`/class/dept/${deptId}/grade/${grade}`),

  getAllGrades: () =>
    request.get<ApiResponse<string[]>>('/class/grades'),

  getMajorsByDept: (deptId: number) =>
    request.get<ApiResponse<string[]>>(`/class/majors/${deptId}`),

  create: (data: Partial<Clazz>) =>
    request.post<ApiResponse<Clazz>>('/class', data),

  update: (id: number, data: Partial<Clazz>) =>
    request.put<ApiResponse<Clazz>>(`/class/${id}`, data),

  delete: (id: number) =>
    request.delete<ApiResponse<string>>(`/class/${id}`),
};
