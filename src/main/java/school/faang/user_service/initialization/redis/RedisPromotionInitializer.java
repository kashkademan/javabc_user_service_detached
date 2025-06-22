package school.faang.user_service.initialization.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.service.event.EventRedisService;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.promotion.PromotionService;
import school.faang.user_service.service.user.UserRedisService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPromotionInitializer {
    private final PromotionService promotionService;
    private final PromotionRedisService promotionRedisService;
    private final EventRedisService eventRedisService;
    private final UserRedisService userRedisService;

    //TODO: возможно можно написать лучше
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<Promotion> eventPromotions = promotionService.getAllActivePromotion(PromotionType.EVENT);

        eventPromotions.forEach(this::saveEventPromotionInRedis);

        log.info("Redis initialized with {} promotions for events", eventPromotions.size());

        List<Promotion> userPromotions = promotionService.getAllActivePromotion(PromotionType.USER);

        userPromotions.forEach(this::saveUserPromotionInRedis);

        log.info("Redis initialized with {} promotions for users", userPromotions.size());
    }

    private void saveEventPromotionInRedis(Promotion promotion) {
        promotionRedisService.savePromotion(promotion);
        eventRedisService.saveEvent(promotion.getEvent(), getTtlByPromotion(promotion));
    }

    private void saveUserPromotionInRedis(Promotion promotion) {
        promotionRedisService.savePromotion(promotion);
        userRedisService.saveUser(promotion.getUser(), getTtlByPromotion(promotion));
    }

    private long getTtlByPromotion(Promotion promotion) {
        long seconds = Duration.between(LocalDateTime.now(), promotion.getEndDate()).getSeconds();
        return seconds > 0 ? seconds : 0;
    }
}
