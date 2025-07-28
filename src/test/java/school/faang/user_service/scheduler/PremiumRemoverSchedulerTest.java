package school.faang.user_service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.premium.PremiumService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumRemoverSchedulerTest {
    @InjectMocks
    private PremiumRemoverScheduler premiumRemoverScheduler;

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumService premiumService;

    @Mock
    private PremiumListPartitioner listPartitioner;

    private List<Premium> expiredPremiums;
    private List<List<Premium>> batches;
    private static final int BATCH_SIZE = 100;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(premiumRemoverScheduler, "batchSize", BATCH_SIZE);

        Premium premium1 = createExpiredPremium(1L);
        Premium premium2 = createExpiredPremium(2L);
        Premium premium3 = createExpiredPremium(3L);
        expiredPremiums = Arrays.asList(premium1, premium2, premium3);

        List<Premium> batch1 = Arrays.asList(premium1, premium2);
        List<Premium> batch2 = Arrays.asList(premium3);
        batches = Arrays.asList(batch1, batch2);
    }

    @Test
    public void testRemoveExpiredPremium_successful() {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(expiredPremiums);
        when(listPartitioner.partition(expiredPremiums, BATCH_SIZE))
                .thenReturn(batches);
        when(premiumService.removeExpiredPremiumAccess(any(List.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        premiumRemoverScheduler.removeExpiredPremium();

        verify(premiumRepository, times(1))
                .findAllByEndDateBefore(any(LocalDateTime.class));
        verify(listPartitioner, times(1))
                .partition(expiredPremiums, BATCH_SIZE);
        verify(premiumService, times(batches.size()))
                .removeExpiredPremiumAccess(any(List.class));
        verify(premiumService).removeExpiredPremiumAccess(batches.get(0));
        verify(premiumService).removeExpiredPremiumAccess(batches.get(1));
    }

    @Test
    void removeExpiredPremium_WhenNoExpiredPremiums_ShouldReturnEarly() {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        premiumRemoverScheduler.removeExpiredPremium();

        verify(premiumRepository, times(1))
                .findAllByEndDateBefore(any(LocalDateTime.class));
        verify(listPartitioner, never()).partition(any(), anyInt());
        verify(premiumService, never()).removeExpiredPremiumAccess(any());
    }

    @Test
    void removeExpiredPremium_WhenServiceThrowsException_ShouldPropagateException() {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(expiredPremiums);
        when(listPartitioner.partition(expiredPremiums, BATCH_SIZE))
                .thenReturn(batches);


        RuntimeException expectedException = new RuntimeException("Service error");
        when(premiumService.removeExpiredPremiumAccess(batches.get(0)))
                .thenReturn(CompletableFuture.failedFuture(expectedException));
        when(premiumService.removeExpiredPremiumAccess(batches.get(1)))
                .thenReturn(CompletableFuture.completedFuture(null));

        assertThrows(RuntimeException.class, () -> {
            premiumRemoverScheduler.removeExpiredPremium();
        }, "Метод должен пробросить исключение при ошибке в сервисе");

        verify(premiumRepository, times(1))
                .findAllByEndDateBefore(any(LocalDateTime.class));
        verify(listPartitioner, times(1))
                .partition(expiredPremiums, BATCH_SIZE);
        verify(premiumService, times(batches.size()))
                .removeExpiredPremiumAccess(any(List.class));
    }

    private Premium createExpiredPremium(Long id) {
        Premium premium = new Premium();
        premium.setId(id);
        premium.setEndDate(LocalDateTime.now().minusDays(5));
        return premium;
    }
}
