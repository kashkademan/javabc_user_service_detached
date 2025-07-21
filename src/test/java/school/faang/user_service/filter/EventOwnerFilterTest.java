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
public class EventOwnerFilterTest {
    @InjectMocks
    private EventOwnerFilter eventOwnerFilter;

    private User owner1;
    private User owner2;

    @BeforeEach
    void setUp() {
        owner1 = User.builder().id(1L).build();
        owner2 = User.builder().id(2L).build();
    }

    @Test
    public void testIsApplicableTrue() {
        boolean result = eventOwnerFilter.isApplicable(new EventFilterDto(null, null, 1L, null, null));

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = eventOwnerFilter.isApplicable(new EventFilterDto(null, null, null, null, null));

        assertFalse(result);
    }

    @Test
    public void testApple_ReturnsOneEventWhenOwnerFound() {
        Stream<Event> events = Stream.of(Event.builder().owner(owner1).build(), Event.builder().owner(owner2).build());

        Stream<Event> event = eventOwnerFilter.apply(events, new EventFilterDto(null, null, 1L, null, null));

        List<Event> eventList = event.toList();

        assertEquals(1, eventList.size());
        assertEquals(1L, eventList.get(0).getOwner().getId());
    }

    @Test
    public void testApply_ReturnsEmptyWhenOwnerIdNotFound() {
        Stream<Event> events = Stream.of(Event.builder().owner(owner1).build(), Event.builder().owner(owner2).build());

        Stream<Event> event = eventOwnerFilter.apply(events, new EventFilterDto(null, null, 3L, null, null));

        List<Event> eventList = event.toList();

        assertEquals(0, eventList.size());
    }

}
