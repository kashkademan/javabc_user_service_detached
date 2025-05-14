package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationFilterTest {
    private final EventLocationFilter locationFilter = new EventLocationFilter();

    @Test
    void testIsApplicableTrue() {
        EventFilterDto filter = EventFilterDto.builder().location("Location").build();

        assertTrue(locationFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableFalse() {
        EventFilterDto filter = EventFilterDto.builder().location(null).build();

        assertFalse(locationFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableBlank() {
        EventFilterDto filter = EventFilterDto.builder().location(" ").build();

        assertFalse(locationFilter.isApplicable(filter));
    }

    @Test
    void testApply() {
        String moscow = "Moscow";
        String novgorod = "Novgorod";
        Stream<EventDto> eventStream = Stream.of(EventDto.builder().location(moscow).build(), EventDto.builder().location(novgorod).build());

        List<EventDto> filteredEvents = locationFilter.apply(eventStream, EventFilterDto.builder().location(moscow).build()).toList();

        assertEquals(1, filteredEvents.size());
        assertEquals(moscow, filteredEvents.get(0).getLocation());
    }
}