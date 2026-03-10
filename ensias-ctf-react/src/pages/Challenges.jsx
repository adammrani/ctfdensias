import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotif } from '../context/NotifContext';
import { challengeAPI } from '../api';
import ChallengeModal from '../components/ChallengeModal';
import './Challenges.css';

const CATEGORIES = ['all', 'web', 'crypto', 'forensics', 'reverse', 'pwn', 'misc'];

function computePoints(c) {
  if (!c?.initialPoints) return c?.points ?? 0;
  const solves  = c.solveCount ?? 0;
  const decay   = c.decayRate  ?? 0.05;
  const minimum = c.minimumPoints ?? 50;
  return Math.max(minimum, Math.round(c.initialPoints * Math.exp(-decay * solves)));
}

export default function Challenges() {
  const { isLoggedIn, solvedIds, markSolved } = useAuth();
  const { showNotification } = useNotif();
  const navigate = useNavigate();
  const [challenges, setChallenges] = useState([]);
  const [filter, setFilter]         = useState('all');
  const [selected, setSelected]     = useState(null);
  const [loading, setLoading]       = useState(true);
  const [error, setError]           = useState('');

  useEffect(() => {
    if (!isLoggedIn) { navigate('/login'); return; }
    challengeAPI.list()
      .then(data => { setChallenges(data); setLoading(false); })
      .catch(e  => { setError(e.message); setLoading(false); });
  }, [isLoggedIn, navigate]);

  const filtered = filter === 'all'
    ? challenges
    : challenges.filter(c => c.category?.toLowerCase() === filter);

  function handleSolved(id) {
    markSolved(id);
    setChallenges(prev => prev.map(c => c.id === id ? { ...c, solveCount: (c.solveCount || 0) + 1 } : c));
  }

  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">Chall<span>enges</span></h2>
        <span className="header-info">{challenges.length} total · {solvedIds.size} solved</span>
      </div>

      <div className="filter-bar">
        {CATEGORIES.map(cat => (
          <button
            key={cat}
            className={`filter-btn ${filter === cat ? 'active' : ''}`}
            onClick={() => setFilter(cat)}
          >
            {cat}
          </button>
        ))}
      </div>

      {loading && (
        <div className="challenges-placeholder">
          <span style={{ color: 'var(--red-bright)' }}>&gt;</span> Loading challenges...
        </div>
      )}
      {error && (
        <div className="challenges-placeholder" style={{ color: 'var(--red-bright)' }}>Error: {error}</div>
      )}

      {!loading && !error && (
        <div className="challenges-grid">
          {filtered.length === 0
            ? <div className="challenges-placeholder">No challenges in this category.</div>
            : filtered.map(c => {
                const solved = solvedIds.has(c.id);
                const diff   = (c.difficulty || 'easy').toLowerCase();
                const pts    = computePoints(c);
                return (
                  <div
                    key={c.id}
                    className={`challenge-card ${solved ? 'solved' : ''}`}
                    onClick={() => setSelected(c)}
                  >
                    <div className="card-top">
                      <span className="card-category">{c.category || 'misc'}</span>
                      <div className="card-points">{pts}<span> pts</span></div>
                    </div>
                    <div className="card-title">{c.title}</div>
                    <div className="card-desc">
                      {(c.description || '').substring(0, 90)}
                      {(c.description || '').length > 90 ? '...' : ''}
                    </div>
                    <div className="card-footer">
                      <span className={`difficulty ${diff}`}>{diff}</span>
                      {solved
                        ? <span className="solved-badge">✓ SOLVED</span>
                        : <span className="solves-count">{c.solveCount ?? 0} solve{c.solveCount === 1 ? '' : 's'}</span>
                      }
                    </div>
                  </div>
                );
              })
          }
        </div>
      )}

      {selected && (
        <ChallengeModal
          challenge={selected}
          onClose={() => setSelected(null)}
          onSolved={handleSolved}
        />
      )}
    </div>
  );
}
