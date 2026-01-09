import request from '@/utils/request';
import type { ApiResponse, DictType, DictData } from '@/types';

export const dictApi = {
  getAllTypes: () =>
    request.get<ApiResponse<DictType[]>>('/dict/types'),

  createType: (data: Partial<DictType>) =>
    request.post<ApiResponse<DictType>>('/dict/types', data),

  updateType: (id: number, data: Partial<DictType>) =>
    request.put<ApiResponse<DictType>>(`/dict/types/${id}`, data),

  deleteType: (id: number) =>
    request.delete<ApiResponse<string>>(`/dict/types/${id}`),

  getAllData: () =>
    request.get<ApiResponse<DictData[]>>('/dict/data'),

  getDataByType: (typeCode: string) =>
    request.get<ApiResponse<DictData[]>>(`/dict/data/${typeCode}`),

  createData: (data: Partial<DictData>) =>
    request.post<ApiResponse<DictData>>('/dict/data', data),

  updateData: (id: number, data: Partial<DictData>) =>
    request.put<ApiResponse<DictData>>(`/dict/data/${id}`, data),

  deleteData: (id: number) =>
    request.delete<ApiResponse<string>>(`/dict/data/${id}`),

  init: () =>
    request.post<ApiResponse<string>>('/dict/init'),
};
