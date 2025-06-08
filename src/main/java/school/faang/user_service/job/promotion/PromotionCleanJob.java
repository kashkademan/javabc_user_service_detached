package school.faang.user_service.job.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.promotion.PromotionService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PromotionCleanJob {
    private final PromotionService promotionService;
    private final PromotionRedisService promotionRedisService;

    @Scheduled(cron = "0 * * * * *")
    public void cleanupDeletedPromotions() {
        log.info("Job promotion clean started");
        List<PromotionRedisModel> promotions = promotionRedisService.getAllPromotions();
        promotions.forEach(promotion -> {
            long seconds = Duration.between(LocalDateTime.now(), promotion.getEndDate()).getSeconds();
            long promotionId = promotion.getId();
            if (seconds <= 0) {
                promotionRedisService.deletePromotionByKey(promotion.getKey());
                promotionService.finishPromotionByTime(promotionId);
                log.debug("Job promotion clean finished promotion with id {} on time", promotionId);
            } else if (promotion.getCountView() <= 0) {
                promotionRedisService.deletePromotionByKey(promotion.getKey());
                promotionService.finishPromotionByView(promotionId);
                log.debug("Job promotion clean finished promotion with id {} on views", promotionId);
            }
        });
        log.info("Job promotion clean finished");
    }
}
