package school.faang.user_service.service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.redis.RedisPromotionEntity;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.promoition.PromotionRepository;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class PromotionRedisService {
    private static final String PROMOTION_KEY_PREFIX = "promotion: ";

    private final RedisTemplate<String, RedisPromotionEntity> redisPromotionTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PromotionRepository promotionRepository;


    public PromotionRedisService(RedisTemplate<String, RedisPromotionEntity> redisPromotionTemplate,
                                 RedisTemplate<String, Object> redisTemplate,
                                 PromotionRepository promotionRepository) {
        this.redisPromotionTemplate = redisPromotionTemplate;
        this.promotionRepository = promotionRepository;
        this.redisTemplate = redisTemplate;
    }

    public void savePromotion(Promotion promotion) {

        RedisPromotionEntity redisPromotionEntity = mappingFromPromotionToRedisEntity(promotion);

        String key = getKeyForRedis(promotion.getId());

        redisPromotionTemplate.opsForValue().set(key, redisPromotionEntity);

        redisTemplate.opsForZSet().add("promotions:sorted", key, promotion.getNumberOfImpressions());
        log.debug("Promotion {} saved to Redis. key redis - {}", promotion.getId(), key);

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

    public List<Long> decrementRemainingImpressionsForPromotions(int countRow) {
        if (countRow <= 0) {
            throw new DataValidationException(String.format("You have entered a negative or zero value %d.",
                    countRow));
        }

        List<Object> promotionKeys = redisTemplate.opsForList()
                .range("promotions:queue", 0, countRow - 1);

        promotionKeys.stream()
                .forEach(keyObj -> {
                    String key = keyObj.toString();
                    HashOperations<String, String, Object> hashOps = redisPromotionTemplate.opsForHash();
                    Long newRemainingImpressions = hashOps.increment(key, "remainingImpressions", -1);
                    deleteFromRedis(key, newRemainingImpressions);
                });

        return null;
    }

    private void deleteFromRedis(String key, Long value) {
        RedisPromotionEntity promotionRedis = redisPromotionTemplate.opsForValue().get(key);
        Long promotionId = promotionRedis.getPromotionId();
        if (value <= 0) {
            redisPromotionTemplate.delete(key);
            promotionRepository.deleteById(promotionId);
            log.info("The promotion {} was removed", promotionId);
        } else {
            promotionRepository.decrementRemainingImpressions(promotionId);
        }
    }

    private String getKeyForRedis(Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(PROMOTION_KEY_PREFIX)
                .append(id);
        return sb.toString();
    }


    private RedisPromotionEntity mappingFromPromotionToRedisEntity(Promotion promotion) {
        return RedisPromotionEntity.builder()
                .promotionId(promotion.getId())
                .userId(promotion.getUserId())
                .tarif(promotion.getTarif())
                .remainingImpressions(promotion.getRemainingImpressions())
                .build();
    }

}
