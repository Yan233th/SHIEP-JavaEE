import request from '@/utils/request';
import type { Department } from '@/types';

export const departmentApi = {
  getAll: () =>
    request.get<Department[]>('/departments'),

  getById: (id: number) =>
    request.get<Department>(`/departments/${id}`),

  create: (data: Partial<Department>) =>
    request.post<Department>('/departments', data),

  update: (id: number, data: Partial<Department>) =>
    request.put<Department>(`/departments/${id}`, data),

  delete: (id: number) =>
    request.delete(`/departments/${id}`),
};
