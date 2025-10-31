package school.faang.user_service.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.redis.RedisPromotionEntity;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.promoition.PromotionRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionRedisService {

    @Value("${promotion-redis.time.initialDelay}")
    private Long initialDelay;
    @Value(" ${promotion-redis.time.fixedRate}")
    private Long fixedRate;

    private static final String PROMOTION_SORTED_KEY_PREFIX = "promotions:sorted";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PromotionRepository promotionRepository;
    private final ObjectMapper objectMapper;


    public void savePromotion(Promotion promotion) {
        User user = userRepository.getByIdOrThrow(promotion.getUserId());
        UserDto userDto = userMapper.toUserDto(user);
        RedisPromotionEntity redisPromotionEntity = new RedisPromotionEntity(userDto, promotion.getId());

        redisTemplate.opsForZSet().add(PROMOTION_SORTED_KEY_PREFIX, redisPromotionEntity,
                promotion.getNumberOfDisplay() * (-1));
        log.debug("Promotion {} saved to Redis. member redis - {}", promotion.getId(), redisPromotionEntity);

    }

    public void saveAll(List<Promotion> promotions) {
        if (promotions.isEmpty()) {
            log.warn("DB promotion is empty");
            return;
        }

        for (Promotion promotion : promotions) {
            savePromotion(promotion);
        }
        log.info("redis init DB promotion");

    }

    @Scheduled(initialDelayString = "${promotion-redis.time.initialDelay}",
              fixedRateString = "${promotion-redis.time.fixedRate}")
    public void syncPromotionData() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        List<Promotion> promotions = promotionRepository.findAll();
        saveAll(promotions);
        log.info("Redis is updating its data");
    }

    @Transactional
    public List<UserDto> decrementRemainingImpressionsForPromotions(int countRow) {
        if (countRow <= 0) {
            throw new DataValidationException(String.format("You have entered a negative or zero value %d.",
                    countRow));
        }
        List<UserDto> resultPromotion = new ArrayList<>();
        Set<Object> promotionKeys = redisTemplate.opsForZSet()
                .range(PROMOTION_SORTED_KEY_PREFIX, 0, countRow - 1);
        promotionKeys.stream()
                .forEach(valueObj -> {
                    RedisPromotionEntity redisPromotionEntity = objectMapper
                            .convertValue(valueObj, RedisPromotionEntity.class);
                    resultPromotion.add(redisPromotionEntity.getUserDto());
                    updatePromotionAfterView(redisPromotionEntity.getPromotionId());
                });

        return resultPromotion;
    }

    private void updatePromotionAfterView(Long promotionId) {

        int updated = promotionRepository.decrementRemainingImpressions(promotionId);

        if (updated == 0) {
            int deleted = promotionRepository.deleteIfNoRemainingImpressions(promotionId);
            if (deleted > 0) {
                log.info("Promotion {} deleted after last impression", promotionId);
            }
        } else {
            log.debug("Promotion {} remaining impressions decremented", promotionId);
        }
    }
}
