-- CampusCare Helpdesk database schema
CREATE DATABASE IF NOT EXISTS campuscare_helpdesk CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campuscare_helpdesk;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    CONSTRAINT chk_users_role CHECK (role IN ('STUDENT', 'ADMIN'))
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    category VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    student_name VARCHAR(100) NOT NULL,
    student_email VARCHAR(150) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    INDEX idx_tickets_status_created_at (status, created_at),
    INDEX idx_tickets_category_created_at (category, created_at),
    INDEX idx_tickets_student_email_created_at (student_email, created_at),
    CONSTRAINT chk_tickets_category CHECK (category IN ('IT_SUPPORT','FACILITIES','ACADEMICS','LIBRARY','TRANSPORT','OTHER')),
    CONSTRAINT chk_tickets_status CHECK (status IN ('OPEN','IN_PROGRESS','CLOSED'))
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
