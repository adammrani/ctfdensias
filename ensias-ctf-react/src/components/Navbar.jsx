import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotif } from '../context/NotifContext';
import { authAPI } from '../api';
import './Navbar.css';

export default function Navbar() {
  const { isLoggedIn, isAdmin, user, clearSession } = useAuth();
  const { showNotification } = useNotif();
  const navigate = useNavigate();
  const location = useLocation();

  const active = (path) => location.pathname === path ? 'active' : '';

  async function handleLogout() {
    try { await authAPI.logout(); } catch (_) {}
    clearSession();
    showNotification('Logged out', 'success');
    navigate('/');
  }

  return (
    <nav>
      <div className="nav-logo" onClick={() => navigate(isLoggedIn ? '/challenges' : '/')}>
        <span className="bracket">[</span>ENSIAS<span className="red">CTF</span><span className="bracket">]</span>
        {isAdmin && <span className="admin-badge">ADMIN</span>}
      </div>

      <ul className="nav-links">
        <li><Link className={active('/challenges')} to="/challenges">Challenges</Link></li>
        <li><Link className={active('/scoreboard')} to="/scoreboard">Scoreboard</Link></li>
        <li><Link className={active('/rules')}      to="/rules">Rules</Link></li>
        <li><Link className={active('/about')}      to="/about">About ENSIAS</Link></li>

        {isLoggedIn && (
          <li><Link className={active('/tickets')} to="/tickets">Tickets</Link></li>
        )}
        {isAdmin && (
          <li><Link className={active('/admin')} to="/admin">Admin</Link></li>
        )}

        {!isLoggedIn ? (
          <>
            <li><Link className={active('/login')}    to="/login">Login</Link></li>
            <li><Link className={active('/register')} to="/register">Register</Link></li>
          </>
        ) : (
          <>
            <li>
              <div className="user-indicator">
                <span className="dot"></span>
                <span>{user?.username}</span>
              </div>
            </li>
            <li><a onClick={handleLogout} style={{ cursor: 'pointer' }}>Logout</a></li>
          </>
        )}
      </ul>
    </nav>
  );
}
