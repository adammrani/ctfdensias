import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotif } from '../context/NotifContext';
import { authAPI, teamAPI } from '../api';
import './Auth.css';

export default function Register() {
  const { saveSession } = useAuth();
  const { showNotification } = useNotif();
  const navigate = useNavigate();

  const [step, setStep]       = useState(1); // 1 = account, 2 = team
  const [teamTab, setTeamTab] = useState('create'); // 'create' | 'join'
  const [loading, setLoading] = useState(false);

  // Step 1 fields
  const [username, setUsername] = useState('');
  const [email, setEmail]       = useState('');
  const [pass, setPass]         = useState('');
  const [registeredUser, setRegisteredUser] = useState('');

  // Step 2 fields
  const [teamName, setTeamName]   = useState('');
  const [teamPass, setTeamPass]   = useState('');
  const [joinName, setJoinName]   = useState('');
  const [joinPass, setJoinPass]   = useState('');

  async function handleAccountCreate() {
    if (!username || !email || !pass) { showNotification('Please fill all fields', 'error'); return; }
    if (pass.length < 6) { showNotification('Password must be at least 6 characters', 'error'); return; }
    setLoading(true);
    try {
      await authAPI.register(username, email, pass);
      const res = await authAPI.login(email, pass);
      saveSession(res.token, { username: res.username, role: res.role });
      setRegisteredUser(res.username);
      setStep(2);
      showNotification('Account created! Now set up your team.', 'success');
    } catch (e) {
      showNotification(e.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  async function handleTeamCreate() {
    if (!teamName || !teamPass) { showNotification('Enter team name and password', 'error'); return; }
    if (teamPass.length < 4) { showNotification('Team password must be at least 4 characters', 'error'); return; }
    setLoading(true);
    try {
      await teamAPI.create(teamName, teamPass);
      showNotification(`Team "${teamName}" created! Welcome!`, 'success');
      navigate('/challenges');
    } catch (e) {
      showNotification(e.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  async function handleTeamJoin() {
    if (!joinName || !joinPass) { showNotification('Enter team name and password', 'error'); return; }
    setLoading(true);
    try {
      await teamAPI.join(joinName, joinPass);
      showNotification(`Joined team "${joinName}"! Welcome!`, 'success');
      navigate('/challenges');
    } catch (e) {
      showNotification(e.message, 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-box" style={{ maxWidth: 480 }}>

        {/* Step indicator */}
        <div className="step-indicator">
          <div className={`step-item ${step === 1 ? 'active' : ''}`}>01 / ACCOUNT</div>
          <div className={`step-item ${step === 2 ? 'active' : ''}`}>02 / TEAM</div>
        </div>

        {/* ---- STEP 1 ---- */}
        {step === 1 && (
          <>
            <h2 className="auth-title">Join the Game</h2>
            <p className="auth-subtitle">// CREATE YOUR ACCOUNT</p>

            <div className="form-group">
              <label className="form-label">Username</label>
              <input className="form-input" type="text" placeholder="h4cker_name"
                value={username} onChange={e => setUsername(e.target.value)} />
            </div>
            <div className="form-group">
              <label className="form-label">Email</label>
              <input className="form-input" type="email" placeholder="student@ensias.ma"
                value={email} onChange={e => setEmail(e.target.value)} />
            </div>
            <div className="form-group">
              <label className="form-label">Password</label>
              <input className="form-input" type="password" placeholder="Min. 6 characters"
                value={pass} onChange={e => setPass(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleAccountCreate()} />
            </div>
            <button className="btn-primary form-submit" onClick={handleAccountCreate} disabled={loading}>
              {loading ? 'Creating account...' : 'Next → Team Setup'}
            </button>
            <p className="auth-switch">Already have an account? <Link to="/login">Login here</Link></p>
          </>
        )}

        {/* ---- STEP 2 ---- */}
        {step === 2 && (
          <>
            <h2 className="auth-title">Team Setup</h2>
            <p className="auth-subtitle">// CREATE OR JOIN A TEAM</p>

            <div className="team-toggle">
              <button
                className={teamTab === 'create' ? 'active' : ''}
                onClick={() => setTeamTab('create')}
              >+ Create Team</button>
              <button
                className={teamTab === 'join' ? 'active' : ''}
                onClick={() => setTeamTab('join')}
              >→ Join Team</button>
            </div>

            {teamTab === 'create' && (
              <>
                <div className="form-group">
                  <label className="form-label">Team Name</label>
                  <input className="form-input" placeholder="Team name (unique)"
                    value={teamName} onChange={e => setTeamName(e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">Team Password</label>
                  <input className="form-input" type="password" placeholder="Share this with teammates"
                    value={teamPass} onChange={e => setTeamPass(e.target.value)} />
                  <p className="hint-note">// Share this password with your teammates so they can join</p>
                </div>
                <button className="btn-primary form-submit" onClick={handleTeamCreate} disabled={loading}>
                  {loading ? 'Creating team...' : 'Create Team & Enter'}
                </button>
              </>
            )}

            {teamTab === 'join' && (
              <>
                <div className="form-group">
                  <label className="form-label">Team Name</label>
                  <input className="form-input" placeholder="Exact team name"
                    value={joinName} onChange={e => setJoinName(e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">Team Password</label>
                  <input className="form-input" type="password" placeholder="Team password"
                    value={joinPass} onChange={e => setJoinPass(e.target.value)}
                    onKeyDown={e => e.key === 'Enter' && handleTeamJoin()} />
                </div>
                <button className="btn-primary form-submit" onClick={handleTeamJoin} disabled={loading}>
                  {loading ? 'Joining team...' : 'Join Team & Enter'}
                </button>
              </>
            )}

            <p className="step2-note">
              Logged in as <span style={{ color: 'var(--white)' }}>{registeredUser}</span>
            </p>
          </>
        )}

      </div>
    </div>
  );
}
