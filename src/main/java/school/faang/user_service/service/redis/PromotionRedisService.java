package school.faang.user_service.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PROMOTION_KEY_PREFIX = "promotion:";
    private static final String PROMOTIONS_BY_USER_KEY = "promotions:user:";
    private static final Duration PROMOTION_TTL = Duration.ofDays(7);

    public void savePromotion(Promotion promotion) {
        try {
            String key = getPromotionKey(promotion.getId());
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

            valueOperations.set(key, promotion, PROMOTION_TTL);

            String userPromotionsKey = PROMOTIONS_BY_USER_KEY + promotion.getUserId();
            redisTemplate.opsForSet().add(userPromotionsKey, promotion.getId().toString());
            redisTemplate.expire(userPromotionsKey, PROMOTION_TTL);

            String sortedByImpressionsKey = getSortedByImpressionsKey();
            Double score = (double) promotion.getNumberOfImpressions();

            redisTemplate.opsForZSet().add(sortedByImpressionsKey, promotion.getId().toString(), score);
            redisTemplate.expire(sortedByImpressionsKey, PROMOTION_TTL);

            log.debug("Promotion {} saved to Redis", promotion.getId());
        } catch (Exception e) {
            log.error("Error saving promotion {} to Redis: {}", promotion.getId(), e.getMessage());
        }
    }

    public void saveAll(List<Promotion> promotions) {
        if (promotions.isEmpty()) {
            log.warn("DB promotion is empty");
            return;
        }
        List<Promotion> sortedPromotions = promotions.stream()
                .sorted(Comparator.comparing(Promotion::getNumberOfImpressions))
                .toList();

        for (Promotion promotion : sortedPromotions) {
            savePromotion(promotion);
        }
        log.info("redis init DB promotion");

    }

    private String getPromotionKey(Long promotionId) {
        return PROMOTION_KEY_PREFIX + promotionId;
    }

    private String getSortedByImpressionsKey() {
        return "promotions:sorted:by_impressions";
    }
}
