CREATE TABLE IF NOT EXISTS landscape (
                                         id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     BIGINT NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    latitude    DECIMAL(10, 8) NOT NULL,
    longitude   DECIMAL(11, 8) NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_landscape_user_id ON landscape(user_id);
CREATE INDEX IF NOT EXISTS idx_landscape_status ON landscape(status);