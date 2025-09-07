CREATE TABLE default_score (
    id           BIGSERIAL PRIMARY KEY,
    action_type  varchar(255) NOT NULL UNIQUE,
    base_points  double PRECISION NOT NULL,
    is_active    boolean NOT NULL DEFAULT TRUE,
    updated_at   timestamptz DEFAULT current_timestamp,
    created_at   timestamptz DEFAULT current_timestamp
);


CREATE TABLE user_action_log (
    id           BIGSERIAL PRIMARY KEY,
    user_id      bigint       NOT NULL,
    action_type  varchar(255) NOT NULL,
    points_earned double PRECISION NOT NULL,
    created_at   timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_user_action_log_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE user_score (
    id BIGSERIAL PRIMARY KEY,
    user_id bigint NOT NULL UNIQUE,
    score double PRECISION NOT NULL,
    updated_at timestamptz DEFAULT current_timestamp
);