package school.faang.user_service.job.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.service.promotion.PromotionService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PromotionCleanByTimeJob {
    private final PromotionService promotionService;

    @Scheduled(cron = "${jobs.promotion.close.time.cron}")
    public void cleanupDeletedPromotionsByTime() {
        log.info("Job promotion clean by time started");
        List<Promotion> promotions = promotionService.getAllActivePromotion();
        promotions.forEach(promotion -> {
            long seconds = Duration.between(LocalDateTime.now(), promotion.getEndDate()).getSeconds();
            if (seconds <= 0) {
                promotionService.finishPromotionByTime(promotion.getId());
                log.debug("Job promotion clean finished promotion by time with id {} on time", promotion.getId());
            }
        });
        log.info("Job promotion clean by time finished");
    }
}
