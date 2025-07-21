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
public class EventTitleFilterTest {
    @InjectMocks
    private EventTitleFilter eventTitleFilter;

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventTitleFilter.isApplicable(new EventFilterDto("title", null, null, null, null));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventTitleFilter.isApplicable(new EventFilterDto(null, null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testIsApplicableFalse_WhenEmpty() {
        boolean result = eventTitleFilter.isApplicable(new EventFilterDto("", null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testIsApplicableFalse_IsBlank() {
        boolean result = eventTitleFilter.isApplicable(new EventFilterDto("   ", null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApply_returnsOneEventWhenTitleMatches() {
        Stream<Event> events = Stream.of(Event.builder().title("news").build(), Event.builder().title("management").build());

        Stream<Event> event = eventTitleFilter.apply(events, new EventFilterDto("news", null, null, null, null));

        List<Event> eventList = event.toList();

        assertEquals(1, eventList.size());
        assertTrue(eventList.get(0).getTitle().contains("news"));
    }

    @Test
    public void testApply_returnsEmptyWhenNoTitleMatches() {
        Stream<Event> events = Stream.of(Event.builder().description("news").build(), Event.builder().description("management").build());

        Stream<Event> event = eventTitleFilter.apply(events, new EventFilterDto(null, "cats", null, null, null));

        List<Event> eventList = event.toList();

        assertEquals(0, eventList.size());
    }

    @Test
    public void testApply_MatchesTitleIgnoringCase() {
        Stream<Event> events = Stream.of(Event.builder().title("News").build(), Event.builder().title("manAgeMent").build());

        Stream<Event> event = eventTitleFilter.apply(events, new EventFilterDto("Management", null, null, null, null));

        List<Event> eventList = event.toList();

        assertEquals(1, eventList.size());
        assertTrue(containsIgnoreCase(eventList.get(0).getTitle(), "management"));
    }

}
