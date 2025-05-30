--liquibase formatted sql

--changeset trytofixme:create_user_score_table
CREATE TABLE IF NOT EXISTS user_score (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id BIGINT NOT NULL,
    score INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

--changeset trytofixme:create_index_user_score
CREATE INDEX IF NOT EXISTS idx_user_score_userid_score_desc ON user_score(user_id, score DESC);