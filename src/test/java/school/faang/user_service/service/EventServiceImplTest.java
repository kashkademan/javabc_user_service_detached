package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.event.EventServiceImpl;
import school.faang.user_service.service.filter.event.EventFilterServiceImpl;

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
    private EventFilterServiceImpl eventFilterService;
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
        eventService = new EventServiceImpl(eventRepository,
                userRepository,
                eventMapper,
                userContext,
                eventFilterService);
    }

    @Test
    @DisplayName("Фильтрация событий через EventFilterServiceImpl работает корректно")
    void testGetList_withFilterService() {
        EventFilterDto dto = new EventFilterDto();

        Event event1 = new Event();
        Event event2 = new Event();
        List<Event> allEvents = List.of(event1, event2);
        List<Event> filteredEvents = List.of(event1);

        when(eventRepository.findAll()).thenReturn(allEvents);
        when(eventFilterService.getFilteredList(allEvents, dto)).thenReturn(filteredEvents);

        EventViewDto dto1 = new EventViewDto();
        when(eventMapper.toViewDto(event1)).thenReturn(dto1);

        List<EventViewDto> result = eventService.getList(dto);

        assertEquals(1, result.size());
        assertEquals(dto1, result.get(0));

        verify(eventRepository).findAll();
        verify(eventFilterService).getFilteredList(allEvents, dto);
        verify(eventMapper).toViewDto(event1);
    }

    @Test
    @DisplayName("Если фильтры ничего не отфильтровали — возвращается весь список")
    void testGetList_noFilteringApplied_returnsAllEvents() {
        final EventFilterDto dto = new EventFilterDto();

        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);
        List<Event> allEvents = List.of(event1, event2);
        List<Event> filteredEvents = allEvents;

        EventViewDto dto1 = new EventViewDto();
        EventViewDto dto2 = new EventViewDto();

        when(eventRepository.findAll()).thenReturn(allEvents);
        when(eventFilterService.getFilteredList(allEvents, dto)).thenReturn(filteredEvents);
        when(eventMapper.toViewDto(event1)).thenReturn(dto1);
        when(eventMapper.toViewDto(event2)).thenReturn(dto2);

        List<EventViewDto> result = eventService.getList(dto);

        assertEquals(2, result.size());
        assertEquals(List.of(dto1, dto2), result);

        verify(eventRepository).findAll();
        verify(eventFilterService).getFilteredList(allEvents, dto);
        verify(eventMapper).toViewDto(event1);
        verify(eventMapper).toViewDto(event2);
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
    @DisplayName("Update throws ForbiddenException если пользователь не владелец события")
    void shouldThrowForbiddenExceptionWhenUpdateByNotOwner() {
        final long userId = 42L;
        final long eventId = 99L;

        User owner = new User();
        owner.setId(100L);

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);

        when(userContext.getUserId()).thenReturn(userId);
        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(event);

        EventUpdateDto updateDto = new EventUpdateDto();

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> eventService.update(eventId, updateDto));

        assertEquals("User 42 is not owner of event 99", ex.getMessage());
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

    @Test
    @DisplayName("Delete throws ForbiddenException если пользователь не владелец события")
    void shouldThrowForbiddenExceptionWhenDeleteByNotOwner() {
        final long userId = 42L;
        final long eventId = 99L;

        User owner = new User();
        owner.setId(100L);

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);

        when(userContext.getUserId()).thenReturn(userId);
        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(event);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () ->
                eventService.delete(eventId));

        assertEquals("User 42 is not owner of event 99", ex.getMessage());
    }


}