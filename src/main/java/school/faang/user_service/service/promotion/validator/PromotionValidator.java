package school.faang.user_service.service.promotion.validator;

import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;
import java.util.Objects;

import static school.faang.user_service.dto.payment.PaymentStatus.SUCCESS;
import static school.faang.user_service.entity.promotion.PromotionStatus.ACTIVE;

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

    public static void validateExistsByUserStatusPromotion(List<Promotion> promotionList, Long userId) {
        promotionList.stream()
                .filter(promotion -> Objects.equals(promotion.getPromotionStatus(), ACTIVE))
                .forEach(promotion -> {
                    throw new DataValidationException(String.format("User %d already has promotions ", userId));
                });
    }

}
