package school.faang.user_service.job.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.redis.InvalidRedisKeyException;
import school.faang.user_service.service.promotion.PromotionService;
import school.faang.user_service.storage.promotion.PromotionViewExpiredQueueStorage;
import school.faang.user_service.util.redis.RedisKeyUtil;

@Component
@Slf4j
@RequiredArgsConstructor
public class PromotionViewExpiredCleanJob {
    private final PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage;
    private final PromotionService promotionService;

    @Scheduled(fixedDelay = 60_000)
    public void cleanupDeletedPromotions() {
        log.info("Job promotion view clean started");
        while (promotionViewExpiredQueueStorage.hasDeletedPromotions()) {
            String key = promotionViewExpiredQueueStorage.pollDeletedPromotion();
            try {
                long promotionId = RedisKeyUtil.extractId(key);
                log.debug("Job promotion view clean ran finished promotion with id {}", promotionId);
                promotionService.finishedPromotionByView(promotionId);
            } catch (InvalidRedisKeyException ex) {
                log.warn("Invalid promotion id in queue: {}", key, ex);
            }
        }
        log.info("Job promotion view clean finished");
    }
}
