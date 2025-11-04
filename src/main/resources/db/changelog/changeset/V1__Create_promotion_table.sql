CREATE TABLE IF NOT EXISTS promotion (
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT    NOT NULL,
    tarif                 TEXT      NOT NULL,
    number_of_display     INTEGER   NOT NULL,
    remaining_display     INTEGER   NOT NULL,
    activation_time       TIMESTAMP,
    update_time           TIMESTAMP,
    promotion_status      TEXT,
    update_for_redis      BOOLEAN
);

CREATE INDEX IF NOT EXISTS idx_promotion_user_id ON promotion(user_id);