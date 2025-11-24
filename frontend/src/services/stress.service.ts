import { apiClient } from './api.config';
import type { StressAnalysis, AverageStressScore } from '../types/api.types';

export const stressService = {
  async analyzeStress(biometricDataId: number): Promise<StressAnalysis> {
    const response = await apiClient.post<StressAnalysis>(`/stress-analysis/analyze/${biometricDataId}`);
    return response.data;
  },

  async getUserStressAnalyses(userId: number): Promise<StressAnalysis[]> {
    const response = await apiClient.get<StressAnalysis[]>(`/stress-analysis/user/${userId}`);
    return response.data;
  },

  async getAverageStressScore(userId: number): Promise<AverageStressScore> {
    const response = await apiClient.get<AverageStressScore>(`/stress-analysis/user/${userId}/average-score`);
    return response.data;
  },
};
