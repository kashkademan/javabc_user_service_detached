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
    private final static long USER_ID = 1L;
    private final static long FIRST_EVENT_ID = 2L;
    private final static long SECOND_EVENT_ID = 3L;

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
    void testCreate_whenValid_thenSuccess() {
        String eventTitle = "Created event";
        User user = createUser(USER_ID);
        Event event = createEvent(FIRST_EVENT_ID, user, eventTitle);
        EventDto eventDto = createEventDto(FIRST_EVENT_ID, USER_ID, eventTitle);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(eventRepository.save(any())).thenReturn(event);

        EventDto savedEventDto = eventService.create(eventDto);

        assertEquals(USER_ID, savedEventDto.getOwnerId());
        assertEquals(FIRST_EVENT_ID, savedEventDto.getId());
        assertEquals(eventTitle, savedEventDto.getTitle());
    }

    @Test
    void testCreate_whenUnexistingUser_thenThrowException() {
        EventDto eventDto = createEventDto(FIRST_EVENT_ID, USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_whenMismatchSkills_thenThrowException() {
        long skillId = 1L;
        String eventTitle = "Created event";
        User user = createUser(USER_ID);
        EventDto eventDto = createEventDto(FIRST_EVENT_ID, USER_ID, eventTitle, List.of(skillId));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventService.create(eventDto));

        assertEquals("Owner should have all event's skills.", exception.getMessage());
    }

    @Test
    void testGetEvent_whenEventExists_thenReturnEvent() {
        User user = createUser(USER_ID);
        Event event = createEvent(FIRST_EVENT_ID, user);
        when(eventRepository.findById(FIRST_EVENT_ID)).thenReturn(Optional.of(event));

        EventDto foundEventDto = eventService.getEvent(FIRST_EVENT_ID);

        assertEquals(FIRST_EVENT_ID, foundEventDto.getId());
        assertEquals(USER_ID, foundEventDto.getOwnerId());
    }

    @Test
    void testGetEvent_whenEventNotExists_thenThrowException() {
        long eventId = -1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.getEvent(eventId));
    }

    @Test
    void testGetEventsByFilter_whenPartiallyPassed_thenReturnFilteredList() {
        User user = createUser(USER_ID);
        Event firstEvent = createEvent(FIRST_EVENT_ID, user);
        Event secondEvent = createEvent(SECOND_EVENT_ID, user, TITLE_FOR_FILTER);
        secondEvent.setStatus(STATUS_FOR_FILTER);
        EventDto firstEventDto = createEventDto(FIRST_EVENT_ID, USER_ID);
        EventDto secondEventDto = createEventDto(SECOND_EVENT_ID, USER_ID, TITLE_FOR_FILTER);
        secondEventDto.setEventStatus(STATUS_FOR_FILTER);
        when(eventRepository.findAll()).thenReturn(List.of(firstEvent, secondEvent));

        List<EventDto> events = eventService.getEventsByFilter(EventFilterDto.builder().build());

        assertEquals(1, events.size());
        assertTrue(events.contains(secondEventDto));
        assertFalse(events.contains(firstEventDto));
    }

    @Test
    void testDeleteEvent() {
        assertDoesNotThrow(() -> eventService.deleteEvent(FIRST_EVENT_ID));
        verify(eventRepository, times(1)).deleteById(FIRST_EVENT_ID);
    }

    @Test
    void testUpdateEvent_whenValid_thenReturnUpdatedEvent() {
        String eventTitle = "Updated event";
        User user = createUser(USER_ID);
        Event event = createEvent(FIRST_EVENT_ID, user, eventTitle);
        EventDto eventDto = createEventDto(FIRST_EVENT_ID, USER_ID, eventTitle);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(eventRepository.save(any())).thenReturn(event);

        EventDto savedEventDto = eventService.create(eventDto);

        assertEquals(FIRST_EVENT_ID, savedEventDto.getId());
        assertEquals(USER_ID, savedEventDto.getOwnerId());
        assertEquals(eventTitle, savedEventDto.getTitle());
    }

    @Test
    void testGetOwnedEvents_whenEventsExists_thenReturnList() {
        User user = createUser(USER_ID);
        Event firstEvent = createEvent(FIRST_EVENT_ID, user);
        Event secondEvent = createEvent(SECOND_EVENT_ID, user);
        when(eventRepository.findAllByUserId(USER_ID)).thenReturn(List.of(firstEvent, secondEvent));

        List<EventDto> ownedEvents = eventService.getOwnedEvents(USER_ID);

        verify(eventRepository, times(1)).findAllByUserId(USER_ID);
        assertEquals(2, ownedEvents.size());
        assertTrue(ownedEvents.contains(eventMapper.toEventDto(firstEvent)));
        assertTrue(ownedEvents.contains(eventMapper.toEventDto(secondEvent)));
    }

    @Test
    void testGetParticipatedEvents_whenEventsExists_thenReturnList() {
        User user = createUser(USER_ID);
        Event firstEvent = createEvent(FIRST_EVENT_ID, user);
        firstEvent.setAttendees(List.of(user));
        Event secondEvent = createEvent(SECOND_EVENT_ID, user);
        secondEvent.setAttendees(List.of(user));
        when(eventRepository.findParticipatedEventsByUserId(USER_ID)).thenReturn(List.of(firstEvent, secondEvent));

        List<EventDto> attendedEvents = eventService.getParticipatedEvents(USER_ID);

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(USER_ID);
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