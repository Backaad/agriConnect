CREATE TABLE provider_callbacks (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    provider     VARCHAR(30) NOT NULL,
    reference    VARCHAR(200),
    raw_payload  TEXT NOT NULL,
    processed    BOOLEAN NOT NULL DEFAULT false,
    received_at  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_callbacks_reference ON provider_callbacks(reference);
