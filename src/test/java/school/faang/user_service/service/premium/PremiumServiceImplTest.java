package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceImplTest {

    private final int maxDeletePerThread = 2;
    private final int deleteExpiredTimeout = 5;
    private final ExecutorService deletePremiumPool = Executors.newFixedThreadPool(2);

    @Captor
    private ArgumentCaptor<List<Long>> premiumListArgumentCaptor = ArgumentCaptor.forClass(List.class);

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private PremiumServiceImpl premiumService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(premiumService, "maxDeletePerThread", maxDeletePerThread);
        ReflectionTestUtils.setField(premiumService, "deleteExpiredTimeout", deleteExpiredTimeout);
        ReflectionTestUtils.setField(premiumService, "deletePremiumPool", deletePremiumPool);
    }

    @Test
    void testDeleteExpired() {
        Premium premiumOne = Premium.builder()
                .id(1L)
                .build();
        Premium premiumTwo = Premium.builder()
                .id(2L)
                .build();

        List<Premium> premiums = List.of(premiumOne, premiumTwo);

        Mockito.when(premiumRepository.findAllByEndDateBefore(Mockito.any(LocalDateTime.class))).thenReturn(premiums);
        doNothing().when(premiumRepository).deleteAllById(Mockito.anyList());

        premiumService.deleteExpired();

        verify(premiumRepository).deleteAllById(premiumListArgumentCaptor.capture());

        List<Long> capturedPremiumIds = premiumListArgumentCaptor.getAllValues().stream()
                .flatMap(Collection::stream)
                .toList();
        List<Long> premiumIds = premiums.stream().map(Premium::getId).toList();

        assertEquals(2, capturedPremiumIds.size());
        assertTrue(capturedPremiumIds.containsAll(premiumIds));
    }
}