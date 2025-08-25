CREATE TABLE user_actions (
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id      bigint       NOT NULL,
    action_type  varchar(255) NOT NULL,
    points_earned int     NOT NULL,
    created_at   timestamptz DEFAULT current_timestamp
);

CONSTRAINT fk_user_actions_user_id FOREIGN KEY (user_id) REFERENCES users (id)

CREATE INDEX idx_user_actions_user_id ON user_actions (user_id);
CREATE INDEX idx_user_actions_created_at ON user_actions (created_at);