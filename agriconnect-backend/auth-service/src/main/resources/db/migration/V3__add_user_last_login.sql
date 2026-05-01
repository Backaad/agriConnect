-- V3: Add last_login_at to users
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP;
