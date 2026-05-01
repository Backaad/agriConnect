CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE wallets (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID NOT NULL UNIQUE,
    balance_fcfa    BIGINT NOT NULL DEFAULT 0 CHECK (balance_fcfa >= 0),
    frozen_fcfa     BIGINT NOT NULL DEFAULT 0 CHECK (frozen_fcfa >= 0),
    currency        VARCHAR(3) NOT NULL DEFAULT 'XAF',
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_wallets_user_id ON wallets(user_id);
