package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.Filter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventDescriptionFilterTest {

    @Test
    public void apply_success() {
        String appropriateDescription = "appropriate Description";
        String inappropriateDescription = "inappropriate Description";

        Event event1 = new Event();
        event1.setTitle(appropriateDescription);

        Event event2 = new Event();
        event2.setTitle(inappropriateDescription);

        Event event3 = new Event();
        event2.setTitle(appropriateDescription);

        EventFilterDto eventFilterDto = new EventFilterDto(null,
                "APPROPRIATE Description",
                null,
                null,
                null,
                List.of(1L, 2L));

        Stream<Event> events = Stream.of(event1, event2, event3);
        Filter<Event, EventFilterDto> eventDescriptionFilter = new EventDescriptionFilter();
        assertTrue(
                eventDescriptionFilter.apply(events, eventFilterDto)
                        .allMatch(event -> event.getDescription().equalsIgnoreCase(appropriateDescription))
        );
    }
}