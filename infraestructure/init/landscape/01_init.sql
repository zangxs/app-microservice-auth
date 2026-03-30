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


CREATE TABLE IF NOT EXISTS outbox (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id    UUID NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    payload         TEXT NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retries         INT NOT NULL DEFAULT 0,
    max_retries     INT NOT NULL DEFAULT 3,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    processed_at    TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox(status);