import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { stressService } from '../services/stress.service';
import { History, Activity, Calendar, TrendingUp } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import type { StressAnalysis } from '../types/api.types';

export const HistoryPage = () => {
  const { user } = useAuth();
  const [analyses, setAnalyses] = useState<StressAnalysis[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user?.id) {
      loadHistory();
    }
  }, [user]);

  const loadHistory = async () => {
    try {
      setIsLoading(true);
      const data = await stressService.getUserStressAnalyses(user!.id);
      setAnalyses(data.sort((a, b) => new Date(b.analyzedAt).getTime() - new Date(a.analyzedAt).getTime()));
    } catch (err: any) {
      setError(err.response?.data?.message || 'Помилка завантаження історії');
    } finally {
      setIsLoading(false);
    }
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

  const chartData = analyses
    .slice()
    .reverse()
    .map(analysis => ({
      date: new Date(analysis.analyzedAt).toLocaleDateString('uk-UA'),
      'Загальний стрес': analysis.stressScore,
      'Серце': analysis.cardiovascularScore,
      'Біохімія': analysis.biochemicalScore,
      'Сон': analysis.sleepScore,
    }));

  if (isLoading) {
    return (
      <div className="history-container">
        <div className="loading">Завантаження історії...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="history-container">
        <div className="error-message">{error}</div>
      </div>
    );
  }

  return (
    <div className="history-container">
      <div className="page-header">
        <History className="page-icon" />
        <h1>Історія аналізів</h1>
        <p>Всього аналізів: {analyses.length}</p>
      </div>

      {analyses.length === 0 ? (
        <div className="empty-state">
          <Activity size={64} />
          <h3>Історія порожня</h3>
          <p>Проведіть перший аналіз на головній сторінці</p>
        </div>
      ) : (
        <>
          {chartData.length > 1 && (
            <div className="card chart-card">
              <h2><TrendingUp /> Динаміка показників</h2>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="Загальний стрес" stroke="#8b5cf6" strokeWidth={2} />
                  <Line type="monotone" dataKey="Серце" stroke="#ef4444" strokeWidth={2} />
                  <Line type="monotone" dataKey="Біохімія" stroke="#3b82f6" strokeWidth={2} />
                  <Line type="monotone" dataKey="Сон" stroke="#22c55e" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          )}

          <div className="history-list">
            {analyses.map((analysis) => (
              <div key={analysis.id} className="history-item">
                <div className="history-header">
                  <div className="history-date">
                    <Calendar size={20} />
                    {new Date(analysis.analyzedAt).toLocaleString('uk-UA')}
                  </div>
                  <div
                    className="stress-badge"
                    style={{ backgroundColor: getStressLevelColor(analysis.stressLevel) }}
                  >
                    {getStressLevelText(analysis.stressLevel)}
                  </div>
                </div>

                <div className="history-score">
                  <Activity />
                  <span>Оцінка стресу:</span>
                  <strong>{analysis.stressScore.toFixed(1)}</strong>
                </div>

                <div className="history-metrics">
                  <div className="small-metric">
                    <span>Серце:</span>
                    <strong>{analysis.cardiovascularScore.toFixed(1)}</strong>
                  </div>
                  <div className="small-metric">
                    <span>Температура:</span>
                    <strong>{analysis.thermalScore.toFixed(1)}</strong>
                  </div>
                  <div className="small-metric">
                    <span>Біохімія:</span>
                    <strong>{analysis.biochemicalScore.toFixed(1)}</strong>
                  </div>
                  <div className="small-metric">
                    <span>Сон:</span>
                    <strong>{analysis.sleepScore.toFixed(1)}</strong>
                  </div>
                  <div className="small-metric">
                    <span>Дихання:</span>
                    <strong>{analysis.respiratoryScore.toFixed(1)}</strong>
                  </div>
                </div>

                {analysis.recommendations.length > 0 && (
                  <details className="history-recommendations">
                    <summary>Рекомендації</summary>
                    <ul>
                      {analysis.recommendations.map((rec, index) => (
                        <li key={index}>{rec}</li>
                      ))}
                    </ul>
                  </details>
                )}
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
};
