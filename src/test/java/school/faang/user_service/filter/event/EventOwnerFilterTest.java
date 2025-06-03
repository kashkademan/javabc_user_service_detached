package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventOwnerFilterTest {
    private final EventOwnerFilter eventOwnerFilter = new EventOwnerFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventOwnerFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .ownerId(1L)
                                .build());

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventOwnerFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .build());

        assertFalse(result);
    }

    @Test
    public void testApplySuccess() {
        Stream<Event> eventStream = Stream.of(
                Event.builder()
                        .owner(User.builder().id(1L).build()).build(),
                Event.builder()
                        .owner(User.builder().id(3L).build()).build());

        Stream<Event> resultEventStream = eventOwnerFilter
                .apply(eventStream,
                        EventFilterDto
                                .builder()
                                .ownerId(1L)
                                .build());

        List<Event> events = resultEventStream.toList();

        assertEquals(1, events.size());
        assertEquals(1L, events.get(0).getOwner().getId());
    }
}
