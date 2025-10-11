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
import static org.mockito.ArgumentMatchers.any;
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
    }

    @Test
    void testCreate_setsStatusAndLocation_andValidates() {
        CreateEventDto dto = CreateEventDto.builder()
                .title("New Event")
                .description("Desc")
                .type(EventType.MEETING)
                .build();

        Event unsaved = new Event();
        Event saved = Event.builder().id(EVENT_ID).build();

        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(userRepository.getByIdOrThrow(OWNER_ID)).thenReturn(user);
        when(eventMapper.toEvent(dto)).thenReturn(unsaved);
        when(eventRepository.save(unsaved)).thenReturn(saved);
        when(eventMapper.toEventDto(any(Event.class))).thenReturn(eventDto);

        EventDto result = eventService.create(dto);

        assertEquals(EventStatus.PLANNED, unsaved.getStatus());
        assertEquals(DEFAULT_LOCATION, unsaved.getLocation());
        verify(eventRepository).save(unsaved);
        assertEquals(EVENT_ID, result.id());
    }

    @Test
    void testUpdate_validatesOwnerAndUpdatesEvent() {
        UpdateEventDto dto = UpdateEventDto.builder()
                .title("Updated")
                .description("Updated Desc")
                .type(EventType.MEETING)
                .build();

        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(eventRepository.getByIdOrThrow(EVENT_ID)).thenReturn(event);

        doAnswer(invocation -> {
            UpdateEventDto update = invocation.getArgument(0);
            Event ev = invocation.getArgument(1);
            ev.setTitle(update.title());
            ev.setDescription(update.description());
            ev.setType(update.type());
            return null;
        }).when(eventMapper).update(dto, event);

        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventDto(event)).thenAnswer(invocation ->
                EventDto.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .status(event.getStatus())
                        .type(event.getType())
                        .ownerId(event.getOwner().getId())
                        .skills(Set.of())
                        .build()
        );

        EventDto result = eventService.update(EVENT_ID, dto);

        verify(eventRepository).getByIdOrThrow(EVENT_ID);
        verify(eventRepository).save(event);
        verify(eventMapper).update(dto, event);
        verify(eventMapper).toEventDto(event);

        assertEquals("Updated", result.title());
        assertEquals("Updated Desc", result.description());
        assertEquals(EventType.MEETING, result.type());
    }

    @Test
    void testGetByFilters_withAllFilters() {
        EventFilterDto filter = EventFilterDto.builder()
                .titleContains("Title")
                .descriptionContains("Desc")
                .ownerId(OWNER_ID)
                .type(EventType.MEETING)
                .build();

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toEventDto(any(Event.class))).thenReturn(eventDto);

        List<EventDto> result = eventService.getByFilters(filter);

        assertEquals(1, result.size());
        verify(eventMapper).toEventDto(event);
    }

    @Test
    void testGetByFilters_withNullFilters_returnsAll() {
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        List<EventDto> result = eventService.getByFilters(null);

        assertEquals(1, result.size());
        assertEquals(EVENT_ID, result.get(0).id());
    }

    @Test
    void testGetByFilters_multipleConditions_noMatch() {
        EventFilterDto filter = EventFilterDto.builder()
                .titleContains("wrong")
                .ownerId(999L)
                .build();

        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<EventDto> result = eventService.getByFilters(filter);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDelete_success() {
        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(eventRepository.deleteById(OWNER_ID, EVENT_ID)).thenReturn(1);

        assertDoesNotThrow(() -> eventService.delete(EVENT_ID));
        verify(eventRepository).deleteById(OWNER_ID, EVENT_ID);
    }

    @Test
    void testDelete_fails_throwsEntityNotFound() {
        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(eventRepository.deleteById(OWNER_ID, EVENT_ID)).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> eventService.delete(EVENT_ID));
    }
}