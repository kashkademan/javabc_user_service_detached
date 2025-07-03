package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Component
@RequiredArgsConstructor
public class PremiumRemover {

    private final PremiumService premiumService;
    private final PremiumRemoverProperties properties;


    @Scheduled(cron = "#{@premiumRemoverProperties.cron}")
    private void premiumRemover() {
        premiumService.removeExpiredPremiums(properties.getBatchSize());
    }
}
