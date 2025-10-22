package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.EventValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.preparation.test.PreparationTest.CURRENT_USER_ID;
import static school.faang.user_service.preparation.test.PreparationTest.END_DATE;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_1;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_2;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_ID;
import static school.faang.user_service.preparation.test.PreparationTest.OTHER_USER_ID;
import static school.faang.user_service.preparation.test.PreparationTest.OWNER_1;
import static school.faang.user_service.preparation.test.PreparationTest.START_DATE;
import static school.faang.user_service.preparation.test.PreparationTest.createEvent;
import static school.faang.user_service.preparation.test.PreparationTest.createEventDto;


@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapperImpl eventMapper;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private UserContext userContext;

    @Mock
    private List<EventFilter> eventFilters;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @InjectMocks
    private EventServiceImpl eventService;


    @Test
    void create_ShouldValidDataWhenEventCreatedSuccessfully() {
        CreateEventDto createEventDto = createEventDto();

        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(eventValidator.validateAndGetUser(CURRENT_USER_ID)).thenReturn(OWNER_1);
        when(eventRepository.save(any(Event.class))).thenReturn(EVENT_1);

        EventDto result = eventService.create(createEventDto);

        assertNotNull(result);
        verify(eventValidator).validateEventNotInPast(START_DATE);
        verify(eventValidator).validateEventDates(START_DATE, END_DATE);
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
                START_DATE.plusDays(1), END_DATE.plusDays(1), EventType.WEBINAR, EventStatus.CANCELED);
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
                START_DATE, END_DATE, EventType.WEBINAR, EventStatus.CANCELED);

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> eventService.update(EVENT_ID, updateEventDto));
    }

    @Test
    void update_ShouldThrowExceptionWhenUserNotOwner() {
        UpdateEventDto updateEventDto = new UpdateEventDto("Title", "Desc",
                START_DATE, END_DATE, EventType.WEBINAR, EventStatus.CANCELED);
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
        EventFilterDto filterDto = new EventFilterDto("test", "desc", CURRENT_USER_ID,
                OTHER_USER_ID, EventType.WEBINAR);
        Event eventFirstOwner = createEvent(CURRENT_USER_ID, OWNER_1);
        Event eventSecondOwner = createEvent(OTHER_USER_ID, OWNER_1);

        EventFilter mockFilter = mock(EventFilter.class);
        when(mockFilter.isApplicable(filterDto)).thenReturn(true);
        when(mockFilter.apply(any(), eq(filterDto))).thenReturn(Stream.of(eventFirstOwner));

        when(eventRepository.findAll()).thenReturn(List.of(eventFirstOwner, eventSecondOwner));
        when(eventFilters.iterator()).thenReturn(List.of(mockFilter).iterator());

        List<EventDto> result = eventService.getByFilters(filterDto);

        assertEquals(1, result.size());
        verify(mockFilter).isApplicable(filterDto);
        verify(mockFilter).apply(any(), eq(filterDto));
    }

    @Test
    void getByFilters_ShouldHandleAllNullFilters() {
        EventFilterDto filterDto = new EventFilterDto(null, null, null, null, null);

        when(eventRepository.findAll()).thenReturn(List.of(EVENT_1));
        when(eventFilters.iterator()).thenReturn(List.<EventFilter>of().iterator());

        List<EventDto> result = eventService.getByFilters(filterDto);

        assertEquals(1, result.size());
    }

    @Test
    void getByFilters_ShouldReturnFilteredEvents() {
        EventFilterDto filterDto = new EventFilterDto("test",
                null,
                null,
                null,
                null);

        when(eventRepository.findAll()).thenReturn(List.of(EVENT_1, EVENT_2));
        when(eventFilters.iterator()).thenReturn(List.<EventFilter>of().iterator());

        List<EventDto> result = eventService.getByFilters(filterDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository).findAll();
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
}