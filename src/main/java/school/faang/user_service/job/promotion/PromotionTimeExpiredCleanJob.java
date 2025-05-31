package school.faang.user_service.job.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.promotion.PromotionService;
import school.faang.user_service.storage.promotion.PromotionTimeExpiredQueueStorage;

@Component
@Slf4j
@RequiredArgsConstructor
public class PromotionTimeExpiredCleanJob {
    private final PromotionTimeExpiredQueueStorage promotionTimeExpiredQueueStorage;
    private final PromotionService promotionService;

    @Scheduled(fixedDelay = 3_600_000)
    public void cleanupDeletedPromotions() {
        log.info("Job promotion time clean started");
        while (promotionTimeExpiredQueueStorage.hasDeletedPromotions()) {
            String id = promotionTimeExpiredQueueStorage.pollDeletedPromotion();
            try {
                // TODO: короткий ключ
                long promotionId = Long.parseLong(id);
                log.debug("Job promotion time clean ran finished promotion with id {}", promotionId);
                promotionService.finishedPromotionByTime(promotionId);
            } catch (NumberFormatException ex) {
                log.warn("Invalid promotion id in queue: {}", id, ex);
            }
        }
        log.info("Job promotion time clean finished");
    }
}