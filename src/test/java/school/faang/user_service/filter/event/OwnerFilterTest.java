package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OwnerFilterTest {
    private final EventOwnerFilter ownerFilter = new EventOwnerFilter();

    @Test
    void testIsApplicable_whenCriteriaExists_thenReturnTrue() {
        EventFilterDto filter = EventFilterDto.builder().ownerId(1L).build();

        assertTrue(ownerFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicable_whenCriteriaIsNull_thenReturnFalse() {
        EventFilterDto filter = EventFilterDto.builder().ownerId(null).build();

        assertFalse(ownerFilter.isApplicable(filter));
    }

    @Test
    void testApply_whenPartiallyPassed_thenReturnFilteredList() {
        long firstUserId = 1L;
        long secondUserId = 2L;
        Stream<EventDto> eventStream = Stream.of(EventDto.builder().ownerId(firstUserId).build(), EventDto.builder().ownerId(secondUserId).build());

        List<EventDto> filteredEvents = ownerFilter.apply(eventStream, EventFilterDto.builder().ownerId(firstUserId).build()).toList();

        assertEquals(1, filteredEvents.size());
        assertEquals(firstUserId, filteredEvents.get(0).getOwnerId());
    }
}