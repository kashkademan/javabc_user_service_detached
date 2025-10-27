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
class EventDescriptionContainsFilterTest {

    private final EventDescriptionContainsFilter filter = new EventDescriptionContainsFilter();

    @Test
    void isApplicable_ShouldReturnTrueWhenDescriptionContainsIsNotNullAndNotBlank() {
        EventFilterDto filterDto = new EventFilterDto(null, "search", null, null, null);

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenDescriptionContainsIsNull() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenDescriptionContainsIsBlank() {
        EventFilterDto filterDto = new EventFilterDto(null, "   ", null, null, null);

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldFilterEventsByDescriptionContainingTextIgnoringCase() {
        Event event1 = Event.builder().description("Important meeting about Spring Boot").build();
        Event event2 = Event.builder().description("Spring framework introduction").build();
        Event event3 = Event.builder().description("Java basics workshop").build();

        EventFilterDto filterDto = new EventFilterDto(null, "spring", null, null, null);
        Stream<Event> result = filter.apply(Stream.of(event1, event2, event3), filterDto);

        List<Event> filteredEvents = result.toList();
        assertEquals(2, filteredEvents.size());
        assertTrue(filteredEvents.contains(event1));
        assertTrue(filteredEvents.contains(event2));
    }

    @Test
    void apply_ShouldExcludeEventsWithNullDescription() {
        Event event1 = Event.builder().description("Spring event").build();
        Event event2 = Event.builder().description(null).build();

        EventFilterDto filterDto = new EventFilterDto(null, "spring", null, null, null);
        Stream<Event> result = filter.apply(Stream.of(event1, event2), filterDto);

        assertEquals(1, result.count());
    }

    @Test
    void apply_ShouldHandleEmptyStream() {
        EventFilterDto filterDto = new EventFilterDto(null, "test", null, null, null);
        Stream<Event> result = filter.apply(Stream.empty(), filterDto);

        assertEquals(0, result.count());
    }
}
