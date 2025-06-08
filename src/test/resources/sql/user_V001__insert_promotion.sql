INSERT INTO promotion_tariff (price, name, currency, count_view, duration_days, coefficient_priority)
VALUES (99.99, 'База', 'RUB', 1000, 7, 1);

INSERT INTO promotion_tariff (price, name, currency, count_view, duration_days, coefficient_priority)
VALUES (499.99, 'Комфорт', 'RUB',10000, 30, 5);

INSERT INTO promotion_tariff (price, name, currency, count_view, duration_days, coefficient_priority)
VALUES (999.99, 'Легенда', 'RUB',50000, 90, 10);

INSERT INTO promotion_tariff (price, currency, count_view, duration_days, coefficient_priority, deleted, deleted_at)
VALUES (199.99, 'Устаревший', 'RUB',5000, 14, 2, TRUE, CURRENT_TIMESTAMP);