ALTER TABLE users
    ADD COLUMN IF NOT EXISTS telegram_user_name varchar(64),
    ADD COLUMN IF NOT EXISTS telegram_chat_id bigint;