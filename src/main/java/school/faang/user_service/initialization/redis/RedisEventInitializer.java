package school.faang.user_service.initialization.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.service.event.EventRedisService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisEventInitializer {
    private final EventService eventService;
    private final PromotionService promotionService;
    private final EventRedisService eventRedisService;

    // TODO: ApplicationEvent
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<Event> promotions = eventService.getAllEvents();
        promotions.forEach(event -> {
            long ttl = promotionService.getActivePromotionByEventId(event.getId()).map(promotion -> promotion.getId()).orElse(10L);
            eventRedisService.saveEvent(event, 10L);
        });

        log.info("Redis initialized with {} promotions for events", promotions.size());
    }
}
