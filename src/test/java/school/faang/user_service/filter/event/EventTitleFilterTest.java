package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.Filter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTitleFilterTest {

    @Test
    public void apply_success() {
        String appropriateTitle = "appropriate title";
        String inappropriateTitle = "inappropriate title";

        Event event1 = new Event();
        event1.setTitle(appropriateTitle);

        Event event2 = new Event();
        event2.setTitle(inappropriateTitle);

        Event event3 = new Event();
        event2.setTitle(appropriateTitle);

        EventFilterDto eventFilterDto = new EventFilterDto("appropriate TITLE",
                null,
                null,
                null,
                null,
                List.of(1L, 2L));

        Stream<Event> events = Stream.of(event1, event2, event3);
        Filter<Event, EventFilterDto> eventTitleFilter = new EventTitleFilter();
        assertTrue(
                eventTitleFilter.apply(events, eventFilterDto)
                        .allMatch(event -> event.getTitle().equalsIgnoreCase(appropriateTitle))
        );
    }
}