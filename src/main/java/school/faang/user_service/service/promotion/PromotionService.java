package school.faang.user_service.service.promotion;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
import school.faang.user_service.service.promotion.validator.PromotionValidator;
import school.faang.user_service.service.redis.PromotionRedisService;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionService {

    private final UserContext userContext;
    private final PromotionRepository promotionRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionRedisService promotionRedisService;
    private final PromotionConfig promotionConfig;
    private final RedisTemplate<String, Object> redisTemplate;

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
        Integer numberOfImpressions = promotionConfig.getImpressionsForTarif(promotion.getTarif());
        promotion.setNumberOfImpressions(numberOfImpressions);
        promotion.setRemainingImpressions(numberOfImpressions);
        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("a new one was saved promotion {}", promotion.getId());

        promotionRedisService.savePromotion(savedPromotion);
        log.info("Promotion {} saved to Redis successfully", savedPromotion.getId());
        return promotion;
    }

    @PreDestroy
    public void destroyDataPromotionToRedis() {
        Set<String> keys = redisTemplate.keys("promotion:*");

        for (String key : keys) {
            Object value = redisTemplate.opsForValue().get(key);

            if (value instanceof Promotion) {
                Promotion promotion = (Promotion) value;
                promotionRepository.save(promotion);
            }
        }
    }
}
