-- 1. Вставка тарифа
INSERT INTO promotion_tariff (price, name, currency, count_view, duration_days, coefficient_priority)
VALUES (99.99, 'База', 'RUB', 1000, 7, 1);

-- 2. Вставка события
INSERT INTO event (
    title, description, user_id, start_date, end_date,
    location, max_attendees, type, status, created_at
)
VALUES (
    'Введение в Spring Boot',
    'Онлайн-вебинар по основам Spring Boot для начинающих разработчиков',
    1,
    '2025-11-20 19:00:00',
    '2025-11-20 21:00:00',
    'Онлайн',
    500,
    0,
    0,
    CURRENT_TIMESTAMP
);

-- 3 Вставка промоушена
WITH latest_event AS (
    SELECT id FROM event ORDER BY id DESC LIMIT 1
),
latest_tariff AS (
    SELECT id FROM promotion_tariff ORDER BY id DESC LIMIT 1
)
INSERT INTO promotion (
    event_id,  type, tariff_id, end_date, count_view, status, created_at
)
SELECT
    latest_event.id,
    'EVENT',
    latest_tariff.id,
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    1000,
    'ACTIVE',
    CURRENT_TIMESTAMP
FROM latest_event, latest_tariff;

WITH latest_tariff AS (
    SELECT id FROM promotion_tariff ORDER BY id DESC LIMIT 1
)
INSERT INTO promotion (
    user_id, type, tariff_id, end_date, count_view, status, created_at
)
SELECT
    9,
    'USER',
    latest_tariff.id,
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    1000,
    'ACTIVE',
    CURRENT_TIMESTAMP
FROM latest_tariff;