CREATE TABLE transactions (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    wallet_id        UUID NOT NULL REFERENCES wallets(id),
    reference        VARCHAR(100) NOT NULL UNIQUE,
    type             VARCHAR(40) NOT NULL,
    amount_fcfa      BIGINT NOT NULL CHECK (amount_fcfa > 0),
    fee_fcfa         BIGINT NOT NULL DEFAULT 0,
    status           VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    provider         VARCHAR(30),
    provider_ref     VARCHAR(200),
    provider_status  VARCHAR(100),
    description      TEXT,
    metadata         JSONB,
    idempotency_key  VARCHAR(200) UNIQUE,
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_transactions_wallet_id   ON transactions(wallet_id);
CREATE INDEX idx_transactions_reference   ON transactions(reference);
CREATE INDEX idx_transactions_status      ON transactions(status);
CREATE INDEX idx_transactions_created_at  ON transactions(created_at DESC);
CREATE INDEX idx_transactions_idempotency ON transactions(idempotency_key);
