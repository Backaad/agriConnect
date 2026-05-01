CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE job_offers (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    farmer_id       UUID NOT NULL,
    work_type       VARCHAR(50) NOT NULL,
    description     TEXT NOT NULL,
    nb_workers      INTEGER NOT NULL DEFAULT 1,
    start_date      DATE NOT NULL,
    end_date        DATE,
    start_time      TIME,
    end_time        TIME,
    salary_fcfa     BIGINT NOT NULL,
    payment_method  VARCHAR(30) NOT NULL DEFAULT 'ANY',
    escrow_enabled  BOOLEAN NOT NULL DEFAULT false,
    location        GEOGRAPHY(Point, 4326),
    address_text    VARCHAR(255),
    radius_km       INTEGER NOT NULL DEFAULT 10,
    status          VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    expires_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50)
);

CREATE INDEX idx_job_offers_farmer_id ON job_offers(farmer_id);
CREATE INDEX idx_job_offers_status    ON job_offers(status);
CREATE INDEX idx_job_offers_start_date ON job_offers(start_date);
CREATE INDEX idx_job_offers_location  ON job_offers USING GIST(location);
