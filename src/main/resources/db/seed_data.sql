-- ============================================================
--  BREADS MINDS – Seed / Initial Data
--  Run AFTER schema.sql
--  Passwords are BCrypt-encoded (cost=10)
-- ============================================================

USE breads_minds;

-- ────────────────────────────────────────────────────────────
--  AREAS
-- ────────────────────────────────────────────────────────────
INSERT IGNORE INTO areas (id, name) VALUES
  (1, 'South Karnataka'),
  (2, 'North Karnataka');

-- ────────────────────────────────────────────────────────────
--  DISTRICTS
-- ────────────────────────────────────────────────────────────
INSERT IGNORE INTO districts (id, name, area_id) VALUES
  (1, 'Bangalore',   1),
  (2, 'Chitradurga', 1),
  (3, 'Bidar',       2),
  (4, 'Yadgir',      2);

-- ────────────────────────────────────────────────────────────
--  SCHOOLS
-- ────────────────────────────────────────────────────────────
INSERT IGNORE INTO schools (id, name, district_id, is_active) VALUES
  (1, 'St. Joseph High School',                1, 1),
  (2, 'Government Primary School - Ulsoor',    1, 1),
  (3, 'Vidhya Vardhaka School',                3, 1),
  (4, 'Chitradurga Government High School',    2, 1),
  (5, 'Yadgir Model School',                   4, 1);

-- ────────────────────────────────────────────────────────────
--  USERS
--  NOTE: Do NOT insert users here.
--  BCrypt password hashing requires the Java application.
--  Users are automatically created with correct password hashes
--  by DataInitializer on first application startup.
--
--  Default credentials (seeded by the app):
--    superadmin   / breads_master  → SUPER_ADMIN
--    admin        / breads_hq      → BREADS_COORDINATOR
--    dc_bangalore / minds_blr      → DISTRICT_COORDINATOR (Bangalore)
--    dc_bidar     / minds_bdr      → DISTRICT_COORDINATOR (Bidar)
--    dc_chitradurga / minds_ctg    → DISTRICT_COORDINATOR (Chitradurga)
--    dc_yadgir    / minds_ydg      → DISTRICT_COORDINATOR (Yadgir)
-- ────────────────────────────────────────────────────────────

-- ────────────────────────────────────────────────────────────
--  HEALTH PROGRAMS
-- ────────────────────────────────────────────────────────────
INSERT IGNORE INTO health_programs (id, name, is_active, dedicated_for, objective) VALUES
  (1, 'Emotional Resilience Workshop',    1, 'Children',
   'Build coping mechanisms for academic stress.'),
  (2, 'Positive Parenting Seminar',        1, 'Parents',
   'Enhance family communication and child mental health support.'),
  (3, 'Teacher Well-Being Program',        1, 'Teachers',
   'Support teacher mental health for better classroom outcomes.'),
  (4, 'Community Mental Health Awareness', 1, 'Community Leaders/Volunteers',
   'Create community-level mental health champions.');

-- ────────────────────────────────────────────────────────────
--  WELCOME NOTICE
-- ────────────────────────────────────────────────────────────
INSERT IGNORE INTO notices (id, title, message, priority, created_by, is_active) VALUES
  (1,
   'Welcome to BREADS MINDS Portal',
   'The system has been set up successfully. Please review the reporting guidelines and submit monthly reports by the 5th of each month.',
   'HIGH', 'System', 1);

INSERT IGNORE INTO notice_target_roles (notice_id, role) VALUES
  (1, 'DISTRICT_COORDINATOR'),
  (1, 'BREADS_COORDINATOR'),
  (1, 'SUPER_ADMIN');
