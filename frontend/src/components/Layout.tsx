import { type ReactNode } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Activity, Home, History, LogOut, User } from 'lucide-react';

interface LayoutProps {
  children: ReactNode;
}

export const Layout = ({ children }: LayoutProps) => {
  const { user, logout, isAuthenticated } = useAuth();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  };

  if (!isAuthenticated) {
    return <>{children}</>;
  }

  return (
    <div className="app-layout">
      <nav className="navbar">
        <div className="nav-brand">
          <Activity size={32} />
          <span>Аналіз стресу</span>
        </div>

        <div className="nav-links">
          <Link
            to="/dashboard"
            className={location.pathname === '/dashboard' ? 'nav-link active' : 'nav-link'}
          >
            <Home size={20} />
            <span>Головна</span>
          </Link>

          <Link
            to="/history"
            className={location.pathname === '/history' ? 'nav-link active' : 'nav-link'}
          >
            <History size={20} />
            <span>Історія</span>
          </Link>
        </div>

        <div className="nav-user">
          <div className="user-info">
            <User size={20} />
            <span>{user?.username}</span>
          </div>
          <button onClick={handleLogout} className="btn-logout">
            <LogOut size={20} />
            <span>Вийти</span>
          </button>
        </div>
      </nav>

      <main className="main-content">{children}</main>

      <footer className="footer">
        <p>© 2024 Biometric Stress Analysis System</p>
      </footer>
    </div>
  );
};
