import request from '@/utils/request';
import type { Role } from '@/types';

export const roleApi = {
  getAll: () =>
    request.get<Role[]>('/roles'),

  getById: (id: number) =>
    request.get<Role>(`/roles/${id}`),

  create: (data: Partial<Role>) =>
    request.post<Role>('/roles', data),

  update: (id: number, data: Partial<Role>) =>
    request.put<Role>(`/roles/${id}`, data),

  delete: (id: number) =>
    request.delete(`/roles/${id}`),
};
