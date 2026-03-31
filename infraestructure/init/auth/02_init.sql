CREATE TABLE IF NOT EXISTS password_reset_token (
                                      id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      user_id     BIGINT NOT NULL,
                                      code        VARCHAR(6) NOT NULL,
                                      used        BOOLEAN NOT NULL DEFAULT FALSE,
                                      expires_at  TIMESTAMP NOT NULL,
                                      created_at  TIMESTAMP NOT NULL DEFAULT now()
);