CREATE TABLE job_offer_tools (
    job_id UUID NOT NULL REFERENCES job_offers(id) ON DELETE CASCADE,
    tool   VARCHAR(100) NOT NULL,
    PRIMARY KEY (job_id, tool)
);
