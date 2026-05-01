CREATE TABLE missions (
    id                    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    contract_id           UUID NOT NULL REFERENCES contracts(id),
    farmer_id             UUID NOT NULL,
    worker_id             UUID NOT NULL,
    status                VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_date        DATE NOT NULL,
    started_at            TIMESTAMP,
    completed_at          TIMESTAMP,
    farmer_validated_at   TIMESTAMP,
    worker_validated_at   TIMESTAMP,
    dispute_reason        TEXT,
    farmer_rating         SMALLINT,
    worker_rating         SMALLINT,
    farmer_review         TEXT,
    worker_review         TEXT,
    created_at            TIMESTAMP NOT NULL DEFAULT now(),
    updated_at            TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_missions_contract_id ON missions(contract_id);
CREATE INDEX idx_missions_farmer_id   ON missions(farmer_id);
CREATE INDEX idx_missions_worker_id   ON missions(worker_id);
CREATE INDEX idx_missions_status      ON missions(status);
