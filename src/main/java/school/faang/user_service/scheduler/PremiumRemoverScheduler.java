package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumRemoverScheduler {
    private static final String NO_EXPIRED_PREMIUM_FOUND = "There is no expired premium subscription found";
    private static final String START_REMOVING = "Start to remove all expired premium accesses";
    private static final String SUCCESS_REMOVING = "All expired premium accesses were successfully removed";

    private final PremiumRepository premiumRepository;
    private final PremiumService premiumService;
    private final PremiumListPartitioner listPartitioner;

    @Value("${premium.scheduler.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${premium.scheduler.time-to-run-remove-expired-premium}")
    public void removeExpiredPremium() {
        List<Premium> premiumAccesses = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (premiumAccesses.isEmpty()) {
            log.info(NO_EXPIRED_PREMIUM_FOUND);
            return;
        }
        log.info(START_REMOVING);
        List<List<Premium>> batches = listPartitioner.partition(premiumAccesses, batchSize);

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(premiumService::removeExpiredPremiumAccess)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info(SUCCESS_REMOVING);
    }
}
