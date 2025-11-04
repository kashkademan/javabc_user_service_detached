package school.faang.user_service.service.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
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
import school.faang.user_service.util.RedisComparingUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromotionRedisService {

    private static final String PROMOTION_SYNC_LOCK = "promotion:sync:lock";
    private static final String PROMOTION_SORTED_KEY_PREFIX = "promotions:sorted";
    private static final String PROMOTION_MAP_KEY_PREFIX = "promotion:map";

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
        RedisPromotionEntity redisPromotionEntity = new RedisPromotionEntity(userDto, promotion.getId(),
                promotion.getTarif());

        Map<Long, UserDto> map = new HashMap<>();

        map.put(redisPromotionEntity.getPromotionId(), redisPromotionEntity.getUserDto());

        redisTemplate.opsForSet().add(PROMOTION_MAP_KEY_PREFIX, map);
        redisTemplate.opsForZSet().add(PROMOTION_SORTED_KEY_PREFIX, redisPromotionEntity,
                promotion.getTarif().getScopeForTarif());
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

    @Transactional
    @Scheduled(initialDelayString = "${promotion-redis.time.initialDelay}",
            fixedRateString = "${promotion-redis.time.fixedRate}")
    public void syncPromotionData() {
        if (lockService.tryLock(PROMOTION_SYNC_LOCK, Duration.ofSeconds(30))) {
            try {
                List<Long> userIds = promotionRepository.findUserIdsWithUpdateForRedisTrue();
                List<User> users = userRepository.findAllById(userIds);
                List<UserDto> userDtos = users.stream()
                        .map(userMapper::toUserDto)
                        .toList();
                for (int i = 0; i < userIds.size(); i++) {
                    updateUserInRedis(userIds.get(i), userDtos.get(i));
                }
                promotionRepository.updateForRedisToFalse(userIds);

                log.info("Redis is updating its data");
            } finally {
                lockService.unlock(PROMOTION_SYNC_LOCK);
            }
        } else {
            log.info("Sync skipped - another service is already syncing");
        }
    }

    @Transactional
    public List<UserDto> fetchPromotionsAndUpdateViews(int countRow, Pageable pageable) {
        if (countRow <= 0) {
            throw new DataValidationException(String.format("You have entered a negative or zero value %d.",
                    countRow));
        }

        Map<Tarif, List<UserDto>> resultByTarif = new EnumMap<>(Tarif.class);
        Set<Object> promotionKeys = redisTemplate.opsForZSet()
                .range(PROMOTION_SORTED_KEY_PREFIX, 0, countRow - 1);

        promotionKeys.stream()
                .map(valueObj -> objectMapper.convertValue(valueObj, RedisPromotionEntity.class))
                .peek(redisPromotionEntity
                        -> {
                    Tarif tarif = redisPromotionEntity.getTarif();
                    resultByTarif.computeIfAbsent(tarif, k -> new ArrayList<>())
                            .add(redisPromotionEntity.getUserDto());
                })
                .forEach(redisPromotionEntity  -> {
                    updatePromotionAfterView(redisPromotionEntity.getPromotionId());
                });

        Comparator<UserDto> comparator = RedisComparingUtil.createComparatorFromSort(pageable.getSort());
        resultByTarif.values().forEach(list -> list.sort(comparator));
        List<UserDto> resultPromotion = resultByTarif.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Tarif::getScopeForTarif)))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
        return resultPromotion;
    }

    private void updatePromotionAfterView(Long promotionId) {
        int updated = promotionRepository.decrementRemainingDisplay(promotionId);

        if (updated == 0) {
            Set<Object> zsetMembers = redisTemplate.opsForZSet().range(PROMOTION_SORTED_KEY_PREFIX, 0, -1);
            deletedBySortedRedis(promotionId, zsetMembers);

            Set<Object> setMembers = redisTemplate.opsForSet().members(PROMOTION_MAP_KEY_PREFIX);
            deletedByPromotionMapToRedis(promotionId, setMembers);

            int deleted = promotionRepository.updateIfNoRemainingDisplay(promotionId);
            if (deleted > 0) {
                log.info("Promotion {} deleted from everywhere", promotionId);
            }

        }
    }

    private void deletedByPromotionMapToRedis(Long promotionId, Set<Object> setMembers) {
        for (Object member : setMembers) {
            Map<Long, UserDto> map = objectMapper.convertValue(member,
                    new TypeReference<>() {});

            if (map.containsKey(promotionId)) {
                redisTemplate.opsForSet().remove(PROMOTION_MAP_KEY_PREFIX, member);
                log.info("Removed map for promotion {} from Set", promotionId);
                break;
            }
        }
    }

    private void deletedBySortedRedis(Long promotionId, Set<Object> zsetMembers) {
        for (Object member : zsetMembers) {
            RedisPromotionEntity entity = objectMapper.convertValue(member, RedisPromotionEntity.class);
            if (entity.getPromotionId().equals(promotionId)) {
                redisTemplate.opsForZSet().remove(PROMOTION_SORTED_KEY_PREFIX, member);
                break;
            }
        }
    }

    private void updateUserInRedis(Long userId, UserDto newUserDto) {
        updateUserInMap(userId, newUserDto);

        updateUserInzSet(userId, newUserDto);

        log.info("User {} updated in Redis", userId);
    }

    private void updateUserInMap(Long userId, UserDto newUserDto) {
        Set<Object> setMembers = redisTemplate.opsForSet().members(PROMOTION_MAP_KEY_PREFIX);

        for (Object member : setMembers) {
            try {
                Map<Long, UserDto> map = objectMapper.convertValue(member,
                        new TypeReference<>() {});

                for (Map.Entry<Long, UserDto> entry : map.entrySet()) {
                    if (entry.getValue() != null && userId.equals(entry.getValue().id())) {
                        map.put(entry.getKey(), newUserDto);
                        redisTemplate.opsForSet().remove(PROMOTION_MAP_KEY_PREFIX, member);
                        redisTemplate.opsForSet().add(PROMOTION_MAP_KEY_PREFIX, map);
                        return;
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to process map member: {}", e.getMessage());
            }
        }
    }

    private void updateUserInzSet(Long userId, UserDto newUserDto) {
        Set<Object> zsetMembers = redisTemplate.opsForZSet().range(PROMOTION_SORTED_KEY_PREFIX, 0, -1);

        for (Object member : zsetMembers) {
            try {
                RedisPromotionEntity entity = objectMapper.convertValue(member, RedisPromotionEntity.class);

                if (entity.getUserDto() != null && userId.equals(entity.getUserDto().id())) {
                    Double score = redisTemplate.opsForZSet().score(PROMOTION_SORTED_KEY_PREFIX, member);
                    redisTemplate.opsForZSet().remove(PROMOTION_SORTED_KEY_PREFIX, member);

                    RedisPromotionEntity updatedEntity = RedisPromotionEntity.builder()
                            .userDto(newUserDto)
                            .promotionId(entity.getPromotionId())
                            .tarif(entity.getTarif())
                            .build();

                    redisTemplate.opsForZSet().add(PROMOTION_SORTED_KEY_PREFIX, updatedEntity, score);
                    return;
                }
            } catch (Exception e) {
                log.warn("Failed to process zset member: {}", e.getMessage());
            }
        }
    }
}
