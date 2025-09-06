CREATE TABLE default_score (
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    action_type  varchar(255) NOT NULL,
    base_points  int      NOT NULL,
    updated_at   timestamptz DEFAULT current_timestamp
    created_at   timestamptz DEFAULT current_timestamp
);

CREATE UNIQUE INDEX idx_default_score_action_type ON default_score (action_type);


CREATE TABLE user_action_log (
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id      bigint       NOT NULL,
    action_type  varchar(255) NOT NULL,
    points_earned int     NOT NULL,
    created_at   timestamptz DEFAULT current_timestamp
);

CONSTRAINT fk_user_actions_user_id FOREIGN KEY (user_id) REFERENCES users (id)

CREATE INDEX idx_user_actions_user_id ON user_actions (user_id);
CREATE INDEX idx_user_actions_created_at ON user_actions (created_at);