CREATE TABLE applications (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id       UUID NOT NULL REFERENCES job_offers(id) ON DELETE CASCADE,
    worker_id    UUID NOT NULL,
    cover_note   TEXT,
    status       VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    applied_at   TIMESTAMP NOT NULL DEFAULT now(),
    reviewed_at  TIMESTAMP,
    UNIQUE (job_id, worker_id)
);

CREATE INDEX idx_applications_job_id    ON applications(job_id);
CREATE INDEX idx_applications_worker_id ON applications(worker_id);
CREATE INDEX idx_applications_status    ON applications(status);
