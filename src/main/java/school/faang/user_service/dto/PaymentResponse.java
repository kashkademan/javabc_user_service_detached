package school.faang.user_service.dto;

import lombok.Builder;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PaymentStatus;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}