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
import school.faang.user_service.filter.event.EventStatusFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
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
    private final static long DEFAULT_EVENT_ID = 2L;
    private final static long ADDITIONAL_EVENT_ID = 3L;

    private static User user;
    private static Event event;
    private static EventDto eventDto;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Spy
    private EventTitleFilter titleFilter;
    @Spy
    private EventStatusFilter statusFilter;
    private EventService eventService;

    @BeforeEach
    public void setUp() {
        eventService = new EventServiceImpl(eventRepository, userRepository,
                eventMapper, List.of(titleFilter, statusFilter));

        user = User.builder()
                .id(USER_ID)
                .skills(List.of())
                .build();

        event = Event.builder()
                .id(DEFAULT_EVENT_ID)
                .title(DEFAULT_EVENT_TITLE)
                .owner(user)
                .build();

        eventDto = EventDto.builder()
                .id(DEFAULT_EVENT_ID)
                .title(DEFAULT_EVENT_TITLE)
                .ownerId(USER_ID)
                .build();
    }

    @Test
    void testCreate_whenValid_thenSuccess() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(eventRepository.save(any())).thenReturn(event);

        EventDto savedEventDto = eventService.create(eventDto);

        assertEquals(USER_ID, savedEventDto.getOwnerId());
        assertEquals(DEFAULT_EVENT_ID, savedEventDto.getId());
        assertEquals(DEFAULT_EVENT_TITLE, savedEventDto.getTitle());
    }

    @Test
    void testCreate_whenUnexistingUser_thenThrowException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_whenMismatchSkills_thenThrowException() {
        long skillId = 1L;
        eventDto.setRelatedSkills(List.of(skillId));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals("Owner should have all event's skills.", exception.getMessage());
    }

    @Test
    void testGetEvent_whenEventExists_thenReturnEvent() {
        when(eventRepository.findById(DEFAULT_EVENT_ID)).thenReturn(Optional.of(event));

        EventDto foundEventDto = eventService.getEvent(DEFAULT_EVENT_ID);

        assertEquals(DEFAULT_EVENT_ID, foundEventDto.getId());
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
        Event additionalEvent = createEvent(ADDITIONAL_EVENT_ID, user, TITLE_FOR_FILTER);
        additionalEvent.setStatus(STATUS_FOR_FILTER);
        EventDto additionalEventDto = createEventDto(ADDITIONAL_EVENT_ID, USER_ID, TITLE_FOR_FILTER);
        additionalEventDto.setEventStatus(STATUS_FOR_FILTER);
        when(eventRepository.findAll()).thenReturn(List.of(event, additionalEvent));

        List<EventDto> events = eventService.getEventsByFilter(EventFilterDto.builder().eventStatus(STATUS_FOR_FILTER).build());

        assertEquals(1, events.size());
        assertTrue(events.contains(additionalEventDto));
        assertFalse(events.contains(eventDto));
    }

    @Test
    void testDeleteEvent() {
        assertDoesNotThrow(() -> eventService.deleteEvent(DEFAULT_EVENT_ID));
        verify(eventRepository, times(1)).deleteById(DEFAULT_EVENT_ID);
    }

    @Test
    void testUpdateEvent_whenValid_thenReturnUpdatedEvent() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(eventRepository.save(any())).thenReturn(event);

        EventDto savedEventDto = eventService.create(eventDto);

        assertEquals(DEFAULT_EVENT_ID, savedEventDto.getId());
        assertEquals(USER_ID, savedEventDto.getOwnerId());
        assertEquals(DEFAULT_EVENT_TITLE, savedEventDto.getTitle());
    }

    @Test
    void testGetOwnedEvents_whenEventsExists_thenReturnList() {
        Event additionalEvent = createEvent(ADDITIONAL_EVENT_ID, user, DEFAULT_EVENT_TITLE);
        when(eventRepository.findAllByUserId(USER_ID)).thenReturn(List.of(event, additionalEvent));

        List<EventDto> ownedEvents = eventService.getOwnedEvents(USER_ID);

        verify(eventRepository, times(1)).findAllByUserId(USER_ID);
        assertEquals(2, ownedEvents.size());
        assertTrue(ownedEvents.contains(eventMapper.toEventDto(event)));
        assertTrue(ownedEvents.contains(eventMapper.toEventDto(additionalEvent)));
    }

    @Test
    void testGetParticipatedEvents_whenEventsExists_thenReturnList() {
        event.setAttendees(List.of(user));
        Event additionalEvent = createEvent(ADDITIONAL_EVENT_ID, user, DEFAULT_EVENT_TITLE);
        additionalEvent.setAttendees(List.of(user));
        when(eventRepository.findParticipatedEventsByUserId(USER_ID)).thenReturn(List.of(event, additionalEvent));

        List<EventDto> attendedEvents = eventService.getParticipatedEvents(USER_ID);

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(USER_ID);
        assertEquals(2, attendedEvents.size());
        assertTrue(attendedEvents.contains(eventMapper.toEventDto(event)));
        assertTrue(attendedEvents.contains(eventMapper.toEventDto(additionalEvent)));
    }


    private Event createEvent(long id, User user, String title) {
        return Event.builder()
                .id(id)
                .owner(user)
                .title(title)
                .relatedSkills(List.of())
                .build();
    }

    private EventDto createEventDto(long id, long user, String title) {
        return EventDto.builder()
                .id(id)
                .title(title)
                .ownerId(user)
                .relatedSkills(List.of())
                .build();
    }
}