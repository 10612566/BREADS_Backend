-- ============================================================
--  BREADS MINDS Backend – Database Schema
--  Database : breads_minds
--  Engine   : MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS breads_minds
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE breads_minds;

-- ────────────────────────────────────────────────────────────
--  AREAS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS areas (
  id   BIGINT       NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_area_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  DISTRICTS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS districts (
  id      BIGINT       NOT NULL AUTO_INCREMENT,
  name    VARCHAR(100) NOT NULL,
  area_id BIGINT       NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_district_area FOREIGN KEY (area_id) REFERENCES areas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  SCHOOLS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS schools (
  id                              BIGINT         NOT NULL AUTO_INCREMENT,
  name                            VARCHAR(200)   NOT NULL,
  district_id                     BIGINT         NOT NULL,
  is_active                       TINYINT(1)     DEFAULT 1,
  taluka                          VARCHAR(100),
  gram_panchayat                  VARCHAR(100),
  village                         VARCHAR(100),
  distance_from_center            DOUBLE,
  school_category                 VARCHAR(50),
  school_status                   VARCHAR(50),
  complete_address                VARCHAR(500),
  teachers_male                   INT,
  teachers_female                 INT,
  teachers_total                  INT,
  class6_boys                     INT,
  class6_girls                    INT,
  class7_boys                     INT,
  class7_girls                    INT,
  class8_boys                     INT,
  class8_girls                    INT,
  class9_boys                     INT,
  class9_girls                    INT,
  grand_total_total               INT,
  groups_divided                  INT,
  strength_per_group              INT,
  total_enrollment                INT,
  marginalized_percentage         DOUBLE,
  sdq_percentage                  DOUBLE,
  academic_performance_percentage DOUBLE,
  dropout_rate_percentage         DOUBLE,
  students_requiring_support      INT,
  has_school_counselor            TINYINT(1),
  school_counselor_name           VARCHAR(150),
  has_basic_amenities             TINYINT(1),
  amenities_list                  VARCHAR(500),
  overall_suitability             VARCHAR(10),
  PRIMARY KEY (id),
  CONSTRAINT fk_school_district FOREIGN KEY (district_id) REFERENCES districts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  USERS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  username    VARCHAR(50)  NOT NULL,
  password    VARCHAR(255) NOT NULL,
  name        VARCHAR(150) NOT NULL,
  email       VARCHAR(150),
  mobile      VARCHAR(20),
  role        ENUM('DISTRICT_COORDINATOR','BREADS_COORDINATOR','SUPER_ADMIN') NOT NULL,
  status      ENUM('PENDING','ACTIVE','INACTIVE') NOT NULL DEFAULT 'PENDING',
  district_id BIGINT,
  photo_url   VARCHAR(500),
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_username (username),
  UNIQUE KEY uq_email    (email),
  CONSTRAINT fk_user_district FOREIGN KEY (district_id) REFERENCES districts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  HEALTH PROGRAMS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS health_programs (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  name          VARCHAR(200) NOT NULL,
  is_active     TINYINT(1)   DEFAULT 1,
  dedicated_for VARCHAR(100),
  objective     TEXT,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  NOTICES
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notices (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  title      VARCHAR(300) NOT NULL,
  message    TEXT         NOT NULL,
  priority   ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL,
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(150),
  is_active  TINYINT(1)   DEFAULT 1,
  start_date DATE,
  end_date   DATE,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- junction: notice ↔ target roles
CREATE TABLE IF NOT EXISTS notice_target_roles (
  notice_id BIGINT      NOT NULL,
  role      VARCHAR(30) NOT NULL,
  CONSTRAINT fk_ntr_notice FOREIGN KEY (notice_id) REFERENCES notices(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  SCHOOL PROPOSALS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS school_proposals (
  id                               BIGINT       NOT NULL AUTO_INCREMENT,
  district_id                      BIGINT       NOT NULL,
  proposed_by_user_id              BIGINT       NOT NULL,
  proposed_at                      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status                           ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  remarks                          TEXT,
  -- Location
  taluka                           VARCHAR(100),
  gram_panchayat                   VARCHAR(100),
  village_name                     VARCHAR(100),
  distance_from_center             DOUBLE,
  -- School basics
  school_name                      VARCHAR(200) NOT NULL,
  school_category                  VARCHAR(50),
  school_status                    VARCHAR(50),
  complete_address                 VARCHAR(500),
  justification                    TEXT,
  -- Teachers
  teachers_male                    INT,
  teachers_female                  INT,
  teachers_total                   INT,
  -- Class enrollment
  class5_boys  INT, class5_girls  INT,
  class6_boys  INT, class6_girls  INT,
  class7_boys  INT, class7_girls  INT,
  class8_boys  INT, class8_girls  INT,
  class9_boys  INT, class9_girls  INT,
  grand_total_boys                 INT,
  grand_total_girls                INT,
  grand_total_total                INT,
  groups_divided                   INT,
  strength_per_group               INT,
  -- Metrics
  total_enrollment                 INT,
  marginalized_percentage          DOUBLE,
  sdq_percentage                   DOUBLE,
  academic_performance_percentage  DOUBLE,
  dropout_rate_percentage          DOUBLE,
  students_requiring_support       INT,
  has_school_counselor             TINYINT(1),
  school_counselor_name            VARCHAR(150),
  has_support_staff_substitution   TINYINT(1),
  has_professional_partnership     TINYINT(1),
  teachers_willing_percentage      DOUBLE,
  proactive_administration         TINYINT(1),
  has_physical_space               TINYINT(1),
  has_basic_amenities              TINYINT(1),
  amenities_list                   VARCHAR(500),
  is_high_risk_region              TINYINT(1),
  has_active_sdmc                  TINYINT(1),
  has_staff_interest               TINYINT(1),
  overall_suitability              VARCHAR(10),
  selection_comments               TEXT,
  estimated_beneficiaries          INT,
  PRIMARY KEY (id),
  CONSTRAINT fk_sp_district  FOREIGN KEY (district_id)         REFERENCES districts(id),
  CONSTRAINT fk_sp_proposer  FOREIGN KEY (proposed_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  SERVICE REQUESTS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS service_requests (
  id                   BIGINT      NOT NULL AUTO_INCREMENT,
  request_number       VARCHAR(20) NOT NULL,
  district_id          BIGINT      NOT NULL,
  category             ENUM('SCHOOL_SELECTION','INFRASTRUCTURE_SUPPORT','PERSONNEL_REQUEST','BUDGET_APPROVAL') NOT NULL,
  requested_by_user_id BIGINT      NOT NULL,
  requested_at         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status               ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  remarks              TEXT,
  description          TEXT,
  school_proposal_id   BIGINT,
  reviewed_by_user_id  BIGINT,
  reviewed_at          DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uq_request_number (request_number),
  CONSTRAINT fk_sr_district    FOREIGN KEY (district_id)          REFERENCES districts(id),
  CONSTRAINT fk_sr_requester   FOREIGN KEY (requested_by_user_id) REFERENCES users(id),
  CONSTRAINT fk_sr_proposal    FOREIGN KEY (school_proposal_id)   REFERENCES school_proposals(id),
  CONSTRAINT fk_sr_reviewer    FOREIGN KEY (reviewed_by_user_id)  REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  BENEFICIARY REPORTS  (monthly, one per district per month)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS beneficiary_reports (
  id                    BIGINT      NOT NULL AUTO_INCREMENT,
  district_id           BIGINT      NOT NULL,
  month                 VARCHAR(7)  NOT NULL,   -- YYYY-MM
  year                  INT         NOT NULL,
  submitted_at          DATETIME    DEFAULT CURRENT_TIMESTAMP,
  submitted_by_user_id  BIGINT,
  -- Targets reached
  children_reached      INT         DEFAULT 0,
  parents_reached       INT         DEFAULT 0,
  professionals_reached INT         DEFAULT 0,
  teachers_reached      INT         DEFAULT 0,
  volunteers_reached    INT         DEFAULT 0,
  -- Activity counts (simple)
  modules               INT         DEFAULT 0,
  community_awareness   INT         DEFAULT 0,
  art_therapy           INT         DEFAULT 0,
  counselling           INT         DEFAULT 0,
  narrative_impact      TEXT,
  is_locked             TINYINT(1)  DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uq_report_district_month (district_id, month),
  CONSTRAINT fk_br_district  FOREIGN KEY (district_id)         REFERENCES districts(id),
  CONSTRAINT fk_br_submitter FOREIGN KEY (submitted_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  TRAINING SESSIONS  (detailed records per beneficiary report)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS training_sessions (
  id                  BIGINT      NOT NULL AUTO_INCREMENT,
  report_id           BIGINT      NOT NULL,
  training_type       VARCHAR(30) NOT NULL,   -- PARENT_TRAINING | TEACHER_TRAINING | VOLUNTEER_TRAINING | PRACTITIONER_TRAINING
  sl_no               INT,
  session_date        DATE,
  place               VARCHAR(200),
  school_name         VARCHAR(200),
  participants_male   INT         DEFAULT 0,
  participants_female INT         DEFAULT 0,
  participants_total  INT         DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT fk_ts_report FOREIGN KEY (report_id) REFERENCES beneficiary_reports(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  ADDITIONAL SESSIONS  (extra classes + additional counselling)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS additional_sessions (
  id                  BIGINT      NOT NULL AUTO_INCREMENT,
  report_id           BIGINT      NOT NULL,
  session_type        VARCHAR(30) NOT NULL,   -- EXTRA_CLASS | ADDITIONAL_COUNSELLING
  sl_no               INT,
  session_date        DATE,
  place               VARCHAR(200),
  school_name         VARCHAR(200),
  participants_male   INT         DEFAULT 0,
  participants_female INT         DEFAULT 0,
  participants_total  INT         DEFAULT 0,
  remarks             TEXT,
  PRIMARY KEY (id),
  CONSTRAINT fk_as_report FOREIGN KEY (report_id) REFERENCES beneficiary_reports(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  MINDS ACTIVITY RECORDS  (per-child intervention log)
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS minds_activity_records (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  district_id         BIGINT       NOT NULL,
  year                INT          NOT NULL,
  child_name          VARCHAR(150) NOT NULL,
  age                 INT          NOT NULL,
  class_name          VARCHAR(20),
  school_name         VARCHAR(200),
  intervention_type   VARCHAR(100),
  gender              VARCHAR(10),
  location            VARCHAR(200),
  topics_discussed    TEXT,
  session1_date       DATE,
  session2_date       DATE,
  session3_date       DATE,
  outcome             TEXT,
  follow_up           TEXT,
  remarks             TEXT,
  submitted_by_user_id BIGINT,
  submitted_at        DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_mar_district  FOREIGN KEY (district_id)          REFERENCES districts(id),
  CONSTRAINT fk_mar_submitter FOREIGN KEY (submitted_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  SCHOOL MONTHLY REPORTS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS school_monthly_reports (
  id                       BIGINT      NOT NULL AUTO_INCREMENT,
  district_id              BIGINT      NOT NULL,
  school_id                BIGINT      NOT NULL,
  visit_date               DATE        NOT NULL,
  type_of_visit            ENUM('ROUTINE','EVALUATION','CRISIS_SUPPORT','FOLLOW_UP'),
  minds_groups_established INT,
  children_in_minds_groups INT,
  module_number            INT,
  children_attended        INT,
  action_prompts           TEXT,
  referred_for_counselling INT,
  identified_with_issues   INT,
  challenges               TEXT,
  follow_up_plan           TEXT,
  follow_up_date           DATE,
  notes_for_next_module    TEXT,
  submitted_by_user_id     BIGINT,
  submitted_at             DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_smr_district  FOREIGN KEY (district_id)          REFERENCES districts(id),
  CONSTRAINT fk_smr_school    FOREIGN KEY (school_id)            REFERENCES schools(id),
  CONSTRAINT fk_smr_submitter FOREIGN KEY (submitted_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  MONTHLY PLAN ITEMS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS monthly_plan_items (
  id                   BIGINT      NOT NULL AUTO_INCREMENT,
  district_id          BIGINT      NOT NULL,
  month                VARCHAR(7)  NOT NULL,
  description          TEXT,
  date                 DATE        NOT NULL,
  responsible_persons  VARCHAR(500),
  status               ENUM('PENDING','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  submitted_by_user_id BIGINT,
  submitted_at         DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_mpi_district  FOREIGN KEY (district_id)          REFERENCES districts(id),
  CONSTRAINT fk_mpi_submitter FOREIGN KEY (submitted_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
--  SYSTEM LOGS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS system_logs (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  timestamp    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  performed_by VARCHAR(150),
  action       VARCHAR(100) NOT NULL,
  details      TEXT,
  PRIMARY KEY (id),
  INDEX idx_log_timestamp    (timestamp),
  INDEX idx_log_performed_by (performed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
