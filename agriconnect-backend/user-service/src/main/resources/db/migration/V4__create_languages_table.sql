CREATE TABLE user_languages (
    user_id  UUID NOT NULL,
    language VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, language)
);

CREATE TABLE user_specialties (
    user_id   UUID NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, specialty)
);
