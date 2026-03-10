import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { scoreboardAPI } from '../api';
import './Scoreboard.css';

export default function Scoreboard() {
  const { user } = useAuth();
  const [entries, setEntries] = useState([]);
  const [error, setError]     = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    scoreboardAPI.public()
      .then(data => { setEntries(data); setLoading(false); })
      .catch(e  => { setError(e.message); setLoading(false); });
  }, []);

  const rankIcon = (i) => i === 0 ? '🥇' : i === 1 ? '🥈' : i === 2 ? '🥉' : i + 1;

  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">Score<span>board</span></h2>
        <span style={{ fontFamily: 'var(--mono)', fontSize: '0.75rem', color: 'var(--text-muted)' }}>Team rankings</span>
      </div>

      <div className="scoreboard-wrap">
        {loading && <p className="sb-msg">Loading...</p>}
        {error   && <p className="sb-msg" style={{ color: 'var(--red-bright)' }}>
          {error.toLowerCase().includes('hidden')
            ? '🔒 Scoreboard is currently hidden by the administrator'
            : error}
        </p>}

        {!loading && !error && (
          <table className="scoreboard-table">
            <thead>
              <tr><th>#</th><th>Team</th><th>Members</th><th>Solves</th><th>Score</th></tr>
            </thead>
            <tbody>
              {entries.length === 0 ? (
                <tr><td colSpan={5} className="sb-msg">No teams yet. Register and create a team!</td></tr>
              ) : entries.map((e, i) => {
                const mine = user && e.members?.includes(user.username);
                return (
                  <tr key={i} style={{ background: mine ? 'rgba(204,0,0,0.06)' : '' }}>
                    <td className={`rank-cell rank-${i + 1}`}>{rankIcon(i)}</td>
                    <td style={{ fontWeight: 500 }}>
                      {e.teamName}
                      {mine && <span className="your-team">[your team]</span>}
                    </td>
                    <td className="members-cell">{(e.members || []).join(', ') || '—'}</td>
                    <td style={{ color: 'var(--text-muted)' }}>{e.solveCount}</td>
                    <td className="score-cell">{e.totalPoints}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
