package school.faang.user_service.service.promotion;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionRedisMapper;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.utils.async.GracefullyShutdownThreadPool;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisService {
    private final PromotionRedisRepository promotionRedisRepository;
    private final PromotionRedisMapper promotionRedisMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    @Qualifier("decrementCountViewExecutorExecutor")
    private Executor executor;


    public void savePromotion(Promotion promotion) {
        PromotionRedisModel promotionRedisModel = promotionRedisMapper.toEventPromotionRedis(promotion);
        log.debug("Mapping Promotion entity to PromotionRedisModel. Entity content: {}. RedisModel content: {}.",
                promotion, promotionRedisModel);

        String promotionKey = RedisKeyUtil.getKeyById(promotion.getId(), RedisHashType.PROMOTION);
        promotionRedisModel.setKey(promotionKey);

        PromotionRedisModel savedPromotion = promotionRedisRepository.save(promotionRedisModel);
        log.info("Promotion {} has been saved in redis", savedPromotion);
    }

    public List<PromotionRedisModel> getAllPromotions() {
        Iterable<PromotionRedisModel> iterable = promotionRedisRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .toList();
    }

    public void deletePromotionByKey(String promotionKey) {
        promotionRedisRepository.deleteById(promotionKey);
        log.info("Promotion with key {} has been deleted", promotionKey);
    }

    @Async("decrementCountViewExecutorExecutor")
    public void decrementCountViewByEventIds(List<Long> eventIds) {
        eventIds.forEach(eventId ->
                promotionRedisRepository.findByEventId(eventId)
                        .ifPresent(promotion ->
                                executor.execute(() -> decrementCountView(promotion.getKey()))
                        )
        );
    }

    private void decrementCountView(String promotionKey) {
//        String lockKey = "lock:promotion:" + promotionKey;
//        String lockValue = UUID.randomUUID().toString();
//        long expireTimeMillis = 3000;
//
//        Boolean success = redisTemplate.opsForValue()
//                .setIfAbsent(lockKey, lockValue, Duration.ofMillis(expireTimeMillis));
//
//        if (Boolean.FALSE.equals(success)) {
//            log.warn("Couldn't get a lock for promotion with key {}", promotionKey);
//            decrementCountView(promotionKey);
//        }

        String lockKey = "lock:promotion:" + promotionKey;
        String lockValue = UUID.randomUUID().toString();
        long expireTimeMillis = 3000;
        int maxRetries = 10;
        int retryDelayMillis = 300;

        boolean lockAcquired = false;
        for (int i = 0; i < maxRetries; i++) {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, Duration.ofMillis(expireTimeMillis));

            if (Boolean.TRUE.equals(success)) {
                lockAcquired = true;
                break;
            }

            try {
                Thread.sleep(retryDelayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while waiting for lock on key {}", promotionKey);
            }
        }

        if (!lockAcquired) {
            log.warn("Failed to acquire lock for promotion key {} after {} retries", promotionKey, maxRetries);
            return;
        }

        try {
            promotionRedisRepository.findById(promotionKey).ifPresentOrElse(
                    freshPromotion -> {
                        int newCount = freshPromotion.getCountView() - 1;
                        freshPromotion.setCountView(newCount);
                        promotionRedisRepository.save(freshPromotion);
                        log.debug("Promotion with key {} has been updated count view to {}", freshPromotion.getKey(), newCount);
                    },
                    () -> log.warn("Promotion with key {} not found during decrement", promotionKey)
            );
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    private void releaseLock(String lockKey, String expectedValue) {
        try {
            String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
            if (expectedValue.equals(currentValue)) {
                redisTemplate.delete(lockKey);
            } else {
                log.debug("Lock for {} was already taken or expired by another process", lockKey);
            }
        } catch (Exception ex) {
            log.error("Error while releasing lock {}", lockKey, ex);
        }
    }

    //TODO: удалить
//    private void releaseLock(String lockKey, String expectedValue) {
//        Object currentValue = redisTemplate.opsForValue().get(lockKey);
//        if (expectedValue.equals(currentValue)) {
//            redisTemplate.delete(lockKey);
//        }
//    }

//    private void decrementCountView(PromotionRedisModel promotion) {
//        int newCount = promotion.getCountView() - 1;
//        promotion.setCountView(newCount);
//        promotionRedisRepository.save(promotion);
//        log.debug("Promotion with key {} has been updated count view, {}", promotion.getKey(), newCount);
//    }
}
