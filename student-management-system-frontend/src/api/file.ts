import request from '@/utils/request';

// 上传文件
export const uploadFile = (file: File, folder: string = 'uploads') => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('folder', folder);
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// 上传头像
export const uploadAvatar = (file: File, userId: number) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post(`/files/avatar/${userId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// 删除文件
export const deleteFile = (objectName: string) => {
  return request.delete('/files', { params: { objectName } });
};
