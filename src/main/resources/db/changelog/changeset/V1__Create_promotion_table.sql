CREATE TABLE IF NOT EXISTS promotion (
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT    NOT NULL,
    tarif                  INTEGER   NOT NULL,
    number_of_impressions INTEGER   NOT NULL,
    remaining_impressions INTEGER   NOT NULL,
    activation_time       TIMESTAMP,
    update_time           TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_promotion_user_id ON promotion(user_id);