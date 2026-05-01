CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE notifications (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL,
    title       VARCHAR(200) NOT NULL,
    body        TEXT NOT NULL,
    type        VARCHAR(50) NOT NULL,
    channel     VARCHAR(20) NOT NULL DEFAULT 'PUSH',
    is_read     BOOLEAN NOT NULL DEFAULT false,
    data        JSONB,
    image_url   TEXT,
    action_url  TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    read_at     TIMESTAMP
);

CREATE INDEX idx_notifications_user_id    ON notifications(user_id);
CREATE INDEX idx_notifications_type       ON notifications(type);
CREATE INDEX idx_notifications_is_read    ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
