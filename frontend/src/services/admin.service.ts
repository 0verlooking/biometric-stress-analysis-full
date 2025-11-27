import { apiClient } from './api.config';
import type { User } from '../types/api.types';

export interface ChangePasswordRequest {
  newPassword: string;
}

export const adminService = {
  async getAllUsers(): Promise<User[]> {
    const response = await apiClient.get<User[]>('/users');
    return response.data;
  },

  async changeUserPassword(userId: number, newPassword: string): Promise<User> {
    const response = await apiClient.put<User>(`/users/${userId}/password`, {
      newPassword
    });
    return response.data;
  },

  async toggleUserActive(userId: number): Promise<User> {
    const response = await apiClient.put<User>(`/users/${userId}/toggle-active`);
    return response.data;
  },

  async deleteUser(userId: number): Promise<void> {
    await apiClient.delete(`/users/${userId}`);
  },
};
