import request from '@/utils/request';
import type { Menu } from '@/types';

export const menuApi = {
  getAll: () =>
    request.get<Menu[]>('/menus'),

  getById: (id: number) =>
    request.get<Menu>(`/menus/${id}`),

  create: (data: Partial<Menu>) =>
    request.post<Menu>('/menus', data),

  update: (id: number, data: Partial<Menu>) =>
    request.put<Menu>(`/menus/${id}`, data),

  delete: (id: number) =>
    request.delete(`/menus/${id}`),
};
