CREATE TABLE IF NOT EXISTS user_resource
(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name       VARCHAR(255) NOT NULL,
    key        VARCHAR(255),
    type       VARCHAR(255),
    status     VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    size BIGINT,

    CONSTRAINT fk_resource_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_resource_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);