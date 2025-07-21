package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
public class EventParticipantFilterTest {
    @InjectMocks
    private EventParticipantFilter eventParticipantFilter;

    private User participant1;
    private User participant2;

    @BeforeEach
    void setUp() {
        participant1 = User.builder().id(1L).build();
        participant2 = User.builder().id(2L).build();
    }

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventParticipantFilter.isApplicable(new EventFilterDto(null, null, null, 11L, null));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventParticipantFilter.isApplicable(new EventFilterDto(null, null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApply_returnsOneEventWhenParticipantFound() {
        Stream<Event> events = Stream.of(Event.builder().attendees(List.of(participant1, participant2)).build());

        Stream<Event> event = eventParticipantFilter.apply(events, new EventFilterDto(null, null, null, 1L, null));

        List<Event> eventList = event.toList();

        assertEquals(1, eventList.size());
        assertEquals(1L, eventList.get(0).getAttendees().get(0).getId());
    }

    @Test
    public void testApply_ReturnsEmptyParticipantIdNotFound() {
        Stream<Event> events = Stream.of(Event.builder().attendees(List.of(participant1, participant2)).build());

        Stream<Event> event = eventParticipantFilter.apply(events, new EventFilterDto(null, null, null, 3L, null));

        List<Event> eventList = event.toList();

        assertEquals(0, eventList.size());
    }

}
