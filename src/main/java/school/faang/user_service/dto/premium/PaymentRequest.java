package school.faang.user_service.dto.premium;

import java.math.BigDecimal;

/**
 * PaymentRequest — DTO, представляющее запрос на оплату премиум подписки.
 * <p>
 * Содержит данные, необходимые для проведения платежа в платежном сервисе.
 * </p>
 *
 * @param paymentNumber уникальный идентификатор платежа
 * @param amount сумма платежа в выбранной валюте
 * @param currency валюта, в которой производится платеж (например, USD или EUR)
 *
 * @author agent
 * @since 10.07.2025
 */
public record PaymentRequest(
        long paymentNumber,
        BigDecimal amount,
        Currency currency
) {}