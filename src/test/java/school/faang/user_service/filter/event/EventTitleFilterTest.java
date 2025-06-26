package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EventTitleFilterTest {
    private final EventTitleFilter eventTitleFilter = new EventTitleFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventTitleFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .title("Java")
                                .build());

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventTitleFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .build());

        assertFalse(result);
    }

    @Test
    public void testIsApplicableFalseWhenTitleIsBlank() {
        boolean result = eventTitleFilter
                .isApplicable(
                        EventFilterDto
                                .builder()
                                .title("  ")
                                .build());

        assertFalse(result);
    }

    @Test
    public void testApplySuccess() {
        Stream<Event> eventStream = Stream.of(
                Event.builder().title("Java").build(),
                Event.builder().title("SQL").build());

        Stream<Event> resultEventStream = eventTitleFilter
                .apply(eventStream,
                        EventFilterDto
                                .builder()
                                .title("SQL")
                                .build());

        List<Event> events = resultEventStream.toList();

        assertEquals(1, events.size());
        assertEquals("SQL", events.get(0).getTitle());
    }
}
