package school.faang.user_service.dto.premium;

import java.math.BigDecimal;

/**
 * PaymentResponse — DTO, представляющее ответ платежного сервиса после попытки проведения платежа.
 * <p>
 * Содержит информацию о статусе платежа, его уникальных идентификаторах, сумме и дополнительном сообщении.
 * </p>
 *
 * @param status статус платежа (например, SUCCESS или FAILURE)
 * @param verificationCode код подтверждения платежа, сгенерированный платежным сервисом
 * @param paymentNumber уникальный идентификатор платежа, совпадающий с запросом
 * @param amount сумма платежа в указанной валюте
 * @param currency валюта платежа (например, USD или EUR)
 * @param message дополнительное сообщение, информирующее о результате операции
 *
 * @author agent
 * @since 10.07.2025
 */
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {}