// 用户相关类型
export interface User {
  id: number;
  username: string;
  nickname?: string;
  email?: string;
  avatar?: string;
  createTime?: string;
  department?: Department;
  roles?: Role[];
  studentId?: number;
}

export interface Role {
  id: number;
  name: string;
  description?: string;
  menus?: Menu[];
}

export interface Menu {
  id: number;
  name: string;
  path?: string;
  icon?: string;
  parentId?: number;
  sortOrder?: number;
  children?: Menu[];
}

export interface Department {
  id: number;
  name: string;
  description?: string;
}

// 学生相关类型
export interface Student {
  id: number;
  studentNumber: string;
  name?: string;
  gender?: string;
  nation?: string;
  political?: string;
  idCard?: string;
  phone?: string;
  email?: string;
  birthDate?: string;
  address?: string;
  avatar?: string;
  enrollDate?: string;
  status?: string;
  clazz?: Clazz;
  department?: Department;
  user?: User;
  courses?: Course[];
  createTime?: string;
  updateTime?: string;
  remark?: string;
}

// 课程相关类型
export interface Course {
  id: number;
  name: string;
  credits: number;
  description?: string;
  semester?: string;
  teacher?: User;
  attachments?: Attachment[];
  students?: Student[];
}

export interface Attachment {
  id: number;
  fileName: string;
  fileUrl: string;
  fileSize?: number;
  fileType?: string;
  uploadTime?: string;
}

// 通知相关类型
export interface Notification {
  id: number;
  title: string;
  content?: string;
  isRead: boolean;
  createTime?: string;
}

// API响应类型
export interface ApiResponse<T = unknown> {
  code: number;
  message?: string;
  data?: T;
}

export interface LoginResponse {
  code: number;
  token?: string;
  username?: string;
  message?: string;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// 登录请求类型
export interface LoginRequest {
  username: string;
  password: string;
  captcha?: string;
  captchaKey?: string;
}

// 注册请求类型
export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
  nickname?: string;
  email?: string;
  captcha?: string;
  captchaKey?: string;
}

// 验证码响应类型
export interface CaptchaResponse {
  key: string;
  image: string;
}

// 密码强度响应类型
export interface PasswordStrengthResponse {
  strength: string;
  errors: string[];
}

// 数据字典类型
export interface DictType {
  id: number;
  typeCode: string;
  typeName: string;
  description?: string;
  status: number;
  createTime?: string;
}

export interface DictData {
  id: number;
  typeCode: string;
  dictLabel: string;
  dictValue: string;
  sortOrder: number;
  status: number;
  remark?: string;
  createTime?: string;
}

// 班级类型
export interface Clazz {
  id: number;
  className: string;
  classCode?: string;
  grade?: string;
  major?: string;
  department?: Department;
  teacherName?: string;
  studentCount?: number;
  status?: number;
  remark?: string;
  createTime?: string;
}
