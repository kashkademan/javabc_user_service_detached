package school.faang.user_service.initialization.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisTtlProperties;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.service.event.EventRedisService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.promotion.PromotionService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisEventInitializer {
    private final EventService eventService;
    private final PromotionService promotionService;
    private final EventRedisService eventRedisService;
    private final RedisTtlProperties redisTtlProperties;

    // TODO: ApplicationEvent
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<Event> promotions = eventService.getAllEvents();
        promotions.forEach(event -> {
            long ttl = promotionService.getActivePromotionByEventId(event.getId())
                    .map(promotion -> getTtlByPromotion(promotion) + redisTtlProperties.getEvent())
                    .orElse(redisTtlProperties.getEvent());
            eventRedisService.saveEvent(event, ttl);
            log.debug("Event {} saved to Redis with TTL {} seconds", event.getId(), ttl);
        });

        log.info("Redis initialized with {} promotions for events", promotions.size());
    }

    private long getTtlByPromotion(Promotion promotion) {
        long seconds = Duration.between(LocalDateTime.now(), promotion.getEndDate()).getSeconds();
        return seconds > 0 ? seconds : 0;
    }
}
