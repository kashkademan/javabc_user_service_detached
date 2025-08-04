package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumRemoverScheduler {
    private static final String SCHEDULED_REMOVAL_STARTED = "Starting scheduled removal of expired premium accesses";
    private static final String SCHEDULED_REMOVAL_COMPLETED = "Scheduled removal completed successfully";
    private static final String SCHEDULED_REMOVAL_ERROR = "Error during scheduled premium removal";
    private static final String CRITICAL_FAILURE_MESSAGE = "Critical failure in premium removal";

    private final PremiumService premiumService;

    @Scheduled(cron = "${premium.scheduler.time-to-run-remove-expired-premium}")
    public void removeExpiredPremium() {
        try {
            log.info(SCHEDULED_REMOVAL_STARTED);
            premiumService.removeAllExpiredPremiumAccesses();
            log.info(SCHEDULED_REMOVAL_COMPLETED);
        } catch (Exception e) {
            log.error(SCHEDULED_REMOVAL_ERROR, e);
            throw new RuntimeException(CRITICAL_FAILURE_MESSAGE, e);
        }
    }
}
