import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User } from '@/types';
import { authApi } from '@/api/auth';

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  login: (username: string, password: string, captcha?: string, captchaKey?: string) => Promise<void>;
  logout: () => void;
  fetchUserInfo: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      user: null,
      isAuthenticated: false,

      login: async (username: string, password: string, captcha?: string, captchaKey?: string) => {
        try {
          const response = await authApi.login({ username, password, captcha, captchaKey });
          const data = response.data;
          if (data.code === 200 && data.token) {
            localStorage.setItem('token', data.token);
            set({ token: data.token, isAuthenticated: true });
          } else {
            throw new Error(data.message || '登录失败');
          }
        } catch (error: unknown) {
          if (error && typeof error === 'object' && 'response' in error) {
            const axiosError = error as { response?: { data?: { message?: string } } };
            const msg = axiosError.response?.data?.message || '用户名或密码错误';
            throw new Error(msg);
          }
          throw error;
        }
      },

      logout: () => {
        localStorage.removeItem('token');
        set({ token: null, user: null, isAuthenticated: false });
      },

      fetchUserInfo: async () => {
        try {
          const response = await authApi.getInfo();
          set({ user: response.data });
        } catch {
          // 如果获取用户信息失败，清除登录状态
          get().logout();
        }
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ token: state.token, isAuthenticated: state.isAuthenticated }),
    }
  )
);
