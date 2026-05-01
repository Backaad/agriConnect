CREATE TABLE withdrawal_requests (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL,
    wallet_id           UUID NOT NULL REFERENCES wallets(id),
    amount_fcfa         BIGINT NOT NULL CHECK (amount_fcfa > 0),
    fee_fcfa            BIGINT NOT NULL DEFAULT 0,
    net_amount_fcfa     BIGINT NOT NULL,
    provider            VARCHAR(30) NOT NULL,
    mobile_number       VARCHAR(20) NOT NULL,
    status              VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    provider_ref        VARCHAR(200),
    failure_reason      TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_withdrawals_user_id ON withdrawal_requests(user_id);
CREATE INDEX idx_withdrawals_status  ON withdrawal_requests(status);
