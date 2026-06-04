-- ============================================================
-- PhishGuard Pro — MySQL Database Schema
-- Run: mysql -u root -p < schema.sql
-- Or let Spring Boot auto-create with ddl-auto=update
-- ============================================================

CREATE DATABASE IF NOT EXISTS phishguard
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE phishguard;

-- Admin users (security admins who run campaigns)
CREATE TABLE IF NOT EXISTS admin_users (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name             VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL UNIQUE,
    password              VARCHAR(255) NOT NULL,
    organisation_name     VARCHAR(255) NOT NULL,
    role                  ENUM('ROLE_ADMIN','ROLE_USER') NOT NULL DEFAULT 'ROLE_ADMIN',
    plan                  ENUM('FREE','PERSONAL','STARTER','PROFESSIONAL','ENTERPRISE') NOT NULL DEFAULT 'FREE',
    mfa_enabled           BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret            VARCHAR(255),
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until          DATETIME,
    account_locked        BOOLEAN NOT NULL DEFAULT FALSE,
    stripe_customer_id    VARCHAR(255),
    plan_expires_at       DATETIME,
    plan_activated_at     DATETIME,
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Target users (employees being trained)
CREATE TABLE IF NOT EXISTS target_users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    department      VARCHAR(255),
    password        VARCHAR(255),
    risk_score      INT NOT NULL DEFAULT 0,
    total_points    INT NOT NULL DEFAULT 0,
    current_badge   VARCHAR(255),
    admin_id        BIGINT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admin_users(id) ON DELETE SET NULL
);

-- Email templates
CREATE TABLE IF NOT EXISTS email_templates (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    category         VARCHAR(100),
    difficulty       ENUM('EASY','MEDIUM','HARD'),
    sender_name      VARCHAR(255) NOT NULL,
    sender_email     VARCHAR(255) NOT NULL,
    subject          VARCHAR(500) NOT NULL,
    html_body        LONGTEXT NOT NULL,
    landing_page_type VARCHAR(100),
    is_default       BOOLEAN NOT NULL DEFAULT TRUE,
    admin_id         BIGINT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admin_users(id) ON DELETE SET NULL
);

-- Campaigns
CREATE TABLE IF NOT EXISTS campaigns (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL,
    description             TEXT,
    difficulty              ENUM('EASY','MEDIUM','HARD'),
    status                  ENUM('DRAFT','SCHEDULED','ACTIVE','PAUSED','COMPLETED') NOT NULL DEFAULT 'DRAFT',
    admin_id                BIGINT NOT NULL,
    template_id             BIGINT,
    scheduled_start_at      DATETIME,
    scheduled_end_at        DATETIME,
    send_window_hours       INT NOT NULL DEFAULT 48,
    auto_retest             BOOLEAN NOT NULL DEFAULT FALSE,
    retest_interval_days    INT NOT NULL DEFAULT 14,
    next_retest_at          DATETIME,
    total_targets           INT NOT NULL DEFAULT 0,
    emails_sent             INT NOT NULL DEFAULT 0,
    emails_opened           INT NOT NULL DEFAULT 0,
    links_clicked           INT NOT NULL DEFAULT 0,
    credentials_submitted   INT NOT NULL DEFAULT 0,
    trainings_completed     INT NOT NULL DEFAULT 0,
    launched_at             DATETIME,
    completed_at            DATETIME,
    created_at              DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id)   REFERENCES admin_users(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES email_templates(id) ON DELETE SET NULL
);

-- Campaign targets (junction: campaign ↔ target user)
CREATE TABLE IF NOT EXISTS campaign_targets (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id             BIGINT NOT NULL,
    target_user_id          BIGINT NOT NULL,
    tracking_token          VARCHAR(255) NOT NULL UNIQUE,
    email_sent              BOOLEAN NOT NULL DEFAULT FALSE,
    email_opened            BOOLEAN NOT NULL DEFAULT FALSE,
    link_clicked            BOOLEAN NOT NULL DEFAULT FALSE,
    credentials_submitted   BOOLEAN NOT NULL DEFAULT FALSE,
    training_completed      BOOLEAN NOT NULL DEFAULT FALSE,
    email_sent_at           DATETIME,
    clicked_at              DATETIME,
    submitted_at            DATETIME,
    scheduled_send_at       DATETIME,
    FOREIGN KEY (campaign_id)    REFERENCES campaigns(id) ON DELETE CASCADE,
    FOREIGN KEY (target_user_id) REFERENCES target_users(id) ON DELETE CASCADE
);

