package school.faang.user_service.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.Currency;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentClientRequestDto {
    private long paymentNumber;
    private BigDecimal amount;
    private Currency currency;
}
