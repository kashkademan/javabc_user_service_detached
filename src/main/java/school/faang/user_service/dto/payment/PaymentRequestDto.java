package school.faang.user_service.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    @NotNull
    private Long paymentNumber;

    @Min(1)
    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;
}
