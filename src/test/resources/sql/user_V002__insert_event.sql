INSERT INTO event (title, description, user_id, start_date, end_date, location, max_attendees, type, status, created_at)
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


INSERT INTO event (title, description, user_id, start_date, end_date, location, max_attendees, type, status, created_at, updated_at)
VALUES (
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


INSERT INTO event (title, description, user_id, start_date, end_date, location, max_attendees, type, status, created_at, updated_at)
VALUES (
    'Розыгрыш курсов по программированию',
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