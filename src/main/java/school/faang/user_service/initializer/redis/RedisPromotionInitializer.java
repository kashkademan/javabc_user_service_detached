package school.faang.user_service.initializer.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.promotion.PromotionRedisService;

import java.util.List;

import static school.faang.user_service.entity.promotion.PromotionStatus.ACTIVE;
import static school.faang.user_service.entity.promotion.PromotionType.EVENT;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPromotionInitializer {

    private final PromotionRepository promotionRepository;
    private final PromotionRedisService promotionRedisService;

    @PostConstruct
    public void init() {
        List<Promotion> promotions = promotionRepository.findAllByTypeAndStatus(EVENT, ACTIVE);
        promotions.stream()
                .filter(promo -> promo.getEvent() != null)
                .forEach(promo -> promotionRedisService.savePromotion(promo, promo.getEvent()));

        log.info("Redis initialized with {} promotions for events", promotions.size());
    }
}