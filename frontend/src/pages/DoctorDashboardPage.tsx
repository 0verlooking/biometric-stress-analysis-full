import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { adminService } from '../services/admin.service';
import { stressService } from '../services/stress.service';
import { Users, Activity, TrendingUp, AlertCircle } from 'lucide-react';
import type { User } from '../types/api.types';

export const DoctorDashboardPage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [patients, setPatients] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedPatient, setSelectedPatient] = useState<User | null>(null);
  const [patientStats, setPatientStats] = useState<any>(null);

  // Redirect if not doctor
  useEffect(() => {
    if (user && user.role !== 'DOCTOR') {
      navigate('/dashboard');
    }
  }, [user, navigate]);

  useEffect(() => {
    loadPatients();
  }, []);

  const loadPatients = async () => {
    try {
      setLoading(true);
      const allUsers = await adminService.getAllUsers();
      // Filter only patients (USER role) and active users
      const patientsList = allUsers.filter(u => u.role === 'USER' && u.active);
      setPatients(patientsList);
    } catch (err) {
      setError('Помилка завантаження списку пацієнтів');
    } finally {
      setLoading(false);
    }
  };

  const loadPatientStats = async (patientId: number) => {
    try {
      const stats = await stressService.getAverageStressScore(patientId);
      setPatientStats(stats);
    } catch (err) {
      console.error('Error loading patient stats:', err);
      setPatientStats(null);
    }
  };

  const handlePatientClick = (patient: User) => {
    setSelectedPatient(patient);
    loadPatientStats(patient.id);
  };

  const getStressLevelColor = (score: number) => {
    if (score < 25) return '#22c55e';
    if (score < 50) return '#eab308';
    if (score < 75) return '#f97316';
    return '#ef4444';
  };

  const getStressLevelText = (score: number) => {
    if (score < 25) return 'Низький';
    if (score < 50) return 'Помірний';
    if (score < 75) return 'Високий';
    return 'Дуже високий';
  };

  const stats = {
    totalPatients: patients.length,
    activePatients: patients.filter(p => p.active).length,
  };

  return (
    <div className="doctor-container">
      <div className="doctor-header">
        <h1><Activity size={32} /> Панель лікаря</h1>
        <p>Вітаємо, Dr. {user?.lastName}!</p>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="stats-grid">
        <div className="stat-card">
          <Users className="stat-icon" />
          <div className="stat-content">
            <h3>Всього пацієнтів</h3>
            <p className="stat-value">{stats.totalPatients}</p>
          </div>
        </div>

        <div className="stat-card">
          <Activity className="stat-icon" />
          <div className="stat-content">
            <h3>Активні пацієнти</h3>
            <p className="stat-value">{stats.activePatients}</p>
          </div>
        </div>

        {patientStats && (
          <>
            <div className="stat-card">
              <TrendingUp className="stat-icon" />
              <div className="stat-content">
                <h3>Середній рівень стресу</h3>
                <p className="stat-value">{patientStats.averageScore.toFixed(1)}</p>
              </div>
            </div>

            <div className="stat-card">
              <AlertCircle className="stat-icon" />
              <div className="stat-content">
                <h3>Всього аналізів</h3>
                <p className="stat-value">{patientStats.totalAnalyses}</p>
              </div>
            </div>
          </>
        )}
      </div>

      <div className="doctor-grid">
        <div className="card">
          <h2>Список пацієнтів</h2>

          {loading ? (
            <p>Завантаження...</p>
          ) : patients.length === 0 ? (
            <p>Немає пацієнтів</p>
          ) : (
            <div className="patients-list">
              {patients.map((patient) => (
                <div
                  key={patient.id}
                  className={`patient-card ${selectedPatient?.id === patient.id ? 'selected' : ''}`}
                  onClick={() => handlePatientClick(patient)}
                >
                  <div className="patient-info">
                    <h3>{patient.firstName} {patient.lastName}</h3>
                    <p className="patient-username">@{patient.username}</p>
                    <p className="patient-email">{patient.email}</p>
                  </div>
                  <div className="patient-status">
                    <span className={`status-badge ${patient.active ? 'active' : 'inactive'}`}>
                      {patient.active ? 'Активний' : 'Неактивний'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {selectedPatient && (
          <div className="card">
            <h2>Інформація про пацієнта</h2>

            <div className="patient-details">
              <div className="detail-row">
                <strong>Ім'я:</strong>
                <span>{selectedPatient.firstName} {selectedPatient.lastName}</span>
              </div>
              <div className="detail-row">
                <strong>Email:</strong>
                <span>{selectedPatient.email}</span>
              </div>
              <div className="detail-row">
                <strong>Username:</strong>
                <span>{selectedPatient.username}</span>
              </div>
              <div className="detail-row">
                <strong>Статус:</strong>
                <span className={selectedPatient.active ? 'text-success' : 'text-danger'}>
                  {selectedPatient.active ? 'Активний' : 'Неактивний'}
                </span>
              </div>
            </div>

            {patientStats && (
              <div className="patient-stats">
                <h3>Статистика стресу</h3>
                <div className="stress-indicator" style={{
                  background: `linear-gradient(135deg, ${getStressLevelColor(patientStats.averageScore)} 0%, ${getStressLevelColor(patientStats.averageScore)}88 100%)`
                }}>
                  <div className="stress-score">{patientStats.averageScore.toFixed(1)}</div>
                  <div className="stress-level">{getStressLevelText(patientStats.averageScore)}</div>
                  <div className="stress-count">Всього аналізів: {patientStats.totalAnalyses}</div>
                </div>
              </div>
            )}

            <div className="patient-actions">
              <button
                onClick={() => navigate(`/patient/${selectedPatient.id}`)}
                className="btn btn-primary"
              >
                Детальна історія
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
