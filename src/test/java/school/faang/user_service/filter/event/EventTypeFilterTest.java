package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.filter.Filter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTypeFilterTest {

    @Test
    public void apply_success() {
        EventType eventType1 = EventType.PRESENTATION;
        EventType eventType2 = EventType.GIVEAWAY;
        EventType eventType3 = EventType.PRESENTATION;

        Event event1 = new Event();
        event1.setType(eventType1);

        Event event2 = new Event();
        event1.setType(eventType2);

        Event event3 = new Event();
        event1.setType(eventType3);

        EventFilterDto eventFilterDto = new EventFilterDto(null,
                null,
                null,
                null,
                eventType1,
                List.of(1L, 2L));

        Stream<Event> events = Stream.of(event3, event2, event3);
        Filter<Event, EventFilterDto> eventTypeFilter = new EventTypeFilter();
        assertTrue(
                eventTypeFilter.apply(events, eventFilterDto)
                        .allMatch(event -> event.getType().equals(eventType1))
        );
    }
}