package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class EventTypeFilterTest {

    private final EventTypeFilter filter = new EventTypeFilter();

    @Test
    void isApplicable_ShouldReturnTrueWhenTypeIsNotNull() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, EventType.WEBINAR);

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenTypeIsNull() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldFilterEventsByExactTypeMatch() {
        Event event1 = Event.builder().type(EventType.WEBINAR).build();
        Event event2 = Event.builder().type(EventType.MEETING).build();
        Event event3 = Event.builder().type(EventType.WEBINAR).build();

        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, EventType.WEBINAR);
        Stream<Event> result = filter.apply(Stream.of(event1, event2, event3), filterDto);

        List<Event> filteredEvents = result.toList();
        assertEquals(2, filteredEvents.size());
        assertTrue(filteredEvents.contains(event1));
        assertTrue(filteredEvents.contains(event3));
    }

    @Test
    void apply_ShouldExcludeEventsWithNullType() {
        Event event1 = Event.builder().type(EventType.WEBINAR).build();
        Event event2 = Event.builder().type(null).build();

        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, EventType.WEBINAR);
        Stream<Event> result = filter.apply(Stream.of(event1, event2), filterDto);

        assertEquals(1, result.count());
    }
}
