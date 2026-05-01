CREATE TABLE notification_logs (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id      UUID NOT NULL,
    channel      VARCHAR(20) NOT NULL,
    provider_ref VARCHAR(200),
    title        VARCHAR(200),
    status       VARCHAR(30) NOT NULL DEFAULT 'SENT',
    error_msg    TEXT,
    sent_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_notif_logs_user_id ON notification_logs(user_id);
CREATE INDEX idx_notif_logs_status  ON notification_logs(status);
