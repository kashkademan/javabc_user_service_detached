package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class EventTitleContainsFilterTest {

    private final EventTitleContainsFilter filter = new EventTitleContainsFilter();

    @Test
    void isApplicable_ShouldReturnTrueWhenTitleContainsIsNotNullAndNotBlank() {
        EventFilterDto filterDto = new EventFilterDto("meeting", null, null, null, null);

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenTitleContainsIsNull() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenTitleContainsIsBlank() {
        EventFilterDto filterDto = new EventFilterDto("   ", null, null, null, null);

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldFilterEventsByTitleContainingTextIgnoringCase() {
        Event event1 = Event.builder().title("Team Meeting Agenda").build();
        Event event2 = Event.builder().title("Weekly team meeting").build();
        Event event3 = Event.builder().title("Code Review Session").build();

        EventFilterDto filterDto = new EventFilterDto("MEETING", null, null, null, null);
        Stream<Event> result = filter.apply(Stream.of(event1, event2, event3), filterDto);

        List<Event> filteredEvents = result.toList();
        assertEquals(2, filteredEvents.size());
        assertTrue(filteredEvents.contains(event1));
        assertTrue(filteredEvents.contains(event2));
    }

    @Test
    void apply_ShouldExcludeEventsWithNullTitle() {
        Event event1 = Event.builder().title("Team Meeting").build();
        Event event2 = Event.builder().title(null).build();

        EventFilterDto filterDto = new EventFilterDto("meeting", null, null, null, null);
        Stream<Event> result = filter.apply(Stream.of(event1, event2), filterDto);

        assertEquals(1, result.count());
    }
}
