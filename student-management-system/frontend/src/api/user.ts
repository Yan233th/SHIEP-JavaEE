import request from '@/utils/request';
import type { User } from '@/types';

export const userApi = {
  getAll: () =>
    request.get<User[]>('/users'),

  getById: (id: number) =>
    request.get<User>(`/users/${id}`),

  create: (data: Partial<User>) =>
    request.post<User>('/users', data),

  update: (id: number, data: Partial<User>) =>
    request.put<User>(`/users/${id}`, data),

  delete: (id: number) =>
    request.delete(`/users/${id}`),
};
