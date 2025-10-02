package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.event.EventDescriptionFilter;
import school.faang.user_service.filter.event.EventOwnerFilter;
import school.faang.user_service.filter.event.EventParticipantFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.filter.event.EventTypeFilter;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventTitleFilter eventTitleFilter;
    @Spy
    private EventDescriptionFilter eventDescriptionFilter;
    @Spy
    private EventOwnerFilter eventOwnerFilter;
    @Spy
    private EventParticipantFilter eventParticipantFilter;
    @Spy
    private EventTypeFilter eventTypeFilter;

    @Spy
    private EventMapperImpl eventMapper;

    private EventService eventService;

    private List<Filter<Event, EventFilterDto>> eventFilters;

    @BeforeEach
    public void setup() {
        eventFilters = new ArrayList<>(List.of(eventTitleFilter,
                eventDescriptionFilter,
                eventOwnerFilter,
                eventParticipantFilter,
                eventTypeFilter));
        eventService = new EventService(eventMapper,
                userContext,
                userRepository,
                eventRepository,
                skillRepository,
                eventFilters);
    }

    @Test
    public void create_thenThrowDataValidationException() {
        Event event = new Event();
        event.setStartDate(LocalDateTime.now().minusDays(1));
        event.setEndDate(LocalDateTime.now().minusDays(2));

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.create(event, Collections.emptyList())
        );

        assertEquals(
                "The start date must be no earlier than the current date."
                        + "The start date must be earlier than the end date.",
                exception.getMessage()
        );
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void create_Success() {
        long userId = 12345L;
        User user = new User();
        user.setId(userId);
        List<Skill> skills = List.of(new Skill());
        user.setSkills(skills);


        EventStatus eventStatus = EventStatus.PLANNED;

        Event eventToSave = new Event();
        eventToSave.setStartDate(LocalDateTime.now().plusDays(1));
        eventToSave.setEndDate(LocalDateTime.now().plusDays(2));

        Event savedEvent = eventToSave;
        savedEvent.setOwner(user);
        savedEvent.setStatus(eventStatus);

        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(eventRepository.save(savedEvent)).thenReturn(savedEvent);
        when(skillRepository.findAllById(anyList())).thenReturn(skills);


        assertEquals(
                savedEvent,
                assertDoesNotThrow(() -> eventService.create(eventToSave, anyList()))
        );
        verify(eventRepository).save(eq(savedEvent));
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    public void update_success() {
        long eventId = 123L;
        String title = "Test title";
        String description = "Test description";
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        EventType eventType = EventType.PRESENTATION;
        EventStatus eventStatus = EventStatus.IN_PROGRESS;

        UpdateEventDto updateEventDto = new UpdateEventDto(title,
                description,
                startDate,
                endDate,
                eventType,
                eventStatus,
                null);

        long requesterId = 456L;
        Event expectingEvent = new Event();
        expectingEvent.setId(eventId);
        User user = new User();
        user.setId(requesterId);
        expectingEvent.setOwner(user);

        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(expectingEvent);
        when(userContext.getUserId()).thenReturn(requesterId);

        eventMapper.update(updateEventDto, expectingEvent);
        when(eventRepository.save(expectingEvent)).thenReturn(expectingEvent);

        assertEquals(
                expectingEvent,
                assertDoesNotThrow(() -> eventService.update(eventId, updateEventDto))
        );
    }

    @Test
    public void getByFilters_eventTitleFilterWasInvoked() {
        EventFilterDto eventFilterDto = new EventFilterDto("TestTitle",
                null,
                null,
                null,
                null,
                List.of(1L, 2L));

        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        eventService.getByFilters(eventFilterDto);

        verify(eventTitleFilter).apply(any(), eq(eventFilterDto));
        eventFilters.forEach(filter -> verify(filter).isApplicable(eventFilterDto));
        eventFilters.remove(eventTitleFilter);
        eventFilters.forEach(filter -> verify(filter, never()).apply(any(), eq(eventFilterDto)));
    }

    @Test
    public void getByFilters_eventTitleFilterWasNotInvoked() {
        EventFilterDto eventFilterDto = new EventFilterDto(null,
                "null",
                123L,
                1234L,
                EventType.PRESENTATION,
                List.of(1L, 2L));

        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        eventService.getByFilters(eventFilterDto);

        verify(eventTitleFilter, never()).apply(any(), eq(eventFilterDto));
        eventFilters.forEach(filter -> verify(filter).isApplicable(eventFilterDto));
        eventFilters.remove(eventTitleFilter);
        eventFilters.forEach(filter -> verify(filter).apply(any(Stream.class), eq(eventFilterDto)));
    }
}