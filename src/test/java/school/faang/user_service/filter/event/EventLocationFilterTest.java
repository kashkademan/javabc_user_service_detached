package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventLocationFilterTest {
    private final EventLocationFilter eventLocationFilter = new EventLocationFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventLocationFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .location("Moscow")
                                .build());

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventLocationFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .build());

        assertFalse(result);
    }

    @Test
    public void testIsApplicableFalseWhenLocationIsBlank() {
        boolean result = eventLocationFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .location("  ")
                                .build());

        assertFalse(result);
    }

    @Test
    public void testApplySuccess() {
        Stream<Event> eventStream = Stream.of(
                Event.builder().location("Moscow").build(),
                Event.builder().location("Tula").build());

        Stream<Event> resultEventStream = eventLocationFilter
                .apply(eventStream,
                        EventFilterDto
                                .builder()
                                .location("moscow")
                                .build());

        List<Event> events = resultEventStream.toList();

        assertEquals(1, events.size());
        assertEquals("Moscow", events.get(0).getLocation());
    }
}
