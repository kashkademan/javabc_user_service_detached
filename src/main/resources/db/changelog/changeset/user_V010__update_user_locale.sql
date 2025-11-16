ALTER TABLE users
    ADD COLUMN if not exists locale varchar(16);

UPDATE users SET locale = 'en-US' WHERE username = 'JohnDoe';
UPDATE users SET locale = 'en-GB' WHERE username = 'JaneSmith';
UPDATE users SET locale = 'en-AU' WHERE username = 'MichaelJohnson';
UPDATE users SET locale = 'fr-FR' WHERE username = 'EmilyDavis';
UPDATE users SET locale = 'en-CA' WHERE username = 'WilliamTaylor';
UPDATE users SET locale = 'de-DE' WHERE username = 'OliviaAnderson';
UPDATE users SET locale = 'ja-JP' WHERE username = 'JamesWilson';
UPDATE users SET locale = 'it-IT' WHERE username = 'SophiaMartin';
UPDATE users SET locale = 'ru-RU' WHERE username = 'BenjaminThompson';
UPDATE users SET locale = 'es-ES' WHERE username = 'AvaHarris';