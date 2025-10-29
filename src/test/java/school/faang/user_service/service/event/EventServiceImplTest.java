package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.event.EventDescriptionContainsFilter;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventOwnerIdFilter;
import school.faang.user_service.filter.event.EventParticipantIdFilter;
import school.faang.user_service.filter.event.EventTitleContainsFilter;
import school.faang.user_service.filter.event.EventTypeFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.event.EventValidator;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.preparation.test.PreparationTest.CURRENT_USER_ID;
import static school.faang.user_service.preparation.test.PreparationTest.PLUS_TWO_DAYS;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_1;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_2;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_ID;
import static school.faang.user_service.preparation.test.PreparationTest.OTHER_USER_ID;
import static school.faang.user_service.preparation.test.PreparationTest.OWNER_1;
import static school.faang.user_service.preparation.test.PreparationTest.PLUS_ONE_DAY;
import static school.faang.user_service.preparation.test.PreparationTest.createEvent;
import static school.faang.user_service.preparation.test.PreparationTest.createEventDto;


@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Mock
    private EventValidator eventValidator;

    @Mock
    private UserContext userContext;

    private final List<EventFilter> eventFilters = List.of(
            new EventTitleContainsFilter(),
            new EventDescriptionContainsFilter(),
            new EventOwnerIdFilter(),
            new EventTypeFilter(),
            new EventParticipantIdFilter()
    );

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @InjectMocks
    private EventServiceImpl eventService;

    private User testUser;
    private Event pastEvent1;
    private Event pastEvent2;
    private Event futureEvent1;
    private Event futureEvent2;

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository, eventMapper, eventValidator, userContext, eventFilters);

        testUser = User.builder().id(1L).username("testUser").build();

        pastEvent1 = Event.builder()
                .id(1L)
                .owner(testUser)
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().minusDays(3))
                .status(EventStatus.PLANNED)
                .build();

        pastEvent2 = Event.builder()
                .id(2L)
                .owner(testUser)
                .startDate(LocalDateTime.now().minusDays(10))
                .endDate(LocalDateTime.now().minusDays(8))
                .status(EventStatus.PLANNED)
                .build();

        futureEvent1 = Event.builder()
                .id(3L)
                .owner(testUser)
                .startDate(LocalDateTime.now().plusDays(5))
                .endDate(LocalDateTime.now().plusDays(7))
                .status(EventStatus.PLANNED)
                .build();

        futureEvent2 = Event.builder()
                .id(4L)
                .owner(testUser)
                .startDate(LocalDateTime.now().plusDays(10))
                .endDate(LocalDateTime.now().plusDays(12))
                .status(EventStatus.PLANNED)
                .build();
    }

    @Test
    void create_ShouldValidDataWhenEventCreatedSuccessfully() {
        CreateEventDto createEventDto = createEventDto();

        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(eventValidator.validateAndGetUser(CURRENT_USER_ID)).thenReturn(OWNER_1);
        when(eventRepository.save(any(Event.class))).thenReturn(EVENT_1);

        EventDto result = eventService.create(createEventDto);

        assertNotNull(result);
        verify(eventValidator).validateEventNotInPast(PLUS_ONE_DAY);
        verify(eventValidator).validateEventDates(PLUS_ONE_DAY, PLUS_TWO_DAYS);
        verify(eventRepository).save(eventCaptor.capture());

        Event capturedEvent = eventCaptor.getValue();
        assertEquals(OWNER_1, capturedEvent.getOwner());
        assertEquals(EventStatus.PLANNED, capturedEvent.getStatus());
    }

    @Test
    void create_ShouldThrowsExceptionWhenUserNotFound() {
        CreateEventDto createEventDto = createEventDto();

        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(eventValidator.validateAndGetUser(CURRENT_USER_ID))
                .thenThrow(new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class, () -> eventService.create(createEventDto));

        verify(eventRepository, never()).save(any());
    }

    @Test
    void update_ShouldUpdateEventWhenUserIsOwnerAndDataValid() {
        UpdateEventDto updateEventDto = new UpdateEventDto("New Title", "New Desc",
                PLUS_ONE_DAY.plusDays(1), PLUS_TWO_DAYS.plusDays(1), EventType.WEBINAR, EventStatus.CANCELED);
        Event existingEvent = EVENT_1;

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventDto result = eventService.update(EVENT_ID, updateEventDto);

        assertNotNull(result);
        verify(eventValidator).validateEventOwnership(existingEvent);
        verify(eventValidator).validateEventNotInPast(updateEventDto.startDate());
        verify(eventValidator).validateEventDates(updateEventDto.startDate(), updateEventDto.endDate());
        verify(eventRepository).save(existingEvent);
    }

    @Test
    void update_ShouldThrowExceptionWhenEventNotFound() {
        UpdateEventDto updateEventDto = new UpdateEventDto("Title", "Desc",
                PLUS_ONE_DAY, PLUS_TWO_DAYS, EventType.WEBINAR, EventStatus.CANCELED);

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> eventService.update(EVENT_ID, updateEventDto));
    }

    @Test
    void update_ShouldThrowExceptionWhenUserNotOwner() {
        UpdateEventDto updateEventDto = new UpdateEventDto("Title", "Desc",
                PLUS_ONE_DAY, PLUS_TWO_DAYS, EventType.WEBINAR, EventStatus.CANCELED);
        Event existingEvent = createEvent(EVENT_ID, OWNER_1);

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(existingEvent));
        doThrow(new ForbiddenException("User is not owner of the event"))
                .when(eventValidator).validateEventOwnership(existingEvent);

        assertThrows(ForbiddenException.class,
                () -> eventService.update(EVENT_ID, updateEventDto));

        verify(eventRepository, never()).save(any());
    }

    @Test
    void getByFilters_ShouldApplyFilters() {
        final EventFilterDto filterDto = new EventFilterDto("test", null, CURRENT_USER_ID,
                null, null);

        Event eventFirstOwner = createEvent(CURRENT_USER_ID, OWNER_1);
        eventFirstOwner.setTitle("test event");
        Event eventSecondOwner = createEvent(OTHER_USER_ID, OWNER_1);
        eventSecondOwner.setTitle("other event");

        when(eventRepository.findAll()).thenReturn(List.of(eventFirstOwner, eventSecondOwner));

        List<EventDto> result = eventService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals("test event", result.get(0).title());
    }

    @Test
    void getByFilters_ShouldHandleAllNullFilters() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);

        when(eventRepository.findAll()).thenReturn(List.of(EVENT_1));

        List<EventDto> result = eventService.getByFilters(filterDto);

        assertEquals(1, result.size());
    }

    @Test
    void getByFilters_ShouldReturnFilteredEvents() {
        final EventFilterDto filterDto = new EventFilterDto("test",
                null,
                null,
                null,
                null);

        Event event1 = EVENT_1;
        event1.setTitle("test event");
        Event event2 = EVENT_2;
        event2.setTitle("another test");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<EventDto> result = eventService.getByFilters(filterDto);

        assertEquals(2, result.size());
    }

    @Test
    void getByFilters_ShouldHandleNullFilterDto() {
        assertThrows(NullPointerException.class, () -> eventService.getByFilters(null));
    }

    @Test
    void delete_ShouldDeleteEventWhenUserIsOwner() {
        Event event = EVENT_1;

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        eventService.delete(EVENT_ID);

        verify(eventValidator).validateEventOwnership(event);
        verify(eventRepository).delete(event);
    }

    @Test
    void delete_ShouldThrowExceptionWhenUserNotOwner() {
        Event event = EVENT_2;

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        doThrow(new ForbiddenException("User is not owner of the event"))
                .when(eventValidator).validateEventOwnership(event);

        assertThrows(ForbiddenException.class,
                () -> eventService.delete(EVENT_ID));
        verify(eventRepository, never()).delete(any());
    }

    @Test
    void delete_ShouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> eventService.delete(EVENT_ID));
        verify(eventRepository, never()).delete(any());
    }

    @Test
    void clearPassedEvents_WithPassedEvents() throws IllegalAccessException, NoSuchFieldException {
        List<Event> allEvents = Arrays.asList(pastEvent1, pastEvent2);

        when(eventRepository.findAll()).thenReturn(allEvents);

        Field batchSizeField = EventServiceImpl.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(eventService, 100);

        eventService.clearPassedEvents();
        timeout(10000);

        verify(eventRepository, times(1)).deleteAllById(anyCollection());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void clearPassedEvents_WithNoPassedEvents() throws IllegalAccessException, NoSuchFieldException {
        List<Event> allEvents = Arrays.asList(futureEvent1, futureEvent2);

        when(eventRepository.findAll()).thenReturn(allEvents);

        Field batchSizeField = EventServiceImpl.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(eventService, 100);

        eventService.clearPassedEvents();

        verify(eventRepository, never()).deleteAllById(anyCollection());
        verify(eventRepository, times(1)).findAll();
    }
}