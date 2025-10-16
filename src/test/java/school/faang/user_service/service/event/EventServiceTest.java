package school.faang.user_service.service.event;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
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
    private UserContext userContext;

    private static final Long OWNER_ID = 1L;
    private static final Long EVENT_ID = 10L;

    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        Skill skill1 = Skill.builder().id(1L).title("Java").build();
        Skill skill2 = Skill.builder().id(2L).title("Python").build();

        user = User.builder()
                .id(OWNER_ID)
                .skills(List.of(skill1, skill2))
                .build();

        event = Event.builder()
                .id(EVENT_ID)
                .owner(user)
                .title("Title")
                .description("Desc")
                .status(EventStatus.PLANNED)
                .type(EventType.MEETING)
                .location("location")
                .build();
    }

    @Test
    void testCreate_mapsAllFieldsCorrectly() {
        EventCreateDto dto = EventCreateDto.builder()
                .title("New Event")
                .description("Desc")
                .type(EventType.MEETING)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .skillsId(Set.of(1L, 2L))
                .build();

        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(userRepository.getByIdOrThrow(OWNER_ID)).thenReturn(user);
        eventService.create(dto);

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());

        Event savedEvent = captor.getValue();

        assertThat(savedEvent.getTitle()).isEqualTo(dto.title());
        assertThat(savedEvent.getDescription()).isEqualTo(dto.description());
        assertThat(savedEvent.getType()).isEqualTo(dto.type());
        assertThat(savedEvent.getStartDate()).isEqualTo(dto.startDate());
        assertThat(savedEvent.getEndDate()).isEqualTo(dto.endDate());
        assertThat(savedEvent.getOwner()).isEqualTo(user);
        assertThat(savedEvent.getStatus()).isEqualTo(EventStatus.PLANNED);
    }

    @Test
    void testUpdate_mapsAllFieldsCorrectly() {
        EventUpdateDto dto = EventUpdateDto.builder()
                .title("Updated Title")
                .description("Updated Desc")
                .type(EventType.MEETING)
                .startDate(LocalDateTime.of(2025, 10, 16, 9, 0))
                .endDate(LocalDateTime.of(2025, 10, 16, 10, 0))
                .build();

        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(eventRepository.getByIdOrThrow(EVENT_ID)).thenReturn(event);
        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Event updated = eventService.update(EVENT_ID, dto);

        assertEquals("Updated Title", event.getTitle());
        assertEquals("Updated Desc", event.getDescription());
        assertEquals(EventType.MEETING, event.getType());
        assertEquals(LocalDateTime.of(2025, 10, 16, 9, 0), event.getStartDate());
        assertEquals(LocalDateTime.of(2025, 10, 16, 10, 0), event.getEndDate());

        assertEquals(OWNER_ID, updated.getOwner().getId());
        assertEquals(event.getStatus(), updated.getStatus());
        assertEquals(event.getLocation(), updated.getLocation());

        verify(eventRepository).save(updated);
    }

    @Test
    void testGetByFilters_withNullFilters_returnsAll() {
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<EventDto> result = eventService.getByFilters(null).stream()
                .map(EventMapper::toEventDto)
                .toList();;

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

        List<EventDto> result = eventService.getByFilters(filter).stream()
                .map(EventMapper::toEventDto)
                .toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByFilters_withMatchingEvent() {
        EventFilterDto filter = EventFilterDto.builder()
                .titleContains("Title")
                .descriptionContains("Desc")
                .ownerId(OWNER_ID)
                .type(EventType.MEETING)
                .build();

        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> result = eventService.getByFilters(filter);

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }

    @Test
    void testDelete_success() {
        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventRepository.deleteById(EVENT_ID, OWNER_ID)).thenReturn(1);

        eventService.delete(EVENT_ID);

        verify(eventRepository).deleteById(EVENT_ID, OWNER_ID);
    }

    @Test
    void testDelete_fails_throwsEntityNotFound() {
        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventService.delete(EVENT_ID));
    }

    @Test
    void testDelete_deleteByIdReturnsZero_throwsEntityNotFoundException() {
        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventRepository.deleteById(EVENT_ID, OWNER_ID)).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> eventService.delete(EVENT_ID));
    }

    @Test
    void testDelete_notOwner_throwsSecurityException() {
        User anotherUser = User.builder().id(999L).build();
        event.setOwner(anotherUser);

        when(userContext.getUserId()).thenReturn(OWNER_ID);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        assertThrows(SecurityException.class, () -> eventService.delete(EVENT_ID));
        verify(eventRepository, never()).deleteById(anyLong(), anyLong());
    }
}