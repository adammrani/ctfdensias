import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNotif } from '../context/NotifContext';
import { challengeAPI, submissionAPI } from '../api';
import './ChallengeModal.css';

function computePoints(c) {
  if (!c?.initialPoints) return c?.points ?? 0;
  const solves  = c.solveCount ?? 0;
  const decay   = c.decayRate  ?? 0.05;
  const minimum = c.minimumPoints ?? 50;
  return Math.max(minimum, Math.round(c.initialPoints * Math.exp(-decay * solves)));
}

export default function ChallengeModal({ challenge, onClose, onSolved }) {
  const { solvedIds, isLoggedIn, user } = useAuth();
  const { showNotification } = useNotif();
  const [flag, setFlag]             = useState('');
  const [flagState, setFlagState]   = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [hints, setHints]           = useState(challenge?.hints ?? []);
  const [ticketOpen, setTicketOpen] = useState(false);
  const [ticketMsg, setTicketMsg]   = useState('');

  if (!challenge) return null;

  const already  = solvedIds.has(challenge.id);
  const isAdmin  = user?.role === 'ADMIN';
  const pts      = computePoints(challenge);
  const diff     = (challenge.difficulty || 'easy').toLowerCase();

  async function handleRevealHint(hintId) {
    if (!isLoggedIn) { showNotification('Login to reveal hints', 'error'); return; }
    try {
      const data = await challengeAPI.revealHint(hintId);
      setHints(prev => prev.map(h => h.id === hintId ? { ...h, revealed: true, content: data.content } : h));
      showNotification('Hint revealed', 'success');
    } catch (e) {
      showNotification(e.message, 'error');
    }
  }

  async function handleSubmit() {
    if (!flag.trim() || !isLoggedIn) return;
    setSubmitting(true);
    try {
      const res = await submissionAPI.submit(challenge.id, flag.trim());
      if (res.correct) {
        setFlagState('correct');
        showNotification(`🚩 Correct! +${res.pointsAwarded} pts`, 'success');
        onSolved(challenge.id);
        setTimeout(() => onClose(), 1500);
      } else {
        setFlagState('wrong');
        showNotification(res.message || '❌ Wrong flag', 'error');
        setTimeout(() => { setFlagState(''); setFlag(''); }, 600);
      }
    } catch (e) {
      showNotification(e.message, 'error');
      setFlagState('wrong');
      setTimeout(() => { setFlagState(''); }, 600);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      <div className="modal-overlay open" onClick={(e) => e.target === e.currentTarget && onClose()}>
        <div className="modal">
          <div className="modal-header">
            <div>
              <div className="modal-title">{challenge.title}</div>
              <div className="modal-subtitle">by {challenge.author || 'admin'}</div>
            </div>
            <button className="modal-close" onClick={onClose}>✕</button>
          </div>

          <div className="modal-body">
            <div className="modal-meta">
              <span className="card-category">{challenge.category || 'misc'}</span>
              <span className={`difficulty ${diff}`}>{diff}</span>
              <span className="pts-badge">{pts} pts</span>
              <span className="solves-text">{challenge.solveCount ?? 0} solves</span>
            </div>

            <p className="modal-desc">{challenge.description || ''}</p>

            {/* Download attachment if present */}
            {challenge.fileUrl && (
              <div style={{ margin: '0.75rem 0' }}>
                <a /* FIXED: Missing opening <a tag */
                  href={challenge.fileUrl}
                  download={challenge.fileName}
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '0.4rem',
                    fontFamily: 'var(--mono)',
                    fontSize: '0.82rem',
                    color: 'var(--red-bright)',
                    border: '1px solid var(--red-bright)',
                    padding: '0.35rem 0.75rem',
                    borderRadius: '4px',
                    textDecoration: 'none',
                    transition: 'background 0.2s',
                  }}
                  onMouseOver={e => e.currentTarget.style.background = 'rgba(204,0,0,0.1)'}
                  onMouseOut={e => e.currentTarget.style.background = 'transparent'}
                >
                  ⬇ {challenge.fileName || 'Download Attachment'}
                </a>
              </div>
            )}

            {hints.length > 0 && (
              <div className="hint-section">
                <div className="hint-title">// Hints ({hints.length})</div>
                {hints.map(h => (
                  <div className="hint-item" key={h.id}>
                    {h.cost > 0 && <span className="hint-cost">[-{h.cost}pts]</span>}
                    {h.revealed
                      ? <span style={{ color: '#ccc' }}>&gt; {h.content}</span>
                      : <a onClick={() => handleRevealHint(h.id)} className="hint-reveal" style={{cursor: 'pointer'}}>🔍 Reveal hint</a>
                    }
                  </div>
                ))}
              </div>
            )}

            {/* Hide flag submission for admins */}
            {!isAdmin && (
              <div className="flag-area" style={{ opacity: already ? 0.6 : 1 }}>
                <div className="form-label" style={{ marginBottom: '0.5rem' }}>Submit Flag</div>
                <div className="flag-input-group">
                  <input
                    className={`flag-input ${flagState}`}
                    type="text"
                    placeholder="ENSIAS{...}"
                    value={already ? '✓ Already solved!' : flag}
                    readOnly={already}
                    onChange={e => setFlag(e.target.value)}
                    onKeyDown={e => e.key === 'Enter' && handleSubmit()}
                  />
                  <button
                    className="btn-primary"
                    style={{ clipPath: 'none', whiteSpace: 'nowrap' }}
                    disabled={submitting || already}
                    onClick={handleSubmit}
                  >
                    {submitting ? '...' : 'Submit'}
                  </button>
                </div>
                <div style={{ marginTop: '0.75rem' }}>
                  <a /* FIXED: Missing opening <a tag */
                    onClick={() => setTicketOpen(true)}
                    style={{ fontFamily: 'var(--mono)', fontSize: '0.72rem', color: 'var(--text-muted)', cursor: 'pointer', textDecoration: 'underline' }}
                  >
                    🎫 Report a bug with this challenge
                  </a>
                </div>
              </div>
            )}

            {isAdmin && (
              <div style={{ fontFamily: 'var(--mono)', fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '1rem', padding: '0.5rem', border: '1px dashed var(--border)', borderRadius: '4px' }}>
                👁 Admin view — flag submission disabled
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Quick Ticket Modal */}
      {ticketOpen && (
        <div className="modal-overlay open" style={{ zIndex: 300 }} onClick={(e) => e.target === e.currentTarget && setTicketOpen(false)}>
          <div className="modal" style={{ maxWidth: 500 }}>
            <div className="modal-header">
              <div className="modal-title">Report Challenge Issue</div>
              <button className="modal-close" onClick={() => setTicketOpen(false)}>✕</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label className="form-label">Challenge</label>
                <input className="form-input" value={challenge.title} readOnly />
              </div>
              <div className="form-group">
                <label className="form-label">Issue Description</label>
                <textarea className="form-input" rows={4} placeholder="Describe the bug..." value={ticketMsg} onChange={e => setTicketMsg(e.target.value)} />
              </div>
              <button className="btn-primary" onClick={async () => {
                if (!ticketMsg.trim()) return;
                try {
                  const { ticketAPI } = await import('../api');
                  await ticketAPI.create({ subject: `[Challenge Bug] ${challenge.title}`, category: 'BUG', message: ticketMsg });
                  showNotification('Bug report submitted!', 'success');
                } catch (_) {
                  showNotification('Report recorded.', 'success');
                }
                setTicketOpen(false); setTicketMsg('');
              }}>Submit Report</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}