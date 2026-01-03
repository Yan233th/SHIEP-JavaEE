import request from '@/utils/request';
import type { LoginRequest, LoginResponse, User, RegisterRequest, CaptchaResponse, PasswordStrengthResponse, ApiResponse } from '@/types';

export const authApi = {
  login: (data: LoginRequest) =>
    request.post<LoginResponse>('/auth/login', data),

  getInfo: () =>
    request.get<User>('/auth/info'),

  logout: () =>
    request.post('/auth/logout'),

  register: (data: RegisterRequest) =>
    request.post<ApiResponse<string>>('/auth/register', data),

  getCaptcha: () =>
    request.get<ApiResponse<CaptchaResponse>>('/auth/captcha'),

  checkPasswordStrength: (password: string) =>
    request.get<ApiResponse<PasswordStrengthResponse>>('/auth/password-strength', { params: { password } }),

  changePassword: (oldPassword: string, newPassword: string) =>
    request.post<ApiResponse<string>>('/auth/change-password', { oldPassword, newPassword }),
};
