package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventTypeFilterTest {
    private final EventTypeFilter eventTypeFilter = new EventTypeFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventTypeFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .eventType(EventType.MEETING)
                                .build());

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventTypeFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .build());

        assertFalse(result);
    }

    @Test
    public void testApplySuccess() {
        Stream<Event> eventStream = Stream.of(
                Event.builder()
                        .type(EventType.MEETING).build(),
                Event.builder()
                        .type(EventType.POLL).build());

        Stream<Event> resultEventStream = eventTypeFilter
                .apply(eventStream,
                        EventFilterDto
                                .builder()
                                .eventType(EventType.MEETING)
                                .build());

        List<Event> events = resultEventStream.toList();

        assertEquals(1, events.size());
        assertEquals(EventType.MEETING, events.get(0).getType());
    }
}
