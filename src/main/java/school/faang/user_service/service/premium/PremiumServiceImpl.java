package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PremiumRepository premiumRepository;
    private final ExecutorService deletePremiumPool;

    @Value("${premium.max-delete-per-thread}")
    private int maxDeletePerThread;
    @Value("${premium.delete-expired-timeout}")
    private int deleteExpiredTimeout;

    @Override
    public void deleteExpired() {
        List<Long> expiredPremiumAccesses = premiumRepository.findAllIdsByEndDateBefore(LocalDateTime.now());

        log.info("Found {} expired premium accesses: {}", expiredPremiumAccesses.size(), expiredPremiumAccesses);

        List<List<Long>> parts = ListUtils.partition(expiredPremiumAccesses, maxDeletePerThread);
        List<CompletableFuture<Void>> futures = parts.stream()
                .map(part -> CompletableFuture.runAsync(
                        () -> {
                            log.info("Deleting premium: {}", part);
                            premiumRepository.deleteAllById(part);
                        },
                        deletePremiumPool
                ))
                .toList();

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(deleteExpiredTimeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("Deletion timed out. {}", e.getMessage());
            futures.forEach(future -> future.cancel(true));
        } catch (Exception e) {
            log.error("Error during deletion. {}", e.getMessage());
        }

        log.info("{} expired premium accesses have been deleted", expiredPremiumAccesses.size());
    }
}