package school.faang.user_service.service.event;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private UserContext userContext;

    private static final Long OWNER_ID = 1L;
    private static final Long EVENT_ID = 10L;
    private static final String DEFAULT_LOCATION = "location";

    private User user;
    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(OWNER_ID)
                .build();

        event = Event.builder()
                .id(EVENT_ID)
                .owner(user)
                .title("Title")
                .description("Desc")
                .status(EventStatus.PLANNED)
                .type(EventType.MEETING)
                .location(DEFAULT_LOCATION)
                .build();

        eventDto = EventDto.builder()
                .id(EVENT_ID)
                .title("Title")
                .description("Desc")
                .status(EventStatus.PLANNED)
                .type(EventType.MEETING)
                .ownerId(OWNER_ID)
                .name("Event name")
                .skills(Set.of())
                .build();

        when(userContext.getUserId()).thenReturn(OWNER_ID);
    }

    @Test
    void testCreateEvent() {
        CreateEventDto dto = CreateEventDto.builder()
                .title("Title")
                .description("Desc")
                .type(EventType.MEETING)
                .build();

        Event unsavedEvent = Event.builder().build();

        when(userRepository.getByIdOrThrow(OWNER_ID)).thenReturn(user);
        when(eventMapper.toEvent(dto)).thenReturn(unsavedEvent);
        when(eventRepository.save(unsavedEvent)).thenReturn(event);
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        EventDto result = eventService.create(dto);

        assertEquals(EVENT_ID, result.id());
        verify(eventRepository).save(unsavedEvent);
    }

    @Test
    void testUpdateEvent() {
        UpdateEventDto dto = UpdateEventDto.builder()
                .title("Updated Title")
                .description("Updated Desc")
                .type(EventType.MEETING)
                .build();

        when(eventRepository.getByIdOrThrow(EVENT_ID)).thenReturn(event);

        doAnswer(invocation -> {
            UpdateEventDto update = invocation.getArgument(0);
            Event ev = invocation.getArgument(1);
            ev.setTitle(update.title());
            ev.setDescription(update.description());
            ev.setType(update.type());
            return null;
        }).when(eventMapper).update(dto, event);

        EventDto updatedDto = EventDto.builder()
                .id(EVENT_ID)
                .title("Updated Title")
                .description("Updated Desc")
                .status(EventStatus.PLANNED)
                .ownerId(OWNER_ID)
                .type(EventType.MEETING)
                .skills(Set.of())
                .build();

        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventDto(event)).thenReturn(updatedDto);
        EventDto result = eventService.update(EVENT_ID, dto);

        assertEquals("Updated Title", result.title());
        verify(eventRepository).save(event);
    }

    @Test
    void testGetByFilters_withMatchingEvent() {
        EventFilterDto filter = EventFilterDto.builder()
                .titleContains("Title")
                .build();

        event.setTitle("Title matches");
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        List<EventDto> result = eventService.getByFilters(filter);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).title());
    }

    @Test
    void testGetByFilters_withNoMatchingEvents() {
        EventFilterDto filter = EventFilterDto.builder()
                .titleContains("non-existent")
                .build();

        event.setTitle("Some event");
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<EventDto> result = eventService.getByFilters(filter);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDelete_successful() {
        when(eventRepository.deleteById(OWNER_ID, EVENT_ID)).thenReturn(1);
        eventService.delete(EVENT_ID);

        verify(eventRepository).deleteById(OWNER_ID, EVENT_ID);
    }

    @Test
    void testDelete_notFoundOrAccessDenied() {
        when(eventRepository.deleteById(OWNER_ID, EVENT_ID));

        assertThrows(EntityNotFoundException.class, () -> eventService.delete(EVENT_ID));
    }
}