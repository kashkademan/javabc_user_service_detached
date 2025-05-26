CREATE TABLE promotion_tariff (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    price NUMERIC(19,2) NOT NULL CHECK (price >= 0),
    count_view INTEGER NOT NULL,
    duration_days INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    deleted_at TIMESTAMP
);


CREATE TABLE promotion (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id BIGINT,
    event_id BIGINT,
    type VARCHAR(255) NOT NULL,
    tariff_id BIGINT NOT NULL,
    end_date TIMESTAMP NOT NULL,
    count_view INTEGER NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT fk_promotion_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT fk_promotion_event FOREIGN KEY (event_id) REFERENCES event(id),
    CONSTRAINT fk_promotion_tariff FOREIGN KEY (tariff_id) REFERENCES promotion_tariff(id),
    CONSTRAINT chk_user_or_event CHECK (
        (user_id IS NULL AND event_id IS NOT NULL) OR
        (user_id IS NOT NULL AND event_id IS NULL)
    )
);