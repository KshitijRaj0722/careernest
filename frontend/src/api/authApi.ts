import axiosClient from '@/api/axiosClient';
import type { AuthResponse, LoginPayload, RegisterPayload } from '@/types';

export const authApi = {
  register: (payload: RegisterPayload) =>
    axiosClient.post<AuthResponse>('/auth/register', payload).then((r) => r.data),

  login: (payload: LoginPayload) =>
    axiosClient.post<AuthResponse>('/auth/login', payload).then((r) => r.data),
};
