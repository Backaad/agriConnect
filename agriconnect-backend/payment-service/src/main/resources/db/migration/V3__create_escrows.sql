CREATE TABLE escrows (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reference_id     UUID NOT NULL,
    reference_type   VARCHAR(30) NOT NULL,
    payer_id         UUID NOT NULL,
    payee_id         UUID NOT NULL,
    amount_fcfa      BIGINT NOT NULL CHECK (amount_fcfa > 0),
    platform_fee     BIGINT NOT NULL DEFAULT 0,
    status           VARCHAR(30) NOT NULL DEFAULT 'LOCKED',
    locked_at        TIMESTAMP NOT NULL DEFAULT now(),
    released_at      TIMESTAMP,
    refunded_at      TIMESTAMP,
    expires_at       TIMESTAMP NOT NULL,
    release_reason   TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_escrows_reference_id   ON escrows(reference_id);
CREATE INDEX idx_escrows_payer_id       ON escrows(payer_id);
CREATE INDEX idx_escrows_payee_id       ON escrows(payee_id);
CREATE INDEX idx_escrows_status         ON escrows(status);
