package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.EventType;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeFilterTest {
    private final EventTypeFilter typeFilter = new EventTypeFilter();

    @Test
    void testIsApplicable_whenCriteriaExists_thenReturnTrue() {
        EventFilterDto filter = EventFilterDto.builder().eventType(EventType.POLL).build();

        assertTrue(typeFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicable_whenCriteriaIsNull_thenReturnFalse() {
        EventFilterDto filter = EventFilterDto.builder().title(null).build();

        assertFalse(typeFilter.isApplicable(filter));
    }

    @Test
    void testApply_whenPartiallyPassed_thenReturnFilteredList() {
        EventType firstEventType = EventType.MEETING;
        EventType secondEventType = EventType.POLL;
        Stream<EventDto> eventStream = Stream.of(EventDto.builder().eventType(firstEventType).build(),
                EventDto.builder().eventType(secondEventType).build());

        List<EventDto> filteredEvents = typeFilter.apply(eventStream, EventFilterDto.builder().eventType(firstEventType).build()).toList();

        assertEquals(1, filteredEvents.size());
        assertEquals(firstEventType, filteredEvents.get(0).getEventType());
    }
}