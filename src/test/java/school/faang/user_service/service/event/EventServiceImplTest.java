package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    public final static EventStatus STATUS_FOR_FILTER = EventStatus.COMPLETED;
    public final static String TITLE_FOR_FILTER = "Filtered title";

    private final static String DEFAULT_EVENT_TITLE = "Event title";

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapperImpl eventMapper;
    private final EventFilter titleFilter = new TestTitleFilter();
    private final EventFilter statusFilter = new TestStatusFilter();
    private EventService eventService;

    @BeforeEach
    public void setUp() {
        eventService = new EventServiceImpl(eventRepository, userRepository,
                eventMapper, List.of(titleFilter, statusFilter));
    }

    @Test
    void testCreateSuccess() {
        long userId = 1L;
        long eventId = 2L;
        String eventTitle = "Created event";
        User user = createUser(userId);
        Event event = createEvent(eventId, user, eventTitle);
        EventDto eventDto = createEventDto(eventId, userId, eventTitle);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.save(any())).thenReturn(event);

        EventDto savedEventDto = eventService.create(eventDto);

        assertEquals(userId, savedEventDto.getOwnerId());
        assertEquals(eventId, savedEventDto.getId());
        assertEquals(eventTitle, savedEventDto.getTitle());
    }

    @Test
    void testCreateForUnexistingUser() {
        long userId = -1L;
        long eventId = 1L;
        EventDto eventDto = createEventDto(eventId, userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreateWithMismatchSkills() {
        long userId = 1L;
        long eventId = 1L;
        long skillId = 1L;
        String eventTitle = "Created event";
        User user = createUser(userId);
        EventDto eventDto = createEventDto(eventId, userId, eventTitle, List.of(skillId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventService.create(eventDto));

        assertEquals("Owner should have all event's skills.", exception.getMessage());
    }

    @Test
    void testGetEventSuccess() {
        long userId = 2L;
        long eventId = 1L;
        User user = createUser(userId);
        Event event = createEvent(eventId, user);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        EventDto foundEventDto = eventService.getEvent(eventId);

        assertEquals(eventId, foundEventDto.getId());
        assertEquals(userId, foundEventDto.getOwnerId());
    }

    @Test
    void testGetEventNotExisting() {
        long eventId = -1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.getEvent(eventId));
    }

    @Test
    void testGetEventsByFilter() {
        long userId = 1L;
        long firstEventId = 2L;
        long secondEventId = 3L;
        User user = createUser(userId);
        Event firstEvent = createEvent(firstEventId, user);
        Event secondEvent = createEvent(secondEventId, user, TITLE_FOR_FILTER);
        secondEvent.setStatus(STATUS_FOR_FILTER);
        EventDto firstEventDto = createEventDto(firstEventId, userId);
        EventDto secondEventDto = createEventDto(secondEventId, userId, TITLE_FOR_FILTER);
        secondEventDto.setEventStatus(STATUS_FOR_FILTER);
        when(eventRepository.findAll()).thenReturn(List.of(firstEvent, secondEvent));

        List<EventDto> events = eventService.getEventsByFilter(EventFilterDto.builder().build());

        assertEquals(1, events.size());
        assertTrue(events.contains(secondEventDto));
        assertFalse(events.contains(firstEventDto));
    }

    @Test
    void testDeleteEvent() {
        long eventId = 1L;

        assertDoesNotThrow(() -> eventService.deleteEvent(eventId));
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    void testUpdateEvent() {
        long userId = 1L;
        long eventId = 2L;
        String eventTitle = "Updated event";
        User user = createUser(userId);
        Event event = createEvent(eventId, user, eventTitle);
        EventDto eventDto = createEventDto(eventId, userId, eventTitle);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.save(any())).thenReturn(event);

        EventDto savedEventDto = eventService.create(eventDto);

        assertEquals(eventId, savedEventDto.getId());
        assertEquals(userId, savedEventDto.getOwnerId());
        assertEquals(eventTitle, savedEventDto.getTitle());
    }

    @Test
    void testGetOwnedEvents() {
        long userId = 1L;
        long firstEventId = 2L;
        long secondEventId = 3L;
        User user = createUser(userId);
        Event firstEvent = createEvent(firstEventId, user);
        Event secondEvent = createEvent(secondEventId, user);
        when(eventRepository.findAllByUserId(userId)).thenReturn(List.of(firstEvent, secondEvent));

        List<EventDto> ownedEvents = eventService.getOwnedEvents(userId);
        verify(eventRepository, times(1)).findAllByUserId(userId);

        assertEquals(2, ownedEvents.size());
        assertTrue(ownedEvents.contains(eventMapper.toEventDto(firstEvent)));
        assertTrue(ownedEvents.contains(eventMapper.toEventDto(secondEvent)));
    }

    @Test
    void testGetParticipatedEvents() {
        long userId = 1L;
        long firstEventId = 2L;
        long secondEventId = 3L;
        User user = createUser(userId);
        Event firstEvent = createEvent(firstEventId, user);
        firstEvent.setAttendees(List.of(user));
        Event secondEvent = createEvent(secondEventId, user);
        secondEvent.setAttendees(List.of(user));
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(List.of(firstEvent, secondEvent));

        List<EventDto> attendedEvents = eventService.getParticipatedEvents(userId);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);

        assertEquals(2, attendedEvents.size());
        assertTrue(attendedEvents.contains(eventMapper.toEventDto(firstEvent)));
        assertTrue(attendedEvents.contains(eventMapper.toEventDto(secondEvent)));
    }

    private User createUser(long id) {
        return User.builder()
                .id(id)
                .skills(List.of())
                .build();
    }

    private Event createEvent(long id, User user) {
        return createEvent(id, user, DEFAULT_EVENT_TITLE, List.of());
    }

    private Event createEvent(long id, User user, String title) {
        return createEvent(id, user, title, List.of());
    }

    private Event createEvent(long id, User user, String title, List<Skill> skills) {
        return Event.builder()
                .id(id)
                .title(title)
                .owner(user)
                .relatedSkills(skills)
                .build();
    }

    private EventDto createEventDto(long id, long user) {
        return createEventDto(id, user, DEFAULT_EVENT_TITLE, List.of());
    }

    private EventDto createEventDto(long id, long user, String title) {
        return createEventDto(id, user, title, List.of());
    }

    private EventDto createEventDto(long id, long user, String title, List<Long> skills) {
        return EventDto.builder()
                .id(id)
                .title(title)
                .ownerId(user)
                .relatedSkills(skills)
                .build();
    }
}