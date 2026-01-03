import request from '@/utils/request';
import type { DictType, DictData, ApiResponse } from '@/types';

export const dictApi = {
  // 字典类型
  getAllTypes: () =>
    request.get<ApiResponse<DictType[]>>('/dict/types'),

  getTypeById: (id: number) =>
    request.get<ApiResponse<DictType>>(`/dict/types/${id}`),

  createType: (data: Partial<DictType>) =>
    request.post<ApiResponse<DictType>>('/dict/types', data),

  updateType: (id: number, data: Partial<DictType>) =>
    request.put<ApiResponse<DictType>>(`/dict/types/${id}`, data),

  deleteType: (id: number) =>
    request.delete<ApiResponse<string>>(`/dict/types/${id}`),

  // 字典数据
  getDataByTypeCode: (typeCode: string) =>
    request.get<ApiResponse<DictData[]>>(`/dict/data/${typeCode}`),

  getAllDataByTypeCode: (typeCode: string) =>
    request.get<ApiResponse<DictData[]>>(`/dict/data/all/${typeCode}`),

  getDataById: (id: number) =>
    request.get<ApiResponse<DictData>>(`/dict/data/item/${id}`),

  createData: (data: Partial<DictData>) =>
    request.post<ApiResponse<DictData>>('/dict/data', data),

  updateData: (id: number, data: Partial<DictData>) =>
    request.put<ApiResponse<DictData>>(`/dict/data/${id}`, data),

  deleteData: (id: number) =>
    request.delete<ApiResponse<string>>(`/dict/data/${id}`),

  getLabel: (typeCode: string, dictValue: string) =>
    request.get<ApiResponse<string>>(`/dict/label/${typeCode}/${dictValue}`),

  initDefaultDicts: () =>
    request.post<ApiResponse<string>>('/dict/init'),
};
