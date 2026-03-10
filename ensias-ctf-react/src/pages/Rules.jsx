import './StaticPages.css';

export default function Rules() {
  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">Ru<span>les</span></h2>
        <span className="page-subtitle">// read before competing</span>
      </div>
      <div className="static-content">
        <Section title="// General Rules">
          <ul>
            <li>This is a <strong>team-based</strong> competition. Each player must belong to a team.</li>
            <li>Do not attack the CTF infrastructure itself.</li>
            <li>Do not brute-force flags — each flag follows the format <code>ENSIAS{`{...}`}</code>.</li>
            <li>Do not share flags or solutions during the competition.</li>
            <li>Do not use automated scanners against other participants' services.</li>
            <li>Be respectful. Any harassment will result in disqualification.</li>
            <li>The organizers' decisions are final.</li>
          </ul>
        </Section>

        <Section title="// Scoring System">
          <p>ENSIAS CTF uses <strong>dynamic scoring</strong> — challenge point values decrease as more teams solve them.</p>
          <div className="scoring-formula">
            points = max(minimumPoints, initialPoints × e<sup>(-decayRate × solveCount)</sup>
            <br/><br/>
            Where:<br/>
            &nbsp;&nbsp;initialPoints&nbsp; = starting value (e.g. 500)<br/>
            &nbsp;&nbsp;minimumPoints  = floor value (e.g. 50)<br/>
            &nbsp;&nbsp;decayRate&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; = how fast points drop (default: 0.05)<br/>
            &nbsp;&nbsp;solveCount&nbsp;&nbsp;&nbsp;&nbsp; = number of teams that solved it
          </div>
          <p>Example: A 500-point challenge with 10 solvers → ~303 pts. With 30 solvers → ~112 pts.</p>
        </Section>

        <Section title="// Hints">
          <ul>
            <li>Each challenge may have one or more hints available.</li>
            <li>Hints are free to reveal (cost = 0 pts) unless specified otherwise.</li>
            <li>Once revealed, a hint cannot be un-revealed.</li>
            <li>Hints will not give away the full solution — only nudges.</li>
          </ul>
        </Section>

        <Section title="// Flag Format">
          <ul>
            <li>All flags follow the format: <code>ENSIAS{`{...}`}</code></li>
            <li>Flags are case-sensitive unless stated otherwise.</li>
            <li>Submit the full flag including the wrapper.</li>
          </ul>
        </Section>

        <Section title="// Support">
          <p>If you encounter technical issues, open a support ticket from the <strong>Tickets</strong> page (requires login).</p>
        </Section>
      </div>
    </div>
  );
}

function Section({ title, children }) {
  return (
    <div className="static-section">
      <h3>{title}</h3>
      {children}
    </div>
  );
}
