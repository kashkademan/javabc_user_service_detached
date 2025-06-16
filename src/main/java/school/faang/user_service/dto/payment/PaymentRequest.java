package school.faang.user_service.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.dto.payment.enums.Currency;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(
        @NotNull
        long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency fromCurrency,

        @NotNull
        Currency toCurrency
) {
}