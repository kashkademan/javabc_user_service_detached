CREATE TABLE role (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(64) UNIQUE NOT NULL
);

CREATE TABLE users_role (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,

    CONSTRAINT fk_users_role__user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_users_role__role_id FOREIGN KEY (role_id) REFERENCES role (id)
);

UPDATE users
SET password = CONCAT('{noop}', password)
WHERE password NOT LIKE '{noop}%';