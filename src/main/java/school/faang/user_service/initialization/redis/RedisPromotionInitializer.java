package school.faang.user_service.initialization.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPromotionInitializer {

    private final PromotionService promotionService;
    private final PromotionRedisService promotionRedisService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<Promotion> promotions = promotionService.getAllActiveEventPromotion();
        promotions.forEach(promotionRedisService::savePromotion);

        log.info("Redis initialized with {} promotions for events", promotions.size());
    }
}
