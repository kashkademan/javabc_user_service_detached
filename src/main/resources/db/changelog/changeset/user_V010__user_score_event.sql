CREATE TABLE user_score_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id bigint NOT NULL,
    username varchar(64) NOT NULL,
    action_type VARCHAR(64) NOT NULL,
    points INT NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    CONSTRAINT fk_user_score_event_user FOREIGN KEY (user_id) REFERENCES users (id)
);