-- =============================================================
--       CAREERMATCH PLATFORM - DATABASE RELATIONAL SCHEMA
-- =============================================================
-- Engine: SQLite 3 / JDBC
-- Constraints: Foreign Keys ON, WAL Mode, Unique constraints

PRAGMA foreign_keys = ON;

-- 1. USERS TABLE (Stores Job Seekers & Administrators)
CREATE TABLE IF NOT EXISTS Users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'USER',  -- 'USER' or 'ADMIN'
    education TEXT,
    interests TEXT
);

-- 2. SKILLS MASTER TABLE
CREATE TABLE IF NOT EXISTS Skills (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL
);

-- 3. USER SKILLS (Many-to-Many junction table with proficiency level)
CREATE TABLE IF NOT EXISTS UserSkills (
    user_id INTEGER,
    skill_id INTEGER,
    level TEXT,  -- 'Beginner', 'Intermediate', 'Advanced'
    PRIMARY KEY (user_id, skill_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES Skills(id) ON DELETE CASCADE
);

-- 4. COMPANIES TABLE
CREATE TABLE IF NOT EXISTS Companies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL,
    location TEXT
);

-- 5. JOB ROLES TABLE
CREATE TABLE IF NOT EXISTS JobRoles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id INTEGER,
    title TEXT NOT NULL,
    min_salary REAL,
    max_salary REAL,
    experience TEXT,
    FOREIGN KEY (company_id) REFERENCES Companies(id) ON DELETE CASCADE
);

-- 6. JOB SKILLS (Many-to-Many junction table for job requirements)
CREATE TABLE IF NOT EXISTS JobSkills (
    job_id INTEGER,
    skill_id INTEGER,
    PRIMARY KEY (job_id, skill_id),
    FOREIGN KEY (job_id) REFERENCES JobRoles(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES Skills(id) ON DELETE CASCADE
);

-- 7. LEARNING RESOURCES
CREATE TABLE IF NOT EXISTS LearningResources (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    skill_id INTEGER,
    title TEXT NOT NULL,
    url TEXT,
    FOREIGN KEY (skill_id) REFERENCES Skills(id) ON DELETE CASCADE
);

-- 8. LEARNING PROGRESS TRACKER
CREATE TABLE IF NOT EXISTS LearningProgress (
    user_id INTEGER,
    resource_id INTEGER,
    completed INTEGER DEFAULT 0,
    PRIMARY KEY (user_id, resource_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES LearningResources(id) ON DELETE CASCADE
);

-- 9. PLACEMENT READINESS EVALUATION SCORES
CREATE TABLE IF NOT EXISTS ReadinessScores (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    technical REAL,
    dsa REAL,
    projects REAL,
    interview REAL,
    total REAL,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- =============================================================
-- SAMPLE DEFAULT SEEDED ACCOUNTS
-- =============================================================
-- Administrator Account:
--   Email:    admin@careermatch.com
--   Password: password: admin123
--   Role:     ADMIN
--
-- Job Seeker Account:
--   Email:    ravi@gmail.com
--   Password: password123
--   Role:     USER
-- =============================================================