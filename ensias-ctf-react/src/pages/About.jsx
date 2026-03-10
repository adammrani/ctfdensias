import './StaticPages.css';

const CATEGORIES = [
  { icon: '🌐', name: 'Web',      desc: 'SQL injection, XSS, SSRF, authentication bypasses' },
  { icon: '🔐', name: 'Crypto',   desc: 'Classical ciphers, RSA, elliptic curves, hashing' },
  { icon: '🔍', name: 'Forensics',desc: 'File analysis, memory dumps, network captures' },
  { icon: '⚙️', name: 'Reverse',  desc: 'Binary analysis, disassembly, deobfuscation' },
  { icon: '💥', name: 'Pwn',      desc: 'Buffer overflows, heap exploits, shellcoding' },
  { icon: '🎲', name: 'Misc',     desc: 'OSINT, steganography, programming challenges' },
];

export default function About() {
  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">About <span>ENSIAS</span></h2>
        <span className="page-subtitle">// who we are</span>
      </div>
      <div className="static-content">
        <div className="static-section">
          <h3>// The School</h3>
          <p>ENSIAS — École Nationale Supérieure d'Informatique et d'Analyse des Systèmes — is one of Morocco's premier engineering schools, specializing in computer science and information systems.</p>
          <p>Founded in 1992 and located in Rabat, ENSIAS has produced thousands of engineers who now lead technology innovation across Morocco and the world.</p>
        </div>

        <div className="static-section">
          <h3>// The CTF</h3>
          <p>ENSIAS CTF is organized by the cybersecurity club at ENSIAS. Our mission is to foster a culture of security awareness, critical thinking, and technical excellence among students.</p>
          <p>This platform features dynamic scoring, multiple challenge categories, and a live scoreboard.</p>
        </div>

        <div className="static-section">
          <h3>// Challenge Categories</h3>
          <div className="about-grid">
            {CATEGORIES.map(cat => (
              <div className="about-card" key={cat.name}>
                <div className="about-icon">{cat.icon}</div>
                <h4>{cat.name}</h4>
                <p>{cat.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
