package school.faang.user_service.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.repository.promoition.PromotionRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class PromotionRedisService {

    private final ValueOperations<String, Object> valueOperations;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PromotionRepository promotionRepository;
    private static final String PROMOTION_KEY_PREFIX = "promotion:";
    private static final String PROMOTIONS_BY_USER_KEY = "promotions:user:";
    private static final Duration PROMOTION_TTL = Duration.ofDays(7);

    public PromotionRedisService(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 PromotionRepository promotionRepository) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.objectMapper = objectMapper;
        this.promotionRepository = promotionRepository;
    }

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
                .sorted(Comparator.comparing(Promotion::getNumberOfImpressions).reversed())
                .toList();

        for (Promotion promotion : sortedPromotions) {
            savePromotion(promotion);
        }
        log.info("redis init DB promotion");

    }

    public List<Long> decrementFirstPromotions(int n) {
        Set<String> keys = redisTemplate.keys("promotion:*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> processedUserIds = new ArrayList<>();
        int count = 0;

        for (String key : keys) {
            if (count >= n) {
                break;
            }

            Promotion promotion = getPromotionFromRedis(key);
            if (promotion == null || promotion.getRemainingImpressions() <= 0) {
                continue;
            }

            promotion.setRemainingImpressions(promotion.getRemainingImpressions() - 1);
            valueOperations.set(key, promotion);

            processedUserIds.add(promotion.getUserId());
            count++;

            if (promotion.getRemainingImpressions() <= 0) {
                promotionRepository.deleteById(promotion.getId());
                redisTemplate.delete(key);
                log.info("was deleted from the database PromotionRepository with the promotionId ID",
                        promotion.getId());
            }
        }

        return processedUserIds;
    }

    private Promotion getPromotionFromRedis(String key) {
        try {
            Object value = valueOperations.get(key);
            if (value instanceof Promotion) {
                return (Promotion) value;
            } else if (value instanceof Map) {
                return objectMapper.convertValue(value, Promotion.class);
            }
            return null;
        } catch (Exception e) {
            log.error("Error converting Redis value to Promotion for key: {}", key);
            return null;
        }
    }


    private String getPromotionKey(Long promotionId) {
        return PROMOTION_KEY_PREFIX + promotionId.toString();
    }

    private String getSortedByImpressionsKey() {
        return "promotions:sorted:by_impressions";
    }
}
