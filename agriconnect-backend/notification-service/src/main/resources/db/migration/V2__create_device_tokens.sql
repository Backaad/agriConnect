CREATE TABLE device_tokens (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL,
    fcm_token   TEXT NOT NULL UNIQUE,
    platform    VARCHAR(20) NOT NULL DEFAULT 'ANDROID',
    device_name VARCHAR(100),
    active      BOOLEAN NOT NULL DEFAULT true,
    last_seen   TIMESTAMP NOT NULL DEFAULT now(),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_device_tokens_user_id ON device_tokens(user_id);
CREATE INDEX idx_device_tokens_active  ON device_tokens(user_id, active);
