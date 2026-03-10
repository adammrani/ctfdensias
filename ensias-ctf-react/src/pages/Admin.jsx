import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotif } from '../context/NotifContext';
import { challengeAPI, scoreboardAPI, ticketAPI, competitionAPI } from '../api';
import './Admin.css';

function computePoints(c) {
  if (!c?.initialPoints) return c?.points ?? 0;
  const minimum = c.minimumPoints ?? 50;
  return Math.max(minimum, Math.round(c.initialPoints * Math.exp(-(c.decayRate ?? 0.05) * (c.solveCount ?? 0))));
}

// ---- Tab: Challenges ----
function ChallengesTab({ showNotification }) {
  const [list, setList] = useState([]);
  const [form, setForm] = useState({
    title: '', category: '', difficulty: 'EASY',
    initialPoints: '', minimumPoints: '', decayRate: '',
    flag: '', description: '',
    hint1: '', hint1Cost: '', hint2: '', hint2Cost: '',
  });
  const [challengeFile, setChallengeFile] = useState(null);

  useEffect(() => { loadList(); }, []);

  async function loadList() {
    try { setList(await challengeAPI.listAll()); } catch (e) { showNotification(e.message, 'error'); }
  }

  async function handleCreate() {
    const { title, category, flag, initialPoints, minimumPoints } = form;
    if (!title || !category || !flag || !initialPoints || !minimumPoints) {
      showNotification('Fill all required (*) fields', 'error'); return;
    }
    try {
      const ch = await challengeAPI.create({
        title,
        description: form.description,
        category,
        difficulty: form.difficulty,
        initialPoints: parseInt(initialPoints),
        minimumPoints: parseInt(minimumPoints),
        decayRate: parseFloat(form.decayRate) || 0.05,
      }, challengeFile);

      await challengeAPI.addFlag(ch.id, flag);
      if (form.hint1) await challengeAPI.addHint(ch.id, form.hint1, parseInt(form.hint1Cost) || 0);
      if (form.hint2) await challengeAPI.addHint(ch.id, form.hint2, parseInt(form.hint2Cost) || 0);

      showNotification(`Challenge "${title}" created!`, 'success');
      setForm({ title:'', category:'', difficulty:'EASY', initialPoints:'', minimumPoints:'', decayRate:'', flag:'', description:'', hint1:'', hint1Cost:'', hint2:'', hint2Cost:'' });
      setChallengeFile(null);
      loadList();
    } catch (e) { showNotification(e.message, 'error'); }
  }

  function f(key) {
    return { value: form[key], onChange: e => setForm(p => ({ ...p, [key]: e.target.value })) };
  }

  return (
    <>
      <div className="admin-section">
        <div className="admin-section-title">// Create New Challenge</div>
        <div className="admin-form-grid">
          <div><label className="form-label">Title *</label><input className="form-input" placeholder="Challenge name" {...f('title')} /></div>
          <div><label className="form-label">Category *</label><input className="form-input" placeholder="web / crypto / pwn / misc..." {...f('category')} /></div>
          <div>
            <label className="form-label">Difficulty</label>
            <select className="form-input" {...f('difficulty')}>
              <option>EASY</option><option>MEDIUM</option><option>HARD</option>
            </select>
          </div>
          <div><label className="form-label">Initial Points *</label><input className="form-input" type="number" placeholder="500" {...f('initialPoints')} /></div>
          <div><label className="form-label">Minimum Points *</label><input className="form-input" type="number" placeholder="50" {...f('minimumPoints')} /></div>
          <div><label className="form-label">Decay Rate</label><input className="form-input" type="number" step="0.01" placeholder="0.05" {...f('decayRate')} /></div>
          <div><label className="form-label">Flag * (ENSIAS{'{}'})</label><input className="form-input" placeholder="ENSIAS{flag_here}" {...f('flag')} /></div>
          <div><label className="form-label">Hint 1 (optional)</label><input className="form-input" placeholder="First hint text" {...f('hint1')} /></div>
          <div className="full">
            <label className="form-label">Description</label>
            <textarea className="form-input" rows={3} placeholder="Challenge description..." {...f('description')} />
          </div>
          <div><label className="form-label">Hint 1 Cost (pts)</label><input className="form-input" type="number" placeholder="0 = free" {...f('hint1Cost')} /></div>
          <div><label className="form-label">Hint 2 (optional)</label><input className="form-input" placeholder="Second hint text" {...f('hint2')} /></div>
          <div><label className="form-label">Hint 2 Cost (pts)</label><input className="form-input" type="number" placeholder="0 = free" {...f('hint2Cost')} /></div>
          <div className="full">
            <label className="form-label">Attachment (optional)</label>
            <input
              className="form-input"
              type="file"
              onChange={e => setChallengeFile(e.target.files[0] || null)}
            />
            {challengeFile && (
              <div style={{ fontFamily: 'var(--mono)', fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.4rem' }}>
                📎 {challengeFile.name} ({(challengeFile.size / 1024).toFixed(1)} KB)
                <span
                  onClick={() => setChallengeFile(null)}
                  style={{ marginLeft: '0.75rem', color: 'var(--red-bright)', cursor: 'pointer' }}
                >✕ Remove</span>
              </div>
            )}
          </div>
        </div>
        <button className="btn-primary" style={{ marginTop: '1rem' }} onClick={handleCreate}>
          Create Challenge
        </button>
      </div>

      <div className="admin-section">
        <div className="admin-section-title">// Manage Challenges</div>
        {list.length === 0
          ? <p style={{ fontFamily: 'var(--mono)', color: 'var(--text-muted)' }}>No challenges yet.</p>
          : list.map(c => (
            <div className="admin-row" key={c.id}>
              <div>
                <span style={{ color: 'var(--red-bright)' }}>[{c.category}]</span> {c.title}
                <span style={{ color: 'var(--text-muted)' }}> · {computePoints(c)}pts · {c.difficulty}</span>
                {c.fileName && <span style={{ color: 'var(--text-muted)' }}> · 📎 {c.fileName}</span>}
                {!c.isActive && <span style={{ color: 'var(--yellow)' }}> · DISABLED</span>}
              </div>
              <div className="admin-actions">
                <button className="btn-sm" onClick={async () => { await challengeAPI.toggle(c.id); loadList(); }}>
                  {c.isActive ? 'Disable' : 'Enable'}
                </button>
                <button className="btn-danger" onClick={async () => {
                  if (!confirm('Delete this challenge?')) return;
                  await challengeAPI.delete(c.id); loadList();
                }}>Delete</button>
              </div>
            </div>
          ))
        }
      </div>
    </>
  );
}

// ---- Tab: Scoreboard ----
function ScoreboardTab({ showNotification }) {
  const [entries, setEntries] = useState(null);

  async function handleToggle(visible) {
    try {
      const comps = await competitionAPI.list();
      if (!comps.length) { showNotification('No active competition found', 'error'); return; }
      await competitionAPI.toggleScoreboard(comps[0].id, visible);
      showNotification(`Scoreboard ${visible ? 'visible' : 'hidden'}`, 'success');
    } catch (e) { showNotification(e.message, 'error'); }
  }

  return (
    <>
      <div className="admin-section">
        <div className="admin-section-title">// Scoreboard Visibility</div>
        <p style={{ fontFamily: 'var(--mono)', fontSize: '0.82rem', color: 'var(--text-muted)', marginBottom: '1.25rem' }}>
          Toggle whether participants can see the scoreboard.
        </p>
        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
          <button className="btn-green" onClick={() => handleToggle(true)}>✓ Show Scoreboard</button>
          <button className="btn-danger" style={{ padding: '0.6rem 1.2rem' }} onClick={() => handleToggle(false)}>✕ Hide Scoreboard</button>
        </div>
      </div>
      <div className="admin-section">
        <div className="admin-section-title">// Current Rankings (Admin View)</div>
        {entries === null
          ? <button className="btn-outline" onClick={async () => { try { setEntries(await scoreboardAPI.admin()); } catch (e) { showNotification(e.message, 'error'); } }}>Load Rankings</button>
          : entries.length === 0
          ? <p style={{ fontFamily: 'var(--mono)', color: 'var(--text-muted)' }}>No scores yet.</p>
          : (
            <table className="scoreboard-table">
              <thead><tr><th>#</th><th>Team</th><th>Members</th><th>Solves</th><th>Score</th></tr></thead>
              <tbody>
                {entries.map((e, i) => (
                  <tr key={i}>
                    <td>{i + 1}</td>
                    <td>{e.teamName}</td>
                    <td style={{ fontFamily: 'var(--mono)', fontSize: '0.72rem', color: 'var(--text-muted)' }}>{(e.members || []).join(', ')}</td>
                    <td>{e.solveCount}</td>
                    <td style={{ color: 'var(--red-bright)', fontWeight: 700 }}>{e.totalPoints}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )
        }
      </div>
    </>
  );
}

// ---- Tab: Tickets ----
function TicketsTab({ showNotification }) {
  const [tickets, setTickets] = useState(null);

  async function load() {
    try { setTickets(await ticketAPI.listAll()); }
    catch (_) { setTickets([]); }
  }

  async function handleClose(id) {
    try { await ticketAPI.close(id); load(); showNotification('Ticket closed', 'success'); }
    catch (e) { showNotification(e.message, 'error'); }
  }

  return (
    <div className="admin-section">
      <div className="admin-section-title">// All Support Tickets</div>
      {tickets === null
        ? <button className="btn-outline" onClick={load}>Load Tickets</button>
        : tickets.length === 0
        ? <p style={{ fontFamily: 'var(--mono)', color: 'var(--text-muted)' }}>No tickets yet.</p>
        : tickets.map(t => (
          <div className="admin-row ticket-row" key={t.id}>
            <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
              <strong>{t.subject}</strong>
              <span className={`ticket-status ${t.status === 'OPEN' ? 'open' : 'closed'}`}>{t.status}</span>
            </div>
            <div style={{ fontFamily: 'var(--mono)', fontSize: '0.72rem', color: 'var(--text-muted)' }}>
              {t.username} · {t.category} · {new Date(t.createdAt).toLocaleString()}
            </div>
            <div style={{ fontSize: '0.82rem', color: '#aaa' }}>{t.message}</div>
            {t.status === 'OPEN' && <button className="btn-sm" onClick={() => handleClose(t.id)}>Mark Resolved</button>}
          </div>
        ))
      }
    </div>
  );
}

// ---- Tab: Competition ----
function CompetitionTab({ showNotification }) {
  const [comp, setComp] = useState(null);

  useEffect(() => {
    competitionAPI.list()
      .then(data => setComp(data[0] || null))
      .catch(e => showNotification(e.message, 'error'));
  }, []);

  if (!comp) return (
    <div className="admin-section">
      <div className="admin-section-title">// Competition Settings</div>
      <p style={{ fontFamily: 'var(--mono)', color: 'var(--text-muted)' }}>No competition configured.</p>
    </div>
  );

  return (
    <div className="admin-section">
      <div className="admin-section-title">// Competition Settings</div>
      <div className="comp-info">
        <div>Name: <span className="val">{comp.name || '—'}</span></div>
        <div>Start: <span className="val">{comp.startTime ? new Date(comp.startTime).toLocaleString() : '—'}</span></div>
        <div>End: <span className="val">{comp.endTime ? new Date(comp.endTime).toLocaleString() : '—'}</span></div>
        <div>Active: <span style={{ color: comp.isActive ? 'var(--green)' : 'var(--red-bright)' }}>{comp.isActive ? 'Yes' : 'No'}</span></div>
        <div>Scoreboard: <span style={{ color: comp.isScoreboardVisible ? 'var(--green)' : 'var(--yellow)' }}>{comp.isScoreboardVisible ? 'Visible' : 'Hidden'}</span></div>
      </div>
    </div>
  );
}

// ---- Main Admin Page ----
const TABS = ['challenges', 'scoreboard', 'tickets', 'competition'];

export default function Admin() {
  const { isAdmin } = useAuth();
  const { showNotification } = useNotif();
  const navigate = useNavigate();
  const [tab, setTab] = useState('challenges');

  useEffect(() => {
    if (!isAdmin) navigate('/');
  }, [isAdmin, navigate]);

  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">Admin <span>Panel</span></h2>
      </div>
      <div className="admin-content">
        <div className="tab-bar">
          {TABS.map(t => (
            <button key={t} className={`tab-btn ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>
        {tab === 'challenges'  && <ChallengesTab  showNotification={showNotification} />}
        {tab === 'scoreboard'  && <ScoreboardTab  showNotification={showNotification} />}
        {tab === 'tickets'     && <TicketsTab      showNotification={showNotification} />}
        {tab === 'competition' && <CompetitionTab  showNotification={showNotification} />}
      </div>
    </div>
  );
}