// Authentication types
export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
}

// User types
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  createdAt: string;
}

// Biometric Data types
export const SleepQuality = {
  POOR: 0,
  FAIR: 1,
  GOOD: 2,
  EXCELLENT: 3
} as const;

export type SleepQualityType = typeof SleepQuality[keyof typeof SleepQuality];

export interface BiometricData {
  id: number;
  userId: number;
  heartRate: number;
  bloodPressureSystolic: number;
  bloodPressureDiastolic: number;
  temperature: number;
  cortisol?: number;
  adrenaline?: number;
  sleepQuality?: SleepQualityType;
  sleepHours?: number;
  respiratoryRate?: number;
  timestamp: string;
}

export interface BiometricDataRequest {
  userId: number;
  heartRate: number;
  bloodPressureSystolic: number;
  bloodPressureDiastolic: number;
  temperature: number;
  cortisol?: number;
  adrenaline?: number;
  sleepQuality?: SleepQualityType;
  sleepHours?: number;
  respiratoryRate?: number;
}

// Stress Analysis types
export interface StressAnalysis {
  id: number;
  biometricDataId: number;
  userId: number;
  stressScore: number;
  stressLevel: 'LOW' | 'MODERATE' | 'HIGH' | 'VERY_HIGH';
  cardiovascularScore: number;
  thermalScore: number;
  biochemicalScore: number;
  sleepScore: number;
  respiratoryScore: number;
  recommendations: string[];
  analyzedAt: string;
}

export interface StressAnalysisRequest {
  biometricDataId: number;
}

export interface AverageStressScore {
  userId: number;
  averageScore: number;
  totalAnalyses: number;
}

// API Error type
export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
}
