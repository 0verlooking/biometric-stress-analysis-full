import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { adminService } from '../services/admin.service';
import { Users, Activity, Shield, TrendingUp } from 'lucide-react';
import type { User } from '../types/api.types';

export const AdminDashboardPage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [newPassword, setNewPassword] = useState('');
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  // Redirect if not admin
  useEffect(() => {
    if (user && user.role !== 'ADMIN') {
      navigate('/dashboard');
    }
  }, [user, navigate]);

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await adminService.getAllUsers();
      setUsers(data);
    } catch (err) {
      setError('Помилка завантаження користувачів');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleActive = async (userId: number) => {
    try {
      await adminService.toggleUserActive(userId);
      await loadUsers();
    } catch (err) {
      setError('Помилка зміни статусу користувача');
    }
  };

  const handleChangePassword = async () => {
    if (!selectedUser || !newPassword || newPassword.length < 8) {
      setError('Пароль має бути не менше 8 символів');
      return;
    }

    try {
      await adminService.changeUserPassword(selectedUser.id, newPassword);
      setShowPasswordModal(false);
      setNewPassword('');
      setSelectedUser(null);
      alert('Пароль успішно змінено!');
    } catch (err) {
      setError('Помилка зміни пароля');
    }
  };

  const handleDeleteUser = async (userId: number, username: string) => {
    if (!confirm(`Ви впевнені, що хочете видалити користувача ${username}?`)) {
      return;
    }

    try {
      await adminService.deleteUser(userId);
      await loadUsers();
    } catch (err) {
      setError('Помилка видалення користувача');
    }
  };

  const stats = {
    totalUsers: users.length,
    activeUsers: users.filter(u => u.active).length,
    adminUsers: users.filter(u => u.role === 'ADMIN').length,
    doctorUsers: users.filter(u => u.role === 'DOCTOR').length,
  };

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1><Shield size={32} /> Адміністративна панель</h1>
        <p>Вітаємо, {user?.username}!</p>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="stats-grid">
        <div className="stat-card">
          <Users className="stat-icon" />
          <div className="stat-content">
            <h3>Всього користувачів</h3>
            <p className="stat-value">{stats.totalUsers}</p>
          </div>
        </div>

        <div className="stat-card">
          <Activity className="stat-icon" />
          <div className="stat-content">
            <h3>Активні</h3>
            <p className="stat-value">{stats.activeUsers}</p>
          </div>
        </div>

        <div className="stat-card">
          <Shield className="stat-icon" />
          <div className="stat-content">
            <h3>Адміністратори</h3>
            <p className="stat-value">{stats.adminUsers}</p>
          </div>
        </div>

        <div className="stat-card">
          <TrendingUp className="stat-icon" />
          <div className="stat-content">
            <h3>Лікарі</h3>
            <p className="stat-value">{stats.doctorUsers}</p>
          </div>
        </div>
      </div>

      <div className="card">
        <h2>Управління користувачами</h2>

        {loading ? (
          <p>Завантаження...</p>
        ) : (
          <div className="table-container">
            <table className="users-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Ім'я користувача</th>
                  <th>Email</th>
                  <th>Ім'я</th>
                  <th>Роль</th>
                  <th>Статус</th>
                  <th>Дії</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td>{u.username}</td>
                    <td>{u.email}</td>
                    <td>{u.firstName} {u.lastName}</td>
                    <td>
                      <span className={`role-badge role-${u.role.toLowerCase()}`}>
                        {u.role}
                      </span>
                    </td>
                    <td>
                      <span className={`status-badge ${u.active ? 'active' : 'inactive'}`}>
                        {u.active ? 'Активний' : 'Заблокований'}
                      </span>
                    </td>
                    <td>
                      <div className="action-buttons">
                        <button
                          onClick={() => handleToggleActive(u.id)}
                          className="btn btn-sm btn-secondary"
                          disabled={u.id === user?.id}
                        >
                          {u.active ? 'Блокувати' : 'Активувати'}
                        </button>
                        <button
                          onClick={() => {
                            setSelectedUser(u);
                            setShowPasswordModal(true);
                          }}
                          className="btn btn-sm btn-primary"
                        >
                          Змінити пароль
                        </button>
                        <button
                          onClick={() => handleDeleteUser(u.id, u.username)}
                          className="btn btn-sm btn-danger"
                          disabled={u.id === user?.id}
                        >
                          Видалити
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Password Change Modal */}
      {showPasswordModal && selectedUser && (
        <div className="modal-overlay" onClick={() => setShowPasswordModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Змінити пароль для {selectedUser.username}</h3>
            <div className="form-group">
              <label htmlFor="newPassword">Новий пароль</label>
              <input
                type="password"
                id="newPassword"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Мінімум 8 символів"
                minLength={8}
              />
            </div>
            <div className="modal-actions">
              <button onClick={handleChangePassword} className="btn btn-primary">
                Змінити
              </button>
              <button onClick={() => {
                setShowPasswordModal(false);
                setNewPassword('');
                setSelectedUser(null);
              }} className="btn btn-secondary">
                Скасувати
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
