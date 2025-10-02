package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.filter.Filter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventParticipantFilterTest {

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void apply_success() {
        long userId1 = 1L;
        long userId2 = 2L;
        long userId3 = 3L;

        Event event1 = new Event();
        User user1 = new User();
        user1.setId(userId1);

        Event event2 = new Event();
        User user2 = new User();
        user2.setId(userId2);

        Event event3 = new Event();
        User user3 = new User();
        user3.setId(userId3);

        event1.setAttendees(List.of(user1, user2));
        event2.setAttendees(List.of(user3, user2));
        event3.setAttendees(Collections.emptyList());

        EventFilterDto eventFilterDto = new EventFilterDto(null,
                null,
                null,
                userId1,
                null,
                List.of(1L, 2L));

        Stream<Event> events = Stream.of(event3, event2, event3);
        Filter<Event, EventFilterDto> eventParticipantFilter = new EventParticipantFilter();
        assertTrue(
                eventParticipantFilter.apply(events, eventFilterDto)
                        .allMatch(event -> event.equals(event1))
        );
    }
}