package school.faang.user_service.service.promotion.validator;

import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.Objects;

import static school.faang.user_service.dto.payment.PaymentStatus.SUCCESS;

public class PromotionValidator {

    public static void validateUserOwnership(Long userIdContext, Long userId) {
        if (!Objects.equals(userIdContext, userId)) {
            throw new ForbiddenException("The user is trying to purchase a subscription for someone else.");
        }
    }

    public static void validatePaymentStatus(PaymentStatus status) {
        if (!Objects.equals(status, SUCCESS)) {
            throw new ForbiddenException("Payment failed");
        }
    }

    public static void validateExistsByUserIdPromotion(Boolean isValidate, Long userId) {
        if (isValidate) {
            throw new DataValidationException(String.format("User %d already has promotions", userId));
        }
    }

}
