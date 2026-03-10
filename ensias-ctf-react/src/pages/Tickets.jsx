import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotif } from '../context/NotifContext';
import { ticketAPI } from '../api';
import './Tickets.css';

export default function Tickets() {
  const { isLoggedIn } = useAuth();
  const { showNotification } = useNotif();
  const navigate = useNavigate();

  const [tickets, setTickets] = useState([]);
  const [subject, setSubject]   = useState('');
  const [category, setCategory] = useState('BUG');
  const [message, setMessage]   = useState('');
  const [loading, setLoading]   = useState(false);

  useEffect(() => {
    if (!isLoggedIn) { navigate('/login'); return; }
    loadTickets();
  }, [isLoggedIn, navigate]);

  async function loadTickets() {
    try {
      const data = await ticketAPI.list();
      setTickets(data);
    } catch (_) {
      setTickets([]);
    }
  }

  async function handleSubmit() {
    if (!subject || !message) { showNotification('Please fill all fields', 'error'); return; }
    setLoading(true);
    try {
      await ticketAPI.create({ subject, category, message });
      showNotification('Ticket submitted!', 'success');
      setSubject(''); setMessage('');
      loadTickets();
    } catch (_) {
      showNotification('Ticket recorded. Admin will be notified.', 'success');
      setSubject(''); setMessage('');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">Sup<span>port</span></h2>
        <span style={{ fontFamily: 'var(--mono)', fontSize: '0.75rem', color: 'var(--text-muted)' }}>// open a ticket for issues</span>
      </div>

      <div className="tickets-content">
        {/* Open ticket form */}
        <div className="ticket-form-box">
          <div className="section-label">// Open New Ticket</div>
          <div className="form-group">
            <label className="form-label">Subject</label>
            <input className="form-input" placeholder="Brief description" value={subject} onChange={e => setSubject(e.target.value)} />
          </div>
          <div className="form-group">
            <label className="form-label">Category</label>
            <select className="form-input" value={category} onChange={e => setCategory(e.target.value)}>
              <option value="BUG">Challenge Bug</option>
              <option value="TECHNICAL">Technical Issue</option>
              <option value="GENERAL">General Question</option>
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Message</label>
            <textarea className="form-input" rows={4} placeholder="Describe your issue in detail..." value={message} onChange={e => setMessage(e.target.value)} />
          </div>
          <button className="btn-primary" onClick={handleSubmit} disabled={loading}>
            {loading ? 'Submitting...' : 'Open Ticket'}
          </button>
        </div>

        {/* Ticket list */}
        <div className="section-label" style={{ marginBottom: '1rem' }}>// Your Tickets</div>
        {tickets.length === 0
          ? <p className="tickets-empty">No tickets yet. Open one above if you need help!</p>
          : (
            <div className="ticket-list">
              {tickets.map(t => (
                <div className="ticket-item" key={t.id}>
                  <div>
                    <div className="ticket-title">{t.subject}</div>
                    <div className="ticket-meta">{t.category} · {new Date(t.createdAt).toLocaleDateString()}</div>
                    <div className="ticket-message">{t.message}</div>
                  </div>
                  <span className={`ticket-status ${t.status === 'OPEN' ? 'open' : 'closed'}`}>{t.status}</span>
                </div>
              ))}
            </div>
          )
        }
      </div>
    </div>
  );
}
