package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.PromotionConfig;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.repository.promoition.PromotionRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.promotion.validator.PromotionValidator;
import school.faang.user_service.service.redis.PromotionRedisService;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionService {

    private final UserContext userContext;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionRedisService promotionRedisService;
    private final PromotionConfig promotionConfig;


    public Promotion crearePromotion(Promotion promotion, PaymentRequest paymentRequest) {

        Long userId = userContext.getUserId();
        userRepository.getByIdOrThrow(userId);

        promotion.setUserId(userId);

        PromotionValidator.validateExistsByUserIdPromotion(() -> promotionRepository.existsByUserId(userId), userId);

        ResponseEntity<PaymentResponse> responseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse paymentResponse = responseEntity.getBody();

        PromotionValidator.validatePaymentResponse(paymentResponse);

        Integer numberOfDisplay = promotionConfig.getDisplayForTarif(promotion.getTarif());
        promotion.setNumberOfDisplay(numberOfDisplay);
        promotion.setRemainingDisplay(numberOfDisplay);

        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("a new one was saved promotion {}", promotion.getId());

        promotionRedisService.savePromotion(savedPromotion);
        log.info("Promotion {} saved to Redis successfully", savedPromotion.getId());
        return promotion;
    }
}
