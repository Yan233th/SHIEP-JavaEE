import request from '@/utils/request';
import type { Course } from '@/types';

export const courseApi = {
  getAll: () =>
    request.get<Course[]>('/courses'),

  getById: (id: number) =>
    request.get<Course>(`/courses/${id}`),

  create: (data: Partial<Course>) =>
    request.post<Course>('/courses', data),

  update: (id: number, data: Partial<Course>) =>
    request.put<Course>(`/courses/${id}`, data),

  delete: (id: number) =>
    request.delete(`/courses/${id}`),

  enroll: (courseId: number, studentId: number) =>
    request.post(`/courses/${courseId}/enroll/${studentId}`),

  unenroll: (courseId: number, studentId: number) =>
    request.delete(`/courses/${courseId}/enroll/${studentId}`),

  uploadAttachment: (courseId: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return request.post(`/upload/course/${courseId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
