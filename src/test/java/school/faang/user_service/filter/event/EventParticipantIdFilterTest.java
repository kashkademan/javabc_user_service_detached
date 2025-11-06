package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class EventParticipantIdFilterTest {

    private final EventParticipantIdFilter filter = new EventParticipantIdFilter();

    @Test
    void isApplicable_ShouldReturnTrueWhenParticipantIdIsNotNull() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, 123L, null);

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenParticipantIdIsNull() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldFilterEventsByParticipantId() {
        User participant1 = User.builder().id(1L).build();
        User participant2 = User.builder().id(2L).build();

        Event event1 = Event.builder().attendees(List.of(participant1, participant2)).build();
        Event event2 = Event.builder().attendees(List.of(participant2)).build();
        Event event3 = Event.builder().attendees(List.of(participant1)).build();

        EventFilterDto filterDto = new EventFilterDto(null, null, null, 1L, null);
        Stream<Event> result = filter.apply(Stream.of(event1, event2, event3), filterDto);

        List<Event> filteredEvents = result.toList();
        assertEquals(2, filteredEvents.size());
        assertTrue(filteredEvents.contains(event1));
        assertTrue(filteredEvents.contains(event3));
    }

    @Test
    void apply_ShouldExcludeEventsWithNullAttendees() {
        Event event1 = Event.builder().attendees(List.of(User.builder().id(1L).build())).build();
        Event event2 = Event.builder().attendees(null).build();

        EventFilterDto filterDto = new EventFilterDto(null, null, null, 1L, null);
        Stream<Event> result = filter.apply(Stream.of(event1, event2), filterDto);

        assertEquals(1, result.count());
    }

    @Test
    void apply_ShouldHandleEmptyAttendeesList() {
        Event event = Event.builder().attendees(List.of()).build();

        EventFilterDto filterDto = new EventFilterDto(null, null, null, 1L, null);
        Stream<Event> result = filter.apply(Stream.of(event), filterDto);

        assertEquals(0, result.count());
    }

    @Test
    void apply_ShouldHandleParticipantNotFound() {
        Event event = Event.builder()
                .attendees(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .build();

        EventFilterDto filterDto = new EventFilterDto(null, null, null, 999L, null);
        Stream<Event> result = filter.apply(Stream.of(event), filterDto);

        assertEquals(0, result.count());
    }
}
