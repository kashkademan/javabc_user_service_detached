-- 0 = EMAIL, 1 = PHONE, 2 = TELEGRAM
INSERT INTO contact_preferences (user_id, preference)
VALUES
    ((SELECT id FROM users WHERE username = 'JohnDoe'),               0), -- EMAIL
    ((SELECT id FROM users WHERE username = 'JaneSmith'),             1), -- PHONE
    ((SELECT id FROM users WHERE username = 'MichaelJohnson'),        2), -- TELEGRAM
    ((SELECT id FROM users WHERE username = 'EmilyDavis'),            0), -- EMAIL
    ((SELECT id FROM users WHERE username = 'WilliamTaylor'),         1), -- PHONE
    ((SELECT id FROM users WHERE username = 'OliviaAnderson'),        2), -- TELEGRAM
    ((SELECT id FROM users WHERE username = 'JamesWilson'),           0), -- EMAIL
    ((SELECT id FROM users WHERE username = 'SophiaMartin'),          1), -- PHONE
    ((SELECT id FROM users WHERE username = 'BenjaminThompson'),      2), -- TELEGRAM
    ((SELECT id FROM users WHERE username = 'AvaHarris'),             0); -- EMAIL