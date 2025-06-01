package school.faang.user_service.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.Currency;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentClientResponseDto {
    private PaymentStatus status;
    private int verificationCode;
    private long paymentNumber;
    private BigDecimal amount;
    private Currency currency;
    private String message;
}
