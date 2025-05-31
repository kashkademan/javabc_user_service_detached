package school.faang.user_service.job.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.service.promotion.PromotionService;
import school.faang.user_service.storage.promotion.PromotionViewExpiredQueueStorage;

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
                // TODO: короткий ключ
                long promotionId = Long.parseLong(key);
                log.debug("Job promotion view clean ran finished promotion with id {}", promotionId);
                promotionService.finishedPromotionByView(promotionId);
            } catch (NumberFormatException ex) {
                log.warn();
            }
        }
        log.info("Job promotion view clean finished");
    }
}
