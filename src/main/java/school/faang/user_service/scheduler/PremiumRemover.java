package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemover {
    private final PremiumService premiumService;
    @Value("${app.scheduler.premium-remove.batch-size}")
    private final int batchSize;

    @Scheduled(cron = "${app.scheduler.premium-remove.cron}")
    public void removePremium() {
        log.debug("Starting scheduled premium remove");
       premiumService.removePremium(batchSize);
    }
}
