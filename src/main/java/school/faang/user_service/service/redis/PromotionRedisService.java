package school.faang.user_service.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.Tarif;
import school.faang.user_service.entity.redis.RedisPromotionEntity;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.promoition.PromotionRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static school.faang.user_service.entity.promotion.PromotionStatus.ENDED;
import static school.faang.user_service.entity.promotion.Tarif.ADVANCED;
import static school.faang.user_service.entity.promotion.Tarif.BASIC;
import static school.faang.user_service.entity.promotion.Tarif.EXPERIENCE;
import static school.faang.user_service.entity.promotion.Tarif.LEGEND;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionRedisService {

    private static final Map<String, Function<UserDto, String>> FIELD_EXTRACTORS = Map.of(
            "id", userDto -> String.valueOf(userDto.id()),
            "username", UserDto::username,
            "email", UserDto::email,
            "phone", UserDto::phone,
            "aboutMe", UserDto::aboutMe
    );

    private static final Map<Tarif, Integer> SCOPE_FOR_USER_IN_REDIS = Map.of(
            LEGEND, 10,
            ADVANCED, 100,
            EXPERIENCE, 1000,
            BASIC, 10000
    );

    private static final String PROMOTION_SORTED_KEY_PREFIX = "promotions:sorted";

    @Value("${promotion-redis.time.initialDelay}")
    private Long initialDelay;
    @Value(" ${promotion-redis.time.fixedRate}")
    private Long fixedRate;


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PromotionRepository promotionRepository;
    private final ObjectMapper objectMapper;
    private final DistributedLockService lockService;


    public void savePromotionByUser(Promotion promotion, Long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        UserDto userDto = userMapper.toUserDto(user);
        RedisPromotionEntity redisPromotionEntity = new RedisPromotionEntity(userDto, promotion.getId());

        redisTemplate.opsForZSet().add(PROMOTION_SORTED_KEY_PREFIX, redisPromotionEntity,
                SCOPE_FOR_USER_IN_REDIS.get(promotion.getTarif()));
        log.debug("Promotion {} saved to Redis. member redis - {}", promotion.getId(), redisPromotionEntity);

    }

    public void saveAll(List<Promotion> promotions) {
        if (promotions.isEmpty()) {
            log.warn("DB promotion is empty");
            return;
        }

        for (Promotion promotion : promotions) {
            savePromotionByUser(promotion, promotion.getUserId());
        }
        log.info("redis init DB promotion");

    }

    @Scheduled(initialDelayString = "${promotion-redis.time.initialDelay}",
            fixedRateString = "${promotion-redis.time.fixedRate}")
    public void syncPromotionData() {
        if (lockService.tryLock("promotion:sync:lock", Duration.ofSeconds(30))) {
            try {
                redisTemplate.getConnectionFactory().getConnection().flushDb();
                List<Promotion> promotions = promotionRepository.findByPromotionStatusNot(ENDED);
                saveAll(promotions);
                log.info("Redis is updating its data");
            } finally {
                lockService.unlock("promotion:sync:lock");
            }
        } else {
            log.info("Sync skipped - another service is already syncing");
        }
    }

    @Transactional
    public List<UserDto> fetchPromotionsAndUpdateViews(int countRow) {
        if (countRow <= 0) {
            throw new DataValidationException(String.format("You have entered a negative or zero value %d.",
                    countRow));
        }
        if (lockService.tryLock("promotion:process:lock", Duration.ofSeconds(10))) {
            try {
                List<UserDto> resultPromotion = new ArrayList<>();
                Set<Object> promotionKeys = redisTemplate.opsForZSet()
                        .range(PROMOTION_SORTED_KEY_PREFIX, 0, countRow - 1);
                promotionKeys.stream()
                        .map(valueObj -> objectMapper.convertValue(valueObj, RedisPromotionEntity.class))
                        .peek(redisPromotionEntity
                                -> resultPromotion.add(redisPromotionEntity.getUserDto()))
                        .forEach(redisPromotionEntity
                                -> updatePromotionAfterView(redisPromotionEntity.getPromotionId()));
                return resultPromotion;
            } finally {
                lockService.unlock("promotion:process:lock");
            }
        } else {
            throw new ConcurrentModificationException("System is busy, please try again later");
        }
    }

    private void updatePromotionAfterView(Long promotionId) {

        int updated = promotionRepository.decrementRemainingDisplay(promotionId);

        if (updated == 0) {

            int deleted = promotionRepository.updateIfNoRemainingDisplay(promotionId);
            if (deleted > 0) {
                log.info("Promotion {} deleted after last impression", promotionId);
            }
        } else {
            log.debug("Promotion {} remaining impressions decremented", promotionId);
        }
    }


    public Comparator<UserDto> createComparatorFromSort(Sort sort) {
        return sort.stream()
                .map(this::createComparatorForOrder)
                .reduce(Comparator::thenComparing)
                .orElse(Comparator.comparing(UserDto::id));
    }

    private Comparator<UserDto> createComparatorForOrder(Sort.Order order) {
        Function<UserDto, String> fieldExtractor = FIELD_EXTRACTORS.get(order.getProperty());

        Comparator<UserDto> comparator;

        comparator = Comparator.comparing(fieldExtractor, nullsLastIgnoreCase());

        return order.isAscending() ? comparator : comparator.reversed();
    }


    private static Comparator<String> nullsLastIgnoreCase() {
        return Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
    }


}
