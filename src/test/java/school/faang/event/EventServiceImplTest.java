package school.faang.event;

import com.amazonaws.services.kms.model.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.event.EventServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class EventServiceImplTest {
    @Mock
    private UserContext userContext;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateEventSuccessfully() {
        final long userId = 42L;
        when(userContext.getUserId()).thenReturn(userId);

        User user = new User();
        user.setId(userId);
        Skill skill = new Skill();
        skill.setId(1L);
        user.setSkills(List.of(skill));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        CreateEventDto createEventDto = new CreateEventDto();

        Event event = new Event();
        event.setRelatedSkills(List.of(skill));

        when(eventMapper.toEvent(createEventDto)).thenReturn(event);

        Event savedEvent = new Event();
        savedEvent.setId(100L);
        savedEvent.setOwner(user);
        savedEvent.setRelatedSkills(List.of(skill));

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        EventDto expectedDto = new EventDto();
        expectedDto.setId(100L);
        expectedDto.setTitle("Test Event");

        when(eventMapper.toEventDto(savedEvent)).thenReturn(expectedDto);

        EventDto result = eventService.create(createEventDto);

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getTitle(), result.getTitle());

        verify(userContext).getUserId();
        verify(userRepository).findById(userId);
        verify(eventMapper).toEvent(createEventDto);
        verify(eventRepository).save(event);
        verify(eventMapper).toEventDto(savedEvent);
    }

    @Test
    void create_shouldThrowException_whenUserNotFound() {
        final long userId = 42L;

        when(userContext.getUserId()).thenReturn(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CreateEventDto createEventDto = new CreateEventDto();

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            eventService.create(createEventDto);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void update_shouldThrowNotFoundException_whenEventNotFound() {
        final long eventId = 1L;
        final long userId = 42L;

        when(userContext.getUserId()).thenReturn(userId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        UpdateEventDto updateDto = new UpdateEventDto();

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            eventService.update(eventId, updateDto);
        });

        assertTrue(thrown.getMessage().contains("Event not found"));
    }

    @Test
    void update_shouldUpdateEventSuccessfully() {
        final long eventId = 1L;
        final long userId = 42L;

        when(userContext.getUserId()).thenReturn(userId);

        User owner = new User();
        owner.setId(userId);
        Skill skill = new Skill();
        skill.setId(1L);
        owner.setSkills(List.of(skill));

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);
        event.setRelatedSkills(List.of(skill));

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        UpdateEventDto updateDto = new UpdateEventDto();

        doAnswer(invocation -> {
            UpdateEventDto dtoArg = invocation.getArgument(0);
            Event eventArg = invocation.getArgument(1);
            return null;
        }).when(eventMapper).update(updateDto, event);

        when(eventRepository.save(event)).thenReturn(event);

        EventDto expectedDto = new EventDto();
        expectedDto.setId(eventId);
        expectedDto.setTitle("Updated title");
        when(eventMapper.toEventDto(event)).thenReturn(expectedDto);

        EventDto result = eventService.update(eventId, updateDto);

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getTitle(), result.getTitle());

        verify(userContext).getUserId();
        verify(eventRepository).findById(eventId);
        verify(eventMapper).update(updateDto, event);
        verify(eventRepository).save(event);
        verify(eventMapper).toEventDto(event);
    }

    @Test
    void getByFilters_shouldReturnNonEmptyList_whenRepositoryHasEvents() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Sample Event");
        User owner = new User();
        owner.setId(100L);
        event.setOwner(owner);
        event.setAttendees(List.of());
        event.setType(EventType.WEBINAR);

        when(eventRepository.findAll()).thenReturn(List.of(event));

        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Sample Event");
        when(eventMapper.toEventDto(any())).thenReturn(eventDto);

        EventFilterDto filters = new EventFilterDto();

        List<EventDto> result = eventService.getByFilters(filters);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void delete_shouldDeleteEventSuccessfully_whenUserIsOwner() {
        final long eventId = 1L;
        final long userId = 42L;

        User owner = new User();
        owner.setId(userId);

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);

        when(userContext.getUserId()).thenReturn(userId);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.delete(eventId);

        verify(eventRepository).delete(event);

        verify(eventRepository).findById(eventId);
    }
}