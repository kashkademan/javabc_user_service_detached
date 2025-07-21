package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EventTypeFilterTest {
    @InjectMocks
    private EventTypeFilter eventTypeFilter;

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventTypeFilter.isApplicable(new EventFilterDto(null, null, null, null, EventType.PRESENTATION));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventTypeFilter.isApplicable(new EventFilterDto(null, null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApply_ReturnsEventsWithGivenType() {
        Stream<Event> events = Stream.of(Event.builder().type(EventType.PRESENTATION).build(), Event.builder().type(EventType.GIVEAWAY).build(), Event.builder().type(EventType.POLL).build());

        Stream<Event> event = eventTypeFilter.apply(events, new EventFilterDto(null, null, null, null, EventType.GIVEAWAY));

        List<Event> eventList = event.toList();

        assertEquals(1, eventList.size());
        assertEquals(EventType.GIVEAWAY, eventList.get(0).getType());
    }

    @Test
    public void testApply_ReturnsEmptyEventTypeNotFound() {
        Stream<Event> events = Stream.of(Event.builder().type(EventType.PRESENTATION).build(), Event.builder().type(EventType.GIVEAWAY).build(), Event.builder().type(EventType.POLL).build());

        Stream<Event> event = eventTypeFilter.apply(events, new EventFilterDto(null, null, null, null, EventType.MEETING));

        List<Event> eventList = event.toList();

        assertEquals(0, eventList.size());
    }

}
