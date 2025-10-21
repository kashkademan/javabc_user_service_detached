package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.repository.promoition.PromotionRepository;
import school.faang.user_service.service.promotion.validator.PromotionValidator;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionService {

    private final UserContext userContext;
    private final PromotionRepository promotionRepository;
    private final PaymentServiceClient paymentServiceClient;

    private final ProjectSubscriptionRepository projectSubscriptionRepository;

    public Promotion crearePromotion(Promotion promotion, PaymentRequest paymentRequest) {

        PromotionValidator.validateUserOwnership(userContext.getUserId(), promotion.getUserId());

        ResponseEntity<PaymentResponse> responseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse paymentResponse = responseEntity.getBody();

        if (paymentResponse.status() != null) {
            PaymentStatus paymentStatus = paymentResponse.status();
            PromotionValidator.validatePaymentStatus(paymentStatus);
        } else {
            throw new ForbiddenException("Unable to determine payment status! We're working on it!!");
        }

        promotionRepository.save(promotion);
        log.info("a new one was saved promotion {}", promotion.getId());
        return promotion;
    }
}
