package school.faang.user_service.initialization.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.service.event.EventRedisService;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.promotion.PromotionService;

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

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<Promotion> promotions = promotionService.getAllActiveEventPromotion();

        promotions.forEach(this::saveInRedis);

        log.info("Redis initialized with {} promotions for events", promotions.size());
    }

    private void saveInRedis(Promotion promotion) {
        promotionRedisService.savePromotion(promotion);
        eventRedisService.saveEvent(promotion.getEvent(), promotion.getId(), getTtlByPromotion(promotion));
    }

    private long getTtlByPromotion(Promotion promotion) {
        long seconds = Duration.between(LocalDateTime.now(), promotion.getEndDate()).getSeconds();
        return seconds > 0 ? seconds : 0;
    }
}