-- Simulation events (every click, submit, open)
CREATE TABLE IF NOT EXISTS simulation_events (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id              BIGINT,
    target_user_id           BIGINT,
    event_type               ENUM('EMAIL_SENT','EMAIL_OPENED','LINK_CLICKED',
                                  'CREDENTIALS_SUBMITTED','TRAINING_STARTED',
                                  'TRAINING_COMPLETED','QUIZ_PASSED','QUIZ_FAILED') NOT NULL,
    tracking_token           VARCHAR(255),
    ip_address               VARCHAR(100),
    user_agent               TEXT,
    captured_username        VARCHAR(255),
    captured_password_hash   VARCHAR(255),
    captured_password_masked VARCHAR(255),
    landing_page_type        VARCHAR(100),
    occurred_at              DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (campaign_id)    REFERENCES campaigns(id) ON DELETE SET NULL,
    FOREIGN KEY (target_user_id) REFERENCES target_users(id) ON DELETE SET NULL
);

-- Training progress per user per topic
CREATE TABLE IF NOT EXISTS training_progress (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_user_id      BIGINT NOT NULL,
    topic_id            VARCHAR(100) NOT NULL,
    topic_title         VARCHAR(255) NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    score               INT,
    attempts            INT NOT NULL DEFAULT 0,
    answers_json        TEXT,
    wrong_questions_json TEXT,
    started_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at        DATETIME,
    UNIQUE KEY uq_user_topic (target_user_id, topic_id),
    FOREIGN KEY (target_user_id) REFERENCES target_users(id) ON DELETE CASCADE
);

-- Notifications (3 categories: CAMPAIGN, LEARNING, BILLING)
CREATE TABLE IF NOT EXISTS notifications (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_id        BIGINT,
    target_user_id  BIGINT,
    category        ENUM('CAMPAIGN','LEARNING','BILLING') NOT NULL,
    title           VARCHAR(255) NOT NULL,
    message         TEXT NOT NULL,
    action_url      VARCHAR(500),
    `read`          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id)       REFERENCES admin_users(id) ON DELETE CASCADE,
    FOREIGN KEY (target_user_id) REFERENCES target_users(id) ON DELETE CASCADE
);

-- JWT refresh tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    token        VARCHAR(512) NOT NULL UNIQUE,
    admin_email  VARCHAR(255),
    user_email   VARCHAR(255),
    expires_at   DATETIME NOT NULL,
    revoked      BOOLEAN NOT NULL DEFAULT FALSE
);

-- Indexes for performance
DROP INDEX IF EXISTS idx_campaign_targets_token ON campaign_targets;
CREATE INDEX idx_campaign_targets_token ON campaign_targets(tracking_token);
DROP INDEX IF EXISTS idx_sim_events_campaign ON simulation_events;
CREATE INDEX idx_sim_events_campaign    ON simulation_events(campaign_id);
DROP INDEX IF EXISTS idx_sim_events_user ON simulation_events;
CREATE INDEX idx_sim_events_user        ON simulation_events(target_user_id);
DROP INDEX IF EXISTS idx_notifications_admin ON notifications;
CREATE INDEX idx_notifications_admin    ON notifications(admin_id, `read`);
DROP INDEX IF EXISTS idx_notifications_user ON notifications;
CREATE INDEX idx_notifications_user     ON notifications(target_user_id, `read`);
DROP INDEX IF EXISTS idx_training_user ON training_progress;
CREATE INDEX idx_training_user          ON training_progress(target_user_id);
