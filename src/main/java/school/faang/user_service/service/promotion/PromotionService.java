package school.faang.user_service.service.promotion;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.PromotionConfig;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.promoition.PromotionRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.promotion.validator.PromotionValidator;
import school.faang.user_service.service.redis.PromotionRedisService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionService {

    private final UserContext userContext;
    private final PromotionRepository promotionRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionRedisService promotionRedisService;
    private final UserRepository userRepository;
    private final PromotionConfig promotionConfig;

    @PostConstruct
    public void initDataPromotionToRedis() {
        List<Promotion> promotions = promotionRepository.findAll();
        promotionRedisService.saveAll(promotions);
    }

    public Promotion crearePromotion(Promotion promotion, PaymentRequest paymentRequest) {
        Long userId = promotion.getUserId();
        PromotionValidator.validateUserOwnership(userContext.getUserId(), promotion.getUserId());

        PromotionValidator.validateExistsByUserIdPromotion(promotionRepository.existsByUserId(userId), userId);

        ResponseEntity<PaymentResponse> responseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse paymentResponse = responseEntity.getBody();

        if (paymentResponse.status() != null) {
            PaymentStatus paymentStatus = paymentResponse.status();
            PromotionValidator.validatePaymentStatus(paymentStatus);
        } else {
            throw new ForbiddenException("Unable to determine payment status! We're working on it!!");
        }
        Integer numberOfImpressions = promotionConfig.getImpressionsForRate(promotion.getRate());
        promotion.setNumberOfImpressions(numberOfImpressions);

        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("a new one was saved promotion {}", promotion.getId());

        promotionRedisService.savePromotion(savedPromotion);
        log.info("Promotion {} saved to Redis successfully", savedPromotion.getId());
        return promotion;
    }
}
