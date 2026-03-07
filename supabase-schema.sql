-- ============================================================
-- ENSIAS CTF — PostgreSQL Schema for Supabase
-- Run this in: Supabase Dashboard > SQL Editor > New Query
-- ============================================================

-- Enable UUID extension (already enabled in Supabase by default)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- ENUMS
-- ============================================================

CREATE TYPE role_type AS ENUM ('USER', 'ADMIN');
CREATE TYPE difficulty_type AS ENUM ('EASY', 'MEDIUM', 'HARD');

-- ============================================================
-- TABLES
-- ============================================================

-- Users
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          role_type    NOT NULL DEFAULT 'USER',
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Competitions
CREATE TABLE competitions (
    id                    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name                  VARCHAR(255) NOT NULL,
    start_time            TIMESTAMP,
    end_time              TIMESTAMP,
    is_active             BOOLEAN NOT NULL DEFAULT TRUE,
    is_scoreboard_visible BOOLEAN NOT NULL DEFAULT TRUE
);

-- Teams
CREATE TABLE teams (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name           VARCHAR(100) NOT NULL UNIQUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    competition_id UUID REFERENCES competitions(id) ON DELETE SET NULL
);

-- Link users to teams (many users → one team)
ALTER TABLE users ADD COLUMN team_id UUID REFERENCES teams(id) ON DELETE SET NULL;

-- Challenges
CREATE TABLE challenges (
    id             UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    title          VARCHAR(255)  NOT NULL,
    description    TEXT,
    category       VARCHAR(100),
    difficulty     difficulty_type,
    points         INTEGER,
    initial_points INTEGER,
    minimum_points INTEGER,
    is_active      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    competition_id UUID          REFERENCES competitions(id) ON DELETE SET NULL
);

-- Flags (stored as SHA-256 hash — NEVER plaintext)
CREATE TABLE flags (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    hash         VARCHAR(64) NOT NULL,   -- SHA-256 hex = 64 chars
    challenge_id UUID NOT NULL REFERENCES challenges(id) ON DELETE CASCADE
);

-- Hints
CREATE TABLE hints (
    id           UUID    PRIMARY KEY DEFAULT uuid_generate_v4(),
    content      TEXT    NOT NULL,
    cost         INTEGER NOT NULL DEFAULT 0,
    challenge_id UUID    NOT NULL REFERENCES challenges(id) ON DELETE CASCADE
);

-- Dynamic Scoring config
CREATE TABLE dynamic_scoring (
    id         UUID   PRIMARY KEY DEFAULT uuid_generate_v4(),
    decay_rate DOUBLE PRECISION NOT NULL DEFAULT 0.08
);

-- Submissions (every flag attempt, correct or not)
CREATE TABLE submissions (
    id             UUID      PRIMARY KEY DEFAULT uuid_generate_v4(),
    submitted_flag VARCHAR(500) NOT NULL,
    is_correct     BOOLEAN   NOT NULL DEFAULT FALSE,
    submitted_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    user_id        UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id   UUID      NOT NULL REFERENCES challenges(id) ON DELETE CASCADE
);

-- Solves (only correct solves — one per user per challenge)
CREATE TABLE solves (
    id             UUID    PRIMARY KEY DEFAULT uuid_generate_v4(),
    solved_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    awarded_points INTEGER   NOT NULL,
    user_id        UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id   UUID      NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
    UNIQUE (user_id, challenge_id)   -- enforce one solve per user per challenge
);

-- ============================================================
-- INDEXES (for performance)
-- ============================================================

CREATE INDEX idx_submissions_user       ON submissions(user_id);
CREATE INDEX idx_submissions_challenge  ON submissions(challenge_id);
CREATE INDEX idx_solves_user            ON solves(user_id);
CREATE INDEX idx_solves_challenge       ON solves(challenge_id);
CREATE INDEX idx_challenges_competition ON challenges(competition_id);
CREATE INDEX idx_challenges_active      ON challenges(is_active);
CREATE INDEX idx_users_team             ON users(team_id);

-- ============================================================
-- VIEWS
-- ============================================================

-- Scoreboard view (aggregated, no raw data exposed)
CREATE VIEW scoreboard AS
SELECT
    u.id          AS user_id,
    u.username,
    t.name        AS team_name,
    COALESCE(SUM(s.awarded_points), 0) AS total_points,
    COUNT(s.id)                        AS solve_count,
    MAX(s.solved_at)                   AS last_solve_at
FROM users u
LEFT JOIN teams t  ON u.team_id = t.id
LEFT JOIN solves s ON u.id = s.user_id
GROUP BY u.id, u.username, t.name
ORDER BY total_points DESC, last_solve_at ASC;

-- ============================================================
-- DEFAULT DATA
-- ============================================================

-- Default admin user
-- Password: admin1234  (BCrypt hash — matches Spring Security BCryptPasswordEncoder)
INSERT INTO users (username, email, password_hash, role)
VALUES (
    'admin',
    'admin@ensias.ma',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    'ADMIN'
) ON CONFLICT (username) DO NOTHING;

-- Default competition
INSERT INTO competitions (name, start_time, end_time, is_active, is_scoreboard_visible)
VALUES (
    'ENSIAS CTF 2025',
    NOW(),
    NOW() + INTERVAL '48 hours',
    TRUE,
    TRUE
);

-- Default dynamic scoring config
INSERT INTO dynamic_scoring (decay_rate) VALUES (0.08);

-- ============================================================
-- ROW LEVEL SECURITY (Supabase RLS)
-- Enable so direct Supabase API calls are protected
-- The Spring Boot backend bypasses RLS via service_role key
-- ============================================================

ALTER TABLE users         ENABLE ROW LEVEL SECURITY;
ALTER TABLE challenges    ENABLE ROW LEVEL SECURITY;
ALTER TABLE flags         ENABLE ROW LEVEL SECURITY;
ALTER TABLE hints         ENABLE ROW LEVEL SECURITY;
ALTER TABLE submissions   ENABLE ROW LEVEL SECURITY;
ALTER TABLE solves        ENABLE ROW LEVEL SECURITY;
ALTER TABLE teams         ENABLE ROW LEVEL SECURITY;
ALTER TABLE competitions  ENABLE ROW LEVEL SECURITY;

-- Block all direct REST access (Spring Boot handles auth)
-- Only the service_role (backend) can read/write everything
CREATE POLICY "Backend only" ON users         FOR ALL USING (FALSE);
CREATE POLICY "Backend only" ON challenges    FOR ALL USING (FALSE);
CREATE POLICY "Backend only" ON flags         FOR ALL USING (FALSE);
CREATE POLICY "Backend only" ON hints         FOR ALL USING (FALSE);
CREATE POLICY "Backend only" ON submissions   FOR ALL USING (FALSE);
CREATE POLICY "Backend only" ON solves        FOR ALL USING (FALSE);
CREATE POLICY "Backend only" ON teams         FOR ALL USING (FALSE);
CREATE POLICY "Backend only" ON competitions  FOR ALL USING (FALSE);
