package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventStatusFilterTest {
    private final EventStatusFilter eventStatusFilter = new EventStatusFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventStatusFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .eventStatus(EventStatus.IN_PROGRESS)
                                .build());

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventStatusFilter
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
                        .status(EventStatus.IN_PROGRESS).build(),
                Event.builder()
                        .status(EventStatus.CANCELED).build());

        Stream<Event> resultEventStream = eventStatusFilter
                .apply(eventStream,
                        EventFilterDto
                                .builder()
                                .eventStatus(EventStatus.CANCELED)
                                .build());

        List<Event> events = resultEventStream.toList();

        assertEquals(1, events.size());
        assertEquals(EventStatus.CANCELED, events.get(0).getStatus());
    }
}
