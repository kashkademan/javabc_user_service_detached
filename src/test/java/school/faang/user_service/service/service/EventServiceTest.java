package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventService eventService;
    private EventRepository eventRepository;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        executor = Executors.newCachedThreadPool();
        eventService = new EventService(eventRepository, 2, executor);
    }

    @Test
    void testClearEvents_whenNoPastEvents_thenReturnZero() {
        Event futureEvent = new Event();
        futureEvent.setId(1L);
        futureEvent.setEndDate(LocalDateTime.now().plusDays(1));

        when(eventRepository.findAll()).thenReturn(List.of(futureEvent));

        int deletedCount = eventService.clearEvents();

        assertEquals(0, deletedCount);
        verify(eventRepository, never()).deleteByIds(anyList());
    }

    @Test
    void testClearEvents_whenPastEventsExist_thenDeleteInBatches() {
        Event past1 = new Event();
        past1.setId(1L);
        past1.setEndDate(LocalDateTime.now().minusDays(1));
        Event past2 = new Event();
        past2.setId(2L);
        past2.setEndDate(LocalDateTime.now().minusDays(2));
        Event future = new Event();
        future.setId(3L);
        future.setEndDate(LocalDateTime.now().plusDays(1));

        when(eventRepository.findAll()).thenReturn(List.of(past1, past2, future));

        int deletedCount = eventService.clearEvents();

        assertEquals(2, deletedCount);

        verify(eventRepository).deleteByIds(List.of(1L, 2L));
    }

    @Test
    void testClearEvents_whenInterrupted_thenHandleGracefully() throws InterruptedException {
        Event past = new Event();
        past.setId(1L);
        past.setEndDate(LocalDateTime.now().minusDays(1));
        when(eventRepository.findAll()).thenReturn(List.of(past));

        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.invokeAll(anyList())).thenThrow(new InterruptedException());

        eventService = new EventService(eventRepository, 2, mockExecutor);

        int result = eventService.clearEvents();

        assertEquals(1, result);
        verify(mockExecutor).invokeAll(anyList());
    }

    @Test
    void testShutdownExecutor_completesGracefully() throws InterruptedException {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.awaitTermination(anyLong(), any())).thenReturn(true);

        eventService = new EventService(eventRepository, 2, mockExecutor);
        eventService.shutdownExecutor();

        verify(mockExecutor).shutdown();
        verify(mockExecutor, times(1)).awaitTermination(anyLong(), any());
        verify(mockExecutor, never()).shutdownNow();
    }

    @Test
    void testShutdownExecutor_forcesShutdownIfNotTerminated() throws InterruptedException {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.awaitTermination(anyLong(), any())).thenReturn(false);

        eventService = new EventService(eventRepository, 2, mockExecutor);
        eventService.shutdownExecutor();

        verify(mockExecutor).shutdown();
        verify(mockExecutor).shutdownNow();
    }

    @Test
    void testShutdownExecutor_whenInterrupted_thenForceShutdown() throws InterruptedException {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.awaitTermination(anyLong(), any())).thenThrow(new InterruptedException());

        eventService = new EventService(eventRepository, 2, mockExecutor);
        eventService.shutdownExecutor();

        verify(mockExecutor).shutdown();
        verify(mockExecutor).shutdownNow();
    }
}
