package school.faang.user_service.service.promotion.validator;

import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.Objects;
import java.util.function.Supplier;

import static school.faang.user_service.dto.payment.PaymentStatus.SUCCESS;

public class PromotionValidator {

    public static void validatePaymentResponse(PaymentResponse paymentResponse) {
        if (paymentResponse != null) {
            PaymentStatus paymentStatus = paymentResponse.status();
            PromotionValidator.validatePaymentStatus(paymentStatus);
        } else {
            throw new ForbiddenException("Unable to determine payment status! We're working on it!!");
        }
    }


    public static void validatePaymentStatus(PaymentStatus status) {
        if (!Objects.equals(status, SUCCESS)) {
            throw new ForbiddenException("Payment failed");
        }
    }

    public static void validateExistsByUserIdPromotion(Supplier<Boolean> existsSupplier, Long userId) {
        Boolean exists = existsSupplier.get();
        if (exists) {
            throw new DataValidationException(String.format("User %d already has promotions", userId));
        }
    }
}
