package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TitleFilterTest {
    private final EventTitleFilter titleFilter = new EventTitleFilter();

    @Test
    void testIsApplicableTrue() {
        EventFilterDto filter = EventFilterDto.builder().title("Event title").build();

        assertTrue(titleFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableFalse() {
        EventFilterDto filter = EventFilterDto.builder().title(null).build();

        assertFalse(titleFilter.isApplicable(filter));
    }

    @Test
    void testApply() {
        String firstEventTitle = "First title";
        String secondEventTitle = "Second title";
        Stream<EventDto> eventStream = Stream.of(EventDto.builder().title(firstEventTitle).build(),
                EventDto.builder().title(secondEventTitle).build());

        List<EventDto> filteredEvents = titleFilter.apply(eventStream, EventFilterDto.builder().title(firstEventTitle).build()).toList();

        assertEquals(1, filteredEvents.size());
        assertEquals(firstEventTitle, filteredEvents.get(0).getTitle());
    }
}