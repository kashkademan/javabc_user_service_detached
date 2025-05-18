package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.EventStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusFilterTest {
    private final EventStatusFilter statusFilter = new EventStatusFilter();

    @Test
    void testIsApplicable_whenCriteriaExists_thenReturnTrue() {
        EventFilterDto filter = EventFilterDto.builder().eventStatus(EventStatus.IN_PROGRESS).build();

        assertTrue(statusFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicable_whenCriteriaIsNull_thenReturnFalse() {
        EventFilterDto filter = EventFilterDto.builder().eventStatus(null).build();

        assertFalse(statusFilter.isApplicable(filter));
    }

    @Test
    void testApply_whenPartiallyPassed_thenReturnFilteredList() {
        EventStatus firstEventStatus = EventStatus.IN_PROGRESS;
        EventStatus secondEventStatus = EventStatus.PLANNED;
        Stream<EventDto> eventStream = Stream.of(EventDto.builder().eventStatus(firstEventStatus).build(),
                EventDto.builder().eventStatus(secondEventStatus).build());

        List<EventDto> filteredEvents = statusFilter.apply(eventStream, EventFilterDto.builder().eventStatus(firstEventStatus).build()).toList();

        assertEquals(1, filteredEvents.size());
        assertEquals(firstEventStatus, filteredEvents.get(0).getEventStatus());
    }
}