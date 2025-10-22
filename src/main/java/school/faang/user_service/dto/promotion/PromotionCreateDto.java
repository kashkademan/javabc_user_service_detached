package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.entity.promotion.Rate;

public record PromotionCreateDto(
        @NotNull(message = "User ID came empty")
        Long userId,
        @NotNull(message = "You have not selected a subscription type.")
        Rate rate,
        @NotNull
        PaymentRequest paymentRequest

) {

}
