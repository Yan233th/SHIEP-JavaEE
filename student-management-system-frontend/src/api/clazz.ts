import request from '@/utils/request';
import type { ApiResponse, Clazz } from '@/types';

export const clazzApi = {
  getAll: () =>
    request.get<ApiResponse<Clazz[]>>('/class'),

  getById: (id: number) =>
    request.get<ApiResponse<Clazz>>(`/class/${id}`),

  getByDeptId: (deptId: number) =>
    request.get<ApiResponse<Clazz[]>>(`/class/dept/${deptId}`),

  getByGrade: (grade: string) =>
    request.get<ApiResponse<Clazz[]>>(`/class/grade/${grade}`),

  create: (data: Partial<Clazz>) =>
    request.post<ApiResponse<Clazz>>('/class', data),

  update: (id: number, data: Partial<Clazz>) =>
    request.put<ApiResponse<Clazz>>(`/class/${id}`, data),

  delete: (id: number) =>
    request.delete<ApiResponse<string>>(`/class/${id}`),
};
