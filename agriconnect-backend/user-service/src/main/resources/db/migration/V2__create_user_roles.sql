CREATE TABLE user_roles (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL,
    role        VARCHAR(30) NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (user_id, role)
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
