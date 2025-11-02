package school.faang.user_service.filter.event;


import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.filter.Filter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventOwnerFilterTest {

    @Test
    public void apply_success() {
        long ownerId1 = 1L;
        long ownerId2 = 2L;
        final long ownerId3 = 3L;

        Event event1 = new Event();
        User user1 = new User();
        user1.setId(ownerId1);
        event1.setOwner(user1);

        Event event2 = new Event();
        User user2 = new User();
        user2.setId(ownerId2);
        event2.setOwner(user2);

        Event event3 = new Event();
        User user3 = new User();
        user3.setId(ownerId3);
        event3.setOwner(user3);

        EventFilterDto eventFilterDto = new EventFilterDto(null,
                null,
                ownerId1,
                null,
                null,
                List.of(1L, 2L));

        Stream<Event> events = Stream.of(event3, event2, event3);
        Filter<Event, EventFilterDto> eventOwnerFilter = new EventOwnerFilter();
        assertTrue(
                eventOwnerFilter.apply(events, eventFilterDto)
                        .allMatch(event -> event.equals(event1))
        );
    }
}