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
class EventOwnerIdFilterTest {

    private final EventOwnerIdFilter filter = new EventOwnerIdFilter();

    @Test
    void isApplicable_ShouldReturnTrueWhenOwnerIdIsNotNull() {
        EventFilterDto filterDto = new EventFilterDto(null, null, 123L, null, null);

        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenOwnerIdIsNull() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);

        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldFilterEventsByOwnerId() {
        User owner1 = User.builder().id(1L).build();
        User owner2 = User.builder().id(2L).build();

        Event event1 = Event.builder().owner(owner1).build();
        Event event2 = Event.builder().owner(owner2).build();
        Event event3 = Event.builder().owner(owner1).build();

        EventFilterDto filterDto = new EventFilterDto(null, null, 1L, null, null);
        Stream<Event> result = filter.apply(Stream.of(event1, event2, event3), filterDto);

        List<Event> filteredEvents = result.toList();
        assertEquals(2, filteredEvents.size());
        assertTrue(filteredEvents.contains(event1));
        assertTrue(filteredEvents.contains(event3));
    }

    @Test
    void apply_ShouldExcludeEventsWithNullOwner() {
        Event event1 = Event.builder().owner(User.builder().id(1L).build()).build();
        Event event2 = Event.builder().owner(null).build();

        EventFilterDto filterDto = new EventFilterDto(null, null, 1L, null, null);
        Stream<Event> result = filter.apply(Stream.of(event1, event2), filterDto);

        assertEquals(1, result.count());
    }

    @Test
    void apply_ShouldHandleOwnerIdMismatch() {
        Event event = Event.builder().owner(User.builder().id(1L).build()).build();

        EventFilterDto filterDto = new EventFilterDto(null, null,  999L, null, null);
        Stream<Event> result = filter.apply(Stream.of(event), filterDto);

        assertEquals(0, result.count());
    }
}
