package school.faang.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.event.EventServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тесты EventServiceImpl")
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
    @DisplayName("Успешное создание события")
    void shouldCreateEventSuccessfully() {
        final long userId = 42L;
        when(userContext.getUserId()).thenReturn(userId);

        Skill skill = new Skill();
        skill.setId(1L);

        User user = new User();
        user.setId(userId);
        user.setSkills(List.of(skill));

        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);

        EventCreateDto dto = new EventCreateDto();
        Event event = new Event();
        event.setRelatedSkills(List.of(skill));
        when(eventMapper.toEntity(dto)).thenReturn(event);

        Event savedEvent = new Event();
        savedEvent.setId(100L);
        savedEvent.setOwner(user);
        savedEvent.setRelatedSkills(List.of(skill));
        when(eventRepository.save(event)).thenReturn(savedEvent);

        EventViewDto viewDto = new EventViewDto();
        viewDto.setId(100L);
        viewDto.setTitle("Test");
        when(eventMapper.toViewDto(savedEvent)).thenReturn(viewDto);

        EventViewDto result = eventService.create(dto);

        assertEquals(100L, result.getId());
        assertEquals("Test", result.getTitle());

        verify(userContext).getUserId();
        verify(userRepository).getByIdOrThrow(userId);
        verify(eventRepository).save(event);
        verify(eventMapper).toViewDto(savedEvent);
    }

    @Test
    @DisplayName("Падает при создании, если пользователь не найден")
    void shouldFailCreateIfUserNotFound() {
        when(userContext.getUserId()).thenReturn(123L);
        when(userRepository.getByIdOrThrow(123L))
                .thenThrow(new IllegalStateException("User not found"));
        when(eventMapper.toEntity(any(EventCreateDto.class))).thenReturn(new Event());

        EventCreateDto dto = new EventCreateDto();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> eventService.create(dto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    @DisplayName("Успешное обновление события")
    void shouldUpdateEventSuccessfully() {
        final long userId = 42L;
        final long eventId = 99L;

        Skill skill = new Skill();
        skill.setId(1L);

        User owner = new User();
        owner.setId(userId);
        owner.setSkills(List.of(skill));

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);
        event.setRelatedSkills(List.of(skill));

        when(userContext.getUserId()).thenReturn(userId);
        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(event);

        final EventUpdateDto updateDto = new EventUpdateDto();
        EventViewDto viewDto = new EventViewDto();
        viewDto.setId(eventId);
        viewDto.setTitle("Updated");
        when(eventMapper.toViewDto(event)).thenReturn(viewDto);
        when(eventRepository.save(event)).thenReturn(event);

        EventViewDto result = eventService.update(eventId, updateDto);

        assertEquals("Updated", result.getTitle());
        verify(eventMapper).update(updateDto, event);
        verify(eventRepository).save(event);
    }

    @Test
    @DisplayName("Удаление работает, если пользователь — владелец")
    void shouldDeleteEventIfOwner() {
        final long userId = 42L;
        final long eventId = 1L;

        User owner = new User();
        owner.setId(userId);
        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);

        when(userContext.getUserId()).thenReturn(userId);
        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(event);

        eventService.delete(eventId);

        verify(eventRepository).delete(event);
    }
}