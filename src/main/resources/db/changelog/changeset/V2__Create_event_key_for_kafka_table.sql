CREATE TABLE IF NOT EXISTS event_key_for_kafka (
    id                    BIGSERIAL PRIMARY KEY,
    key_for_kafka         TEXT      UNIQUE
);
