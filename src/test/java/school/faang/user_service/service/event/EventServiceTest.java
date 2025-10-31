package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    Event event1 = new Event();
    Event event2 = new Event();
    Event event3 = new Event();
    Event event4 = new Event();
    Event event5 = new Event();

    @BeforeEach
    void setUp() {
        event1.setId(1L);
        event2.setId(2L);
        event3.setId(3L);
        event4.setId(4L);
        event5.setId(5L);
    }

    @Test
    void testClearPastEventsSuccess() {
        ReflectionTestUtils.setField(eventService, "chunkSize", 1000);

        List<Event> testEventList = List.of(event1, event2, event3, event4, event5);
        testEventList.forEach((e) -> e.setStatus(EventStatus.COMPLETED));

        Mockito.when(eventRepository.findAll()).thenReturn(testEventList);
        eventService.clearPastEvents();

        List<Long> testEventIds = List
                .of(event1.getId(), event2.getId(), event3.getId(), event4.getId(), event5.getId());
        Mockito.verify(eventRepository).deleteAllById(testEventIds);
    }

    @Test
    void testClearPastEventsIdListIsEmpty() {
        ReflectionTestUtils.setField(eventService, "chunkSize", 1000);

        event1.setStatus(EventStatus.CANCELED);
        event2.setStatus(EventStatus.IN_PROGRESS);
        event3.setStatus(EventStatus.PLANNED);
        event4.setStatus(EventStatus.CANCELED);
        event5.setStatus(EventStatus.PLANNED);

        List<Event> events = List.of(event1, event2, event3, event4, event5);
        Mockito.when(eventRepository.findAll()).thenReturn(events);

        eventService.clearPastEvents();

        Mockito.verify(eventRepository, Mockito.never()).deleteAllById(Mockito.anyList());
    }
}