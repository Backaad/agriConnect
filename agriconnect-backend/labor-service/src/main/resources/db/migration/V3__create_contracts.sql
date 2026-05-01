CREATE TABLE contracts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id          UUID NOT NULL REFERENCES job_offers(id),
    application_id  UUID NOT NULL REFERENCES applications(id),
    farmer_id       UUID NOT NULL,
    worker_id       UUID NOT NULL,
    amount_fcfa     BIGINT NOT NULL,
    duration_days   INTEGER NOT NULL,
    work_type       VARCHAR(50) NOT NULL,
    location_text   VARCHAR(255),
    start_date      DATE NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    farmer_signed_at TIMESTAMP,
    worker_signed_at TIMESTAMP,
    escrow_ref      VARCHAR(100),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_contracts_farmer_id ON contracts(farmer_id);
CREATE INDEX idx_contracts_worker_id ON contracts(worker_id);
CREATE INDEX idx_contracts_status    ON contracts(status);
