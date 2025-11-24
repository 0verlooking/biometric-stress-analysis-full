import { apiClient } from './api.config';
import type { RegisterRequest, LoginRequest, AuthResponse } from '../types/api.types';

export const authService = {
  async register(data: RegisterRequest): Promise<AuthResponse> {
    console.log('Registering user with data:', data);
    const response = await apiClient.post<AuthResponse>('/auth/register', data);
    console.log('Registration response:', response.data);
    if (response.data.token) {
      this.saveToken(response.data.token);
      this.saveUser(response.data);
    }
    return response.data;
  },

  async login(data: LoginRequest): Promise<AuthResponse> {
    console.log('Logging in user:', data.username);
    const response = await apiClient.post<AuthResponse>('/auth/login', data);
    console.log('Login response:', response.data);
    if (response.data.token) {
      this.saveToken(response.data.token);
      this.saveUser(response.data);
    }
    return response.data;
  },

  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
  },

  saveToken(token: string): void {
    localStorage.setItem('jwt_token', token);
  },

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  },

  saveUser(user: AuthResponse): void {
    localStorage.setItem('user', JSON.stringify(user));
  },

  getCurrentUser(): AuthResponse | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  },
};
