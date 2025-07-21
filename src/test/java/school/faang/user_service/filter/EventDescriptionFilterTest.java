package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EventDescriptionFilterTest {
    @InjectMocks
    private EventDescriptionFilter eventDescriptionFilter;

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventDescriptionFilter
                .isApplicable(new EventFilterDto(
                        null, "Description", null, null, null));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventDescriptionFilter
                .isApplicable(new EventFilterDto(
                        null, null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testIsApplicableFalse_WhenEmpty() {
        boolean result = eventDescriptionFilter
                .isApplicable(new EventFilterDto(
                        null, "", null, null, null));

        assertFalse(result);
    }

    @Test
    public void testIsApplicableFalse_IsBlank() {
        boolean result = eventDescriptionFilter
                .isApplicable(new EventFilterDto(
                        null, "   ", null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApply_ReturnsOneEventWhenDescriptionMatches() {
        Stream<Event> events = Stream.of(Event.builder()
                .description("News").build(),
                Event.builder().description("plane")
                        .build());

        Stream<Event> event = eventDescriptionFilter
                .apply(events, new EventFilterDto(
                        null, "plane", null, null, null));

        List<Event> eventList = event.toList();

        assertEquals(1, eventList.size());
        assertTrue(eventList.get(0).getDescription().contains("plane"));
    }

    @Test
    public void testApply_ReturnsEmptyWhenNoDescriptionMatches() {
        Stream<Event> events = Stream.of(Event.builder()
                .description("News").build(), Event.builder().description("plane").build());

        Stream<Event> event = eventDescriptionFilter
                .apply(events, new EventFilterDto(
                        null, "cats", null, null, null));

        List<Event> eventList = event.toList();

        assertEquals(0, eventList.size());
    }

    @Test
    public void testApply_MatchesDescriptionIgnoringCase() {
        Stream<Event> events = Stream.of(Event.builder()
                .description("News").build(),
                Event.builder().description("pLaNe")
                        .build());

        Stream<Event> event = eventDescriptionFilter
                .apply(events, new EventFilterDto(
                        null, "PlAnE", null, null, null));

        List<Event> eventList = event.toList();

        assertEquals(1, eventList.size());
        assertTrue(containsIgnoreCase(eventList.get(0).getDescription(), "plane"));
    }


}
