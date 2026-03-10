import { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { challengeAPI, scoreboardAPI, competitionAPI } from '../api';
import './Home.css';

const PHRASES = [
  './run_challenges.sh', 'cat flag.txt',
  'nc ctf.ensias.ma 1337', 'python3 exploit.py',
  'strings binary | grep ENSIAS',
];

export default function Home() {
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({ challenges: '–', players: '–' });
  const [timeLeft, setTimeLeft] = useState('--:--:--');
  const [typed, setTyped] = useState('');
  const endTimeRef = useRef(null);

  // If already logged in, redirect to challenges
  useEffect(() => {
    if (isLoggedIn) navigate('/challenges');
  }, [isLoggedIn, navigate]);

  // Fetch stats
  useEffect(() => {
    async function load() {
      const [challenges, scoreboard, competitions] = await Promise.all([
        challengeAPI.list().catch(() => []),
        scoreboardAPI.public().catch(() => []),
        competitionAPI.list().catch(() => []),
      ]);
      setStats({ challenges: challenges.length, players: scoreboard.length });
      if (competitions[0]?.endTime) endTimeRef.current = new Date(competitions[0].endTime);
    }
    load();
  }, []);

  // Countdown timer
  useEffect(() => {
    const id = setInterval(() => {
      if (!endTimeRef.current) { setTimeLeft('--:--:--'); return; }
      const diff = Math.max(0, Math.floor((endTimeRef.current - Date.now()) / 1000));
      const h = String(Math.floor(diff / 3600)).padStart(2, '0');
      const m = String(Math.floor((diff % 3600) / 60)).padStart(2, '0');
      const s = String(diff % 60).padStart(2, '0');
      setTimeLeft(`${h}:${m}:${s}`);
    }, 1000);
    return () => clearInterval(id);
  }, []);

  // Typewriter effect
  useEffect(() => {
    let pi = 0, ci = 0, deleting = false;
    const id = setInterval(() => {
      const phrase = PHRASES[pi];
      if (!deleting) {
        setTyped(phrase.substring(0, ci + 1));
        ci++;
        if (ci === phrase.length) deleting = true;
      } else {
        setTyped(phrase.substring(0, ci - 1));
        ci--;
        if (ci === 0) { deleting = false; pi = (pi + 1) % PHRASES.length; }
      }
    }, 90);
    return () => clearInterval(id);
  }, []);

  return (
    <section className="hero">
      <div className="hero-bg" />
      <div className="hero-content">
        <p className="hero-pretitle">// ECOLE NATIONALE SUPERIEURE D'INFORMATIQUE ET D'ANALYSE DES SYSTEMES</p>
        <h1 className="hero-title">
          ENSIAS<br />
          <span className="red">CTF</span>
        </h1>
        <p className="hero-desc">
          Test your skills in web exploitation, reverse engineering,<br />
          cryptography, forensics and more.<br />
          <span style={{ color: 'var(--red-bright)' }}>root@ensias:~$</span> {typed}
          <span className="cursor">_</span>
        </p>
        <div className="hero-cta">
          <button className="btn-primary" onClick={() => navigate('/register')}>Register Now</button>
          <button className="btn-outline" onClick={() => navigate('/login')}>Login</button>
        </div>
        <div className="hero-stats">
          <div className="stat-box">
            <span className="stat-number">{stats.challenges}</span>
            <span className="stat-label">Challenges</span>
          </div>
          <div className="stat-box">
            <span className="stat-number">{stats.players}</span>
            <span className="stat-label">Players</span>
          </div>
          <div className="stat-box">
            <span className="stat-number">{timeLeft}</span>
            <span className="stat-label">Remaining</span>
          </div>
        </div>
      </div>
    </section>
  );
}
