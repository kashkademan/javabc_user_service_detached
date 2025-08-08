package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.exception.EventCleanupException;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Тестирование сервиса для работы с событиями {@link EventServiceImpl}
 *
 * @author Linempy
 * @since 07.08.2025
 */
@DisplayName("Тестирование сервиса для работы с событиями")
@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    private static final int BATCH_SIZE = 100;

    @Mock
    private EventRepository repository;

    private ThreadPoolTaskExecutor taskExecutor;

    private EventServiceImpl service;

    @BeforeEach
    public void setUp() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.initialize();

        service = new EventServiceImpl(repository, executor);

        ReflectionTestUtils.setField(service, "batchSize", BATCH_SIZE);
        ReflectionTestUtils.setField(service, "cleanupTimeoutSeconds", 1L);
    }

    @Test
    @DisplayName("Успешная обработка пустого списка просроченных ивентов")
    public void testCleanupEventsWhenEventsIsEmpty() {
        when(repository.findExpiredEventIds()).thenReturn(List.of());

        service.cleanupExpiredEvents();

        verify(repository, times(1)).findExpiredEventIds();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен обработать небольшое кол-во событий")
    public void testCleanupEventsWhenDeleteInBatches() {
        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L);
        ReflectionTestUtils.setField(service, "batchSize", 3);

        when(repository.findExpiredEventIds()).thenReturn(ids);
        when(repository.deleteByIds(any())).thenAnswer(invocation -> {
            List<Long> batch = invocation.getArgument(0);
            return batch.size();
        });


        assertDoesNotThrow(() -> service.cleanupExpiredEvents());

        verify(repository, times(1)).findExpiredEventIds();
        verify(repository, times(2)).deleteByIds(anyList());
    }

    @Test
    @DisplayName("Должен успешно обработать большое кол-во событий")
    void testCleanupEventsWhenLargeDatasetSuccessful() {
        int totalEvents = 10_000;
        List<Long> massiveList = LongStream.rangeClosed(1, 10_000)
                .boxed()
                .collect(Collectors.toList());

        when(repository.findExpiredEventIds()).thenReturn(massiveList);
        when(repository.deleteByIds(anyList()))
                .thenAnswer(inv -> {
                    List<Long> batch = inv.getArgument(0);
                    return batch.size();
                });
        int expectedBatches = totalEvents / BATCH_SIZE;

        assertDoesNotThrow(() -> service.cleanupExpiredEvents());
        verify(repository, times(expectedBatches))
                .deleteByIds(argThat(list -> list.size() == BATCH_SIZE));
        verify(repository, times(expectedBatches)).deleteByIds(anyList());
    }

    @Test
    @DisplayName("Должен выбросить EventCleanupException, когда наступил таймаут")
    void testCleanupEventsWhenTimeoutShouldThrowException() {
        List<Long> eventIds = LongStream.rangeClosed(1, 500)
                .boxed()
                .collect(Collectors.toList());

        when(repository.findExpiredEventIds()).thenReturn(eventIds);
        when(repository.deleteByIds(anyList())).thenAnswer(inv -> {
            Thread.sleep(2000);
            return 100;
        });

        ReflectionTestUtils.setField(service, "cleanupTimeoutSeconds", 1L);

        assertThrows(EventCleanupException.class, () -> service.cleanupExpiredEvents());
    }

    @Test
    @DisplayName("Должен продолжить обработку после сбоя пакета")
    void testCleanupEventsWhenBatchFailsShouldContinue() {
        List<Long> eventIds = LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        ReflectionTestUtils.setField(service, "batchSize", 3);

        when(repository.findExpiredEventIds()).thenReturn(eventIds);
        when(repository.deleteByIds(anyList()))
                .thenReturn(3)
                .thenThrow(new DataAccessException("DB error") {
                })
                .thenReturn(1);

        assertThrows(EventCleanupException.class, () -> service.cleanupExpiredEvents());

        verify(repository, times(4)).deleteByIds(anyList());
    }

    @Test
    @DisplayName("Должен корректно обработать пустые батчи")
    void testCleanupEventsWhenEmptyBatchShouldNotFail() {
        List<Long> eventIds = List.of(1L, 2L, 3L);
        ReflectionTestUtils.setField(service, "batchSize", 5);

        when(repository.findExpiredEventIds()).thenReturn(eventIds);
        when(repository.deleteByIds(anyList())).thenReturn(3);

        assertDoesNotThrow(() -> service.cleanupExpiredEvents());

        verify(repository).deleteByIds(argThat(list -> list.size() == 3));
    }
}