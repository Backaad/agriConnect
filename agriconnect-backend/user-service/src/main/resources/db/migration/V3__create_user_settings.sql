CREATE TABLE user_settings (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id          UUID NOT NULL UNIQUE,
    language         VARCHAR(10) NOT NULL DEFAULT 'fr',
    notif_push       BOOLEAN NOT NULL DEFAULT true,
    notif_sms        BOOLEAN NOT NULL DEFAULT true,
    notif_email      BOOLEAN NOT NULL DEFAULT false,
    dark_mode        BOOLEAN NOT NULL DEFAULT false,
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP NOT NULL DEFAULT now()
);
