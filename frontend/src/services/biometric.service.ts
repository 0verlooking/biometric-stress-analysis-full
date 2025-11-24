import { apiClient } from './api.config';
import type { BiometricData, BiometricDataRequest } from '../types/api.types';

export const biometricService = {
  async createBiometricData(data: BiometricDataRequest): Promise<BiometricData> {
    const response = await apiClient.post<BiometricData>('/biometric-data', data);
    return response.data;
  },

  async getUserBiometricData(userId: number): Promise<BiometricData[]> {
    const response = await apiClient.get<BiometricData[]>(`/biometric-data/user/${userId}`);
    return response.data;
  },

  async getLatestBiometricData(userId: number): Promise<BiometricData> {
    const response = await apiClient.get<BiometricData>(`/biometric-data/user/${userId}/latest`);
    return response.data;
  },
};
