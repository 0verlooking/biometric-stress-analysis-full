import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { biometricService } from '../services/biometric.service';
import { stressService } from '../services/stress.service';
import { Activity, Heart, Thermometer, Droplets, Moon, Wind, TrendingUp } from 'lucide-react';
import type { BiometricDataRequest, StressAnalysis, AverageStressScore } from '../types/api.types';
import { SleepQuality } from '../types/api.types';

export const DashboardPage = () => {
  const { user } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [latestAnalysis, setLatestAnalysis] = useState<StressAnalysis | null>(null);
  const [averageScore, setAverageScore] = useState<AverageStressScore | null>(null);

  const [formData, setFormData] = useState<BiometricDataRequest>({
    userId: user?.id || 0,
    heartRate: 70,
    bloodPressureSystolic: 120,
    bloodPressureDiastolic: 80,
    temperature: 36.6,
    cortisol: undefined,
    adrenaline: undefined,
    sleepQuality: undefined,
    sleepHours: undefined,
    respiratoryRate: undefined,
  });

  useEffect(() => {
    if (user?.id) {
      loadAverageScore();
    }
  }, [user]);

  const loadAverageScore = async () => {
    try {
      const score = await stressService.getAverageStressScore(user!.id);
      setAverageScore(score);
    } catch (err) {
      console.error('Failed to load average score:', err);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setIsLoading(true);

    try {
      // Create biometric data
      const biometricData = await biometricService.createBiometricData({
        ...formData,
        userId: user!.id,
      });

      // Analyze stress
      const analysis = await stressService.analyzeStress(biometricData.id);
      setLatestAnalysis(analysis);
      setSuccess('✓ Дані успішно проаналізовані!');

      // Reload average score
      await loadAverageScore();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Помилка при обробці даних');
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const value = e.target.value === '' ? undefined : parseFloat(e.target.value);
    setFormData({
      ...formData,
      [e.target.name]: value,
    });
  };

  const getStressLevelColor = (level: string) => {
    switch (level) {
      case 'LOW': return '#22c55e';
      case 'MODERATE': return '#eab308';
      case 'HIGH': return '#f97316';
      case 'VERY_HIGH': return '#ef4444';
      default: return '#6b7280';
    }
  };

  const getStressLevelText = (level: string) => {
    switch (level) {
      case 'LOW': return 'Низький';
      case 'MODERATE': return 'Помірний';
      case 'HIGH': return 'Високий';
      case 'VERY_HIGH': return 'Дуже високий';
      default: return level;
    }
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Панель аналізу стресу</h1>
        <p>Вітаємо, {user?.username}!</p>
      </div>

      {averageScore && (
        <div className="stats-card">
          <TrendingUp className="stats-icon" />
          <div className="stats-content">
            <h3>Середній рівень стресу</h3>
            <p className="stats-value">{averageScore.averageScore.toFixed(1)}</p>
            <p className="stats-label">Всього аналізів: {averageScore.totalAnalyses}</p>
          </div>
        </div>
      )}

      <div className="dashboard-grid">
        <div className="card">
          <h2>Введення біометричних даних</h2>

          <form onSubmit={handleSubmit} className="biometric-form">
            {error && <div className="error-message">{error}</div>}
            {success && <div className="success-message">{success}</div>}

            <div className="form-section">
              <h3><Heart /> Серцево-судинні показники</h3>

              <div className="form-group">
                <label htmlFor="heartRate">Пульс (уд/хв) *</label>
                <input
                  type="number"
                  id="heartRate"
                  name="heartRate"
                  value={formData.heartRate || ''}
                  onChange={handleChange}
                  required
                  min="40"
                  max="200"
                  step="1"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="bloodPressureSystolic">Систолічний тиск *</label>
                  <input
                    type="number"
                    id="bloodPressureSystolic"
                    name="bloodPressureSystolic"
                    value={formData.bloodPressureSystolic || ''}
                    onChange={handleChange}
                    required
                    min="70"
                    max="250"
                    step="1"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="bloodPressureDiastolic">Діастолічний тиск *</label>
                  <input
                    type="number"
                    id="bloodPressureDiastolic"
                    name="bloodPressureDiastolic"
                    value={formData.bloodPressureDiastolic || ''}
                    onChange={handleChange}
                    required
                    min="40"
                    max="150"
                    step="1"
                  />
                </div>
              </div>
            </div>

            <div className="form-section">
              <h3><Thermometer /> Температура тіла</h3>

              <div className="form-group">
                <label htmlFor="temperature">Температура (°C) *</label>
                <input
                  type="number"
                  id="temperature"
                  name="temperature"
                  value={formData.temperature || ''}
                  onChange={handleChange}
                  required
                  min="35"
                  max="42"
                  step="0.1"
                />
              </div>
            </div>

            <div className="form-section">
              <h3><Droplets /> Біохімічні показники (опціонально)</h3>

              <div className="form-group">
                <label htmlFor="cortisol">Кортизол (нмоль/л)</label>
                <input
                  type="number"
                  id="cortisol"
                  name="cortisol"
                  value={formData.cortisol || ''}
                  onChange={handleChange}
                  min="0"
                  step="0.1"
                />
              </div>

              <div className="form-group">
                <label htmlFor="adrenaline">Адреналін (пг/мл)</label>
                <input
                  type="number"
                  id="adrenaline"
                  name="adrenaline"
                  value={formData.adrenaline || ''}
                  onChange={handleChange}
                  min="0"
                  step="0.1"
                />
              </div>
            </div>

            <div className="form-section">
              <h3><Moon /> Якість сну (опціонально)</h3>

              <div className="form-group">
                <label htmlFor="sleepQuality">Якість сну</label>
                <select
                  id="sleepQuality"
                  name="sleepQuality"
                  value={formData.sleepQuality ?? ''}
                  onChange={handleChange}
                >
                  <option value="">Не вказано</option>
                  <option value={SleepQuality.POOR}>Погана</option>
                  <option value={SleepQuality.FAIR}>Задовільна</option>
                  <option value={SleepQuality.GOOD}>Добра</option>
                  <option value={SleepQuality.EXCELLENT}>Відмінна</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="sleepHours">Тривалість сну (годин)</label>
                <input
                  type="number"
                  id="sleepHours"
                  name="sleepHours"
                  value={formData.sleepHours || ''}
                  onChange={handleChange}
                  min="0"
                  max="24"
                  step="0.5"
                />
              </div>
            </div>

            <div className="form-section">
              <h3><Wind /> Дихальні показники (опціонально)</h3>

              <div className="form-group">
                <label htmlFor="respiratoryRate">Частота дихання (вд/хв)</label>
                <input
                  type="number"
                  id="respiratoryRate"
                  name="respiratoryRate"
                  value={formData.respiratoryRate || ''}
                  onChange={handleChange}
                  min="8"
                  max="40"
                  step="1"
                />
              </div>
            </div>

            <button type="submit" className="btn btn-primary btn-large" disabled={isLoading}>
              {isLoading ? 'Аналіз...' : 'Проаналізувати'}
            </button>
          </form>
        </div>

        {latestAnalysis && (
          <div className="card">
            <h2>Результати аналізу</h2>

            <div className="analysis-result">
              <div className="stress-level" style={{ backgroundColor: getStressLevelColor(latestAnalysis.stressLevel) }}>
                <Activity size={48} />
                <h3>Рівень стресу</h3>
                <p className="stress-level-text">{getStressLevelText(latestAnalysis.stressLevel)}</p>
                <p className="stress-score">Оцінка: {latestAnalysis.stressScore.toFixed(1)}</p>
              </div>

              <div className="metrics-grid">
                <div className="metric">
                  <Heart />
                  <span>Серцево-судинні</span>
                  <strong>{latestAnalysis.cardiovascularScore.toFixed(1)}</strong>
                </div>
                <div className="metric">
                  <Thermometer />
                  <span>Термальні</span>
                  <strong>{latestAnalysis.thermalScore.toFixed(1)}</strong>
                </div>
                <div className="metric">
                  <Droplets />
                  <span>Біохімічні</span>
                  <strong>{latestAnalysis.biochemicalScore.toFixed(1)}</strong>
                </div>
                <div className="metric">
                  <Moon />
                  <span>Якість сну</span>
                  <strong>{latestAnalysis.sleepScore.toFixed(1)}</strong>
                </div>
                <div className="metric">
                  <Wind />
                  <span>Дихальні</span>
                  <strong>{latestAnalysis.respiratoryScore.toFixed(1)}</strong>
                </div>
              </div>

              <div className="recommendations">
                <h3>Рекомендації</h3>
                <ul>
                  {latestAnalysis.recommendations.map((rec, index) => (
                    <li key={index}>{rec}</li>
                  ))}
                </ul>
              </div>

              <p className="analysis-date">
                Проаналізовано: {new Date(latestAnalysis.analyzedAt).toLocaleString('uk-UA')}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
