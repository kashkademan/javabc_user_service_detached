package school.faang.user_service.service.event;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.RequestEventDto;
import school.faang.user_service.dto.event.ResponseEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.filter.event.EventLocationFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Spy
    private EventTitleFilter eventTitleFilter;
    @Spy
    private EventLocationFilter eventLocationFilter;
    @Mock
    private UserRepository userRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Captor
    private ArgumentCaptor<Event> captor;

    private EventService eventService;

    @BeforeEach
    public void setUp() {
        eventService = new EventService(
                eventRepository,
                eventMapper,
                userService,
                skillService,
                List.of(eventLocationFilter, eventTitleFilter));
    }

    private record EventDtoTestData(RequestEventDto requestEventDto, List<Skill> eventSkills) {
    }

    private static User getUserTestData(Long userId, List<Skill> userSkills) {
        return User.builder()
                .id(userId)
                .city("Moscow")
                .username("John")
                .skills(userSkills)
                .build();
    }

    private static @NotNull EventDtoTestData getEventDtoTestData() {
        RequestEventDto requestEventDto = RequestEventDto.builder()
                .title("event")
                .description("descr")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusWeeks(1L))
                .location("Tula")
                .ownerId(1L)
                .eventType(EventType.MEETING)
                .eventStatus(EventStatus.COMPLETED)
                .relatedSkills(List.of(1L, 3L))
                .build();

        List<Skill> eventSkills = List.of(
                Skill.builder()
                        .id(1L)
                        .title("Java")
                        .build(),
                Skill.builder()
                        .id(3L)
                        .title("Python")
                        .build()
        );
        return new EventDtoTestData(requestEventDto, eventSkills);
    }

    @Test
    public void testCreateEventNotValidSkills() {
        EventDtoTestData eventDtoTestData = getEventDtoTestData();

        List<Skill> userSkills = List.of(
                Skill.builder()
                        .id(1L)
                        .title("Java")
                        .build(),
                Skill.builder()
                        .id(2L)
                        .title("SQL")
                        .build()
        );

        User user = getUserTestData(1L, userSkills);

        when(userService.getUserById(1L)).thenReturn(user);
        when(skillService.getSkillsByIds(List.of(1L, 3L))).thenReturn(eventDtoTestData.eventSkills());

        assertThrows(DataValidationException.class, () -> eventService.create(eventDtoTestData.requestEventDto()));
    }

    @Test
    public void testCreateEventValidSkills() {
        EventDtoTestData eventDtoTestData = getEventDtoTestData();

        List<Skill> userSkills = List.of(
                Skill.builder()
                        .id(1L)
                        .title("Java")
                        .build(),
                Skill.builder()
                        .id(3L)
                        .title("SQL")
                        .build()
        );

        User user = getUserTestData(1L, userSkills);

        when(userService.getUserById(1L)).thenReturn(user);
        when(skillService.getSkillsByIds(List.of(1L, 3L))).thenReturn(eventDtoTestData.eventSkills());

        assertDoesNotThrow(() -> eventService.create(eventDtoTestData.requestEventDto()));
    }

    @Test
    public void testCreateSuccess() {
        EventDtoTestData eventDtoTestData = getEventDtoTestData();

        List<Skill> userSkills = List.of(
                Skill.builder()
                        .id(1L)
                        .title("Java")
                        .build(),
                Skill.builder()
                        .id(3L)
                        .title("SQL")
                        .build()
        );

        User user = getUserTestData(1L, userSkills);

        when(userService.getUserById(1L)).thenReturn(user);
        when(skillService.getSkillsByIds(List.of(1L, 3L))).thenReturn(eventDtoTestData.eventSkills());

        RequestEventDto requestEventDto = eventDtoTestData.requestEventDto;
        ResponseEventDto responseEventDto = eventService.create(requestEventDto);

        assertEquals(requestEventDto.getTitle(), responseEventDto.getTitle());
        assertEquals(requestEventDto.getStartDate(), responseEventDto.getStartDate());
        assertEquals(requestEventDto.getEndDate(), responseEventDto.getEndDate());
        assertEquals(requestEventDto.getOwnerId(), responseEventDto.getOwnerId());
        assertEquals(requestEventDto.getDescription(), responseEventDto.getDescription());
        assertEquals(requestEventDto.getRelatedSkills(), responseEventDto.getRelatedSkills());
        assertEquals(requestEventDto.getMaxAttendees(), responseEventDto.getMaxAttendees());
        assertEquals(requestEventDto.getEventType(), responseEventDto.getEventType());
        assertEquals(requestEventDto.getEventStatus(), responseEventDto.getEventStatus());

        verify(eventRepository).save(captor.capture());

        Event event = captor.getValue();

        assertEquals(event.getTitle(), requestEventDto.getTitle());
        assertEquals(event.getDescription(), requestEventDto.getDescription());
        assertEquals(event.getLocation(), requestEventDto.getLocation());
    }

    @Test
    public void testUpdateEventNotFoundException() {
        EventDtoTestData eventDtoTestData = getEventDtoTestData();
        eventDtoTestData.requestEventDto.setId(1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(eventDtoTestData.requestEventDto()));
    }

    @Test
    public void testUpdateEventSuccess() {
        EventDtoTestData eventDtoTestData = getEventDtoTestData();
        eventDtoTestData.requestEventDto.setId(1L);
        List<Skill> skills = List.of(
                Skill.builder()
                        .id(1L)
                        .title("Java")
                        .build(),
                Skill.builder()
                        .id(3L)
                        .title("SQL")
                        .build()
        );

        User user = getUserTestData(1L, skills);

        Event event = Event.builder()
                .id(1L)
                .title("event test")
                .description("description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .location("Moscow")
                .maxAttendees(10)
                .attendees(List.of())
                .ratings(List.of())
                .owner(user)
                .relatedSkills(skills)
                .type(EventType.MEETING)
                .status(EventStatus.PLANNED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userService.getUserById(1L)).thenReturn(user);
        when(skillService.getSkillsByIds(List.of(1L, 3L))).thenReturn(eventDtoTestData.eventSkills());

        ResponseEventDto responseEventDto = eventService.updateEvent(eventDtoTestData.requestEventDto);
        RequestEventDto requestEventDto = eventDtoTestData.requestEventDto;

        assertEquals(requestEventDto.getId(), responseEventDto.getId());
        assertEquals(requestEventDto.getTitle(), responseEventDto.getTitle());
        assertEquals(requestEventDto.getStartDate(), responseEventDto.getStartDate());
        assertEquals(requestEventDto.getEndDate(), responseEventDto.getEndDate());
        assertEquals(requestEventDto.getOwnerId(), responseEventDto.getOwnerId());
        assertEquals(requestEventDto.getDescription(), responseEventDto.getDescription());
        assertEquals(requestEventDto.getRelatedSkills(), responseEventDto.getRelatedSkills());
        assertEquals(requestEventDto.getMaxAttendees(), responseEventDto.getMaxAttendees());
        assertEquals(requestEventDto.getEventType(), responseEventDto.getEventType());
        assertEquals(requestEventDto.getEventStatus(), responseEventDto.getEventStatus());

        verify(eventRepository).save(captor.capture());
        Event captureEvent = captor.getValue();

        assertEquals(event.getCreatedAt(), captureEvent.getCreatedAt());
    }

    @Test
    public void testEventByFilter() {
        Event eventOne = Event.builder()
                .id(1L)
                .title("Java")
                .location("Moscow")
                .description("test descr")
                .build();

        Event eventTwo = Event.builder()
                .id(2L)
                .title("SQL")
                .location("Moscow")
                .description("test descr test")
                .build();

        Event eventThree = Event.builder()
                .id(3L)
                .title("C#")
                .location("Tula")
                .description("test descr test")
                .build();


        when(eventRepository.findAll()).thenReturn(List.of(eventOne, eventTwo, eventThree));

        List<ResponseEventDto> responseEventDto = eventService
                .getEventsByFilter(
                        EventFilterDto
                                .builder()
                                .location("Moscow")
                                .title("Java")
                                .build());

        assertEquals(1, responseEventDto.size());
    }

    @Test
    public void testEventByFilterNoFilter() {
        Event eventOne = Event.builder()
                .id(1L)
                .title("Java")
                .location("Moscow")
                .description("test descr")
                .build();

        Event eventTwo = Event.builder()
                .id(2L)
                .title("SQL")
                .location("Moscow")
                .description("test descr test")
                .build();

        Event eventThree = Event.builder()
                .id(3L)
                .title("C#")
                .location("Tula")
                .description("test descr test")
                .build();


        when(eventRepository.findAll()).thenReturn(List.of(eventOne, eventTwo, eventThree));

        List<ResponseEventDto> responseEventDto = eventService
                .getEventsByFilter(
                        EventFilterDto
                                .builder()
                                .build());

        assertEquals(3, responseEventDto.size());
    }

    @Test
    public void testDeleteEventSuccess() {
        eventService.deleteEvent(1L);
        verify(eventRepository).deleteById(anyLong());
    }

    @Test
    public void testGetOwnedEventsSuccess() {
        eventService.getOwnedEvents(1L);
        verify(eventRepository).findAllByUserId(anyLong());
    }

    @Test
    public void testGetParticipatedEventsSuccess() {
        eventService.getParticipatedEvents(1L);
        verify(eventRepository).findParticipatedEventsByUserId(anyLong());
    }
}
