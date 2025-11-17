package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ChunkDeletionService chunkDeletionService;

    @Mock
    private ThreadPoolTaskExecutor asyncExecutor;

    @InjectMocks
    private EventService eventService;

    private static final int testPageSize = 2000;
    List<Long> testEventIds = List.of(1L, 2L, 3L, 4L, 5L);
    Page<Long> emptyPage = Page.empty();

    @BeforeEach
    void setup() {
        eventService = new EventService(
                eventRepository,
                asyncExecutor,
                chunkDeletionService
        );
    }

    @Test
    void testClearPastEventsSuccess() {
        Pageable pageable = Pageable.ofSize(testPageSize);
        Page<Long> newPage = new PageImpl<>(testEventIds, pageable, 5);

        Mockito.when(eventRepository.findAllIdByStatus(EventStatus.COMPLETED,
                PageRequest.of(0, testPageSize))).thenReturn(newPage);
        Mockito.when(eventRepository.findAllIdByStatus(EventStatus.COMPLETED,
                PageRequest.of(1, testPageSize))).thenReturn(emptyPage);
        eventService.clearPastEvents(testPageSize);

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(asyncExecutor).execute(captor.capture());
        captor.getValue().run();

        Mockito.verify(chunkDeletionService).deleteChunk(testEventIds);
    }

    @Test
    void testClearPastEventsIdPageIsEmpty() {
        Page<Long> emptyPage = Page.empty();
        Mockito.when(eventRepository.findAllIdByStatus(EventStatus.COMPLETED,
                PageRequest.of(0, testPageSize))).thenReturn(emptyPage);
        eventService.clearPastEvents(testPageSize);

        Mockito.verify(asyncExecutor, Mockito.never()).execute(Mockito.any());
        Mockito.verify(chunkDeletionService, Mockito.never()).deleteChunk(Mockito.anyList());
    }
}