INSERT INTO promotion_tariff (id, price, name, currency, count_view, duration_days, coefficient_priority)
OVERRIDING SYSTEM VALUE
VALUES (101, 99.99, 'База', 'RUB', 1000, 7, 1);

INSERT INTO promotion_tariff (id, price, name, currency, count_view, duration_days, coefficient_priority)
OVERRIDING SYSTEM VALUE
VALUES (102, 499.99, 'Комфорт', 'RUB', 10000, 30, 5);

INSERT INTO promotion_tariff (id, price, name, currency, count_view, duration_days, coefficient_priority)
OVERRIDING SYSTEM VALUE
VALUES (103, 999.99, 'Легенда','RUB', 50000, 90, 10);

INSERT INTO promotion_tariff (id, price, name, currency, count_view, duration_days, coefficient_priority, deleted, deleted_at)
OVERRIDING SYSTEM VALUE
VALUES (104, 199.99, 'Устаревший', 'RUB', 5000, 14, 2, TRUE, CURRENT_TIMESTAMP);

INSERT INTO event (id, title, description, user_id, start_date, end_date, location, max_attendees, type, status, created_at)
OVERRIDING SYSTEM VALUE
VALUES (
    201,
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


INSERT INTO event (id, title, description, user_id, start_date, end_date, location, max_attendees, type, status, created_at, updated_at)
OVERRIDING SYSTEM VALUE
VALUES (
    202,
    'Планирование Q4',
    'Встреча команды для планирования задач на последний квартал года',
    2,
    '2025-10-05 14:00:00',
    '2025-10-05 16:30:00',
    'Офис, переговорная 3B',
    15,
    2,
    1,
    '2025-09-28 10:00:00',
    '2025-09-30 09:15:00'
);


INSERT INTO event (id, title, description, user_id, start_date, end_date, location, max_attendees, type, status, created_at, updated_at)
OVERRIDING SYSTEM VALUE
VALUES (
    203,
    'Розыгрыш курсов',
    'Ежемесячный розыгрыш бесплатных курсов среди подписчиков',
    3,
    '2025-12-01 00:00:00',
    '2025-12-31 23:59:59',
    'Онлайн',
    1000,
    3,
    0,
    '2025-11-15 12:00:00',
    '2025-11-20 17:30:00'
);

INSERT INTO promotion (id, user_id, type, tariff_id, end_date, count_view, status, created_at)
OVERRIDING SYSTEM VALUE
VALUES (
    301,
    9,
    'USER',
    101,
    '2025-12-31 23:59:59',
    1000,
    'ACTIVE',
    '2025-11-15 12:00:00'
);

INSERT INTO promotion (id, event_id, type, tariff_id, end_date, count_view, status, created_at)
OVERRIDING SYSTEM VALUE
VALUES (
    302,
    201,
    'EVENT',
    101,
    '2025-12-31 23:59:59',
    1000,
    'ACTIVE',
    '2025-11-15 12:00:00'
);

INSERT INTO promotion (id, event_id, type, tariff_id, end_date, count_view, status, created_at)
OVERRIDING SYSTEM VALUE
VALUES (
    303,
    202,
    'EVENT',
    101,
    '2025-12-31 23:59:59',
    1000,
    'FINISHED_TIME',
    '2025-11-15 12:00:00'
);

INSERT INTO promotion (id, user_id, type, tariff_id, end_date, count_view, status, created_at)
OVERRIDING SYSTEM VALUE
VALUES (
    304,
    2,
    'USER',
    101,
    '2025-12-31 23:59:59',
    3000,
    'FINISHED_VIEW',
    '2025-11-15 12:00:00'
);