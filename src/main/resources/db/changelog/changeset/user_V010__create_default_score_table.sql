CREATE TABLE default_score (
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    action_type  varchar(255) NOT NULL,
    base_points  int      NOT NULL,
    updated_at   timestamptz DEFAULT current_timestamp
    created_at   timestamptz DEFAULT current_timestamp
);

CREATE UNIQUE INDEX idx_default_score_action_type ON default_score (action_type);