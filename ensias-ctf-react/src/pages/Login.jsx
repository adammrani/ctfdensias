import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotif } from '../context/NotifContext';
import { authAPI } from '../api';
import './Auth.css';

export default function Login() {
  const { saveSession } = useAuth();
  const { showNotification } = useNotif();
  const navigate = useNavigate();
  const [email, setEmail]     = useState('');
  const [pass, setPass]       = useState('');
  const [loading, setLoading] = useState(false);

  async function handleLogin() {
    if (!email || !pass) { showNotification('Please fill all fields', 'error'); return; }
    setLoading(true);
    try {
      const res = await authAPI.login(email, pass);
      saveSession(res.token, { username: res.username, role: res.role });
      showNotification(`Welcome back, ${res.username}!`, 'success');
      navigate('/challenges');
    } catch (e) {
      showNotification(e.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-box">
        <h2 className="auth-title">Access Terminal</h2>
        <p className="auth-subtitle">// AUTHENTICATE TO CONTINUE</p>

        <div className="form-group">
          <label className="form-label">Email</label>
          <input
            className="form-input" type="email" placeholder="student@ensias.ma"
            value={email} onChange={e => setEmail(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleLogin()}
          />
        </div>
        <div className="form-group">
          <label className="form-label">Password</label>
          <input
            className="form-input" type="password" placeholder="••••••••"
            value={pass} onChange={e => setPass(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleLogin()}
          />
        </div>

        <button className="btn-primary form-submit" onClick={handleLogin} disabled={loading}>
          {loading ? 'Authenticating...' : 'Authenticate'}
        </button>
        <p className="auth-switch">No account? <Link to="/register">Register here</Link></p>
      </div>
    </div>
  );
}
