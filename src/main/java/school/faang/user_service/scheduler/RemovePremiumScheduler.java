package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Component
@RequiredArgsConstructor
public class RemovePremiumScheduler {

    private final PremiumService premiumService;

    @Scheduled(cron = "${premium.delete-premium-cron}")
    public void removePremium() {
        premiumService.deleteExpired();
    }
}