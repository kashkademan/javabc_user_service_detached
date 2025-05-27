package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    private final static long USER_ID = 1L;
    private final static long FIRST_EVENT_ID = 2L;
    private final static long SECOND_EVENT_ID = 3L;

    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;

    @Test
    void testCreate_whenValid_thenReturnCreatedEvent() {
        String title = "Event title";
        EventDto eventDto = createEventDto(null, USER_ID, title);
        EventDto returnedEventDto = createEventDto(FIRST_EVENT_ID, USER_ID, title);
        when(eventService.create(eventDto)).thenReturn(returnedEventDto);

        EventDto createdEventDto = eventController.create(eventDto);

        verify(eventService, times(1)).create(eventDto);
        assertNotNull(createdEventDto);
        assertEquals(FIRST_EVENT_ID, createdEventDto.getId());
        assertEquals(USER_ID, createdEventDto.getOwnerId());
        assertEquals(title, createdEventDto.getTitle());
    }

    @Test
    void testCreate_whenEmptyTitle_thenThrowException() {
        EventDto eventDto = createEventDto(null, USER_ID, null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

        assertEquals("Title is required.", exception.getMessage());
    }

    @Test
    void testCreate_withEmptyOwner_thenThrowException() {
        String title = "Event title";
        EventDto eventDto = createEventDto(null, null, title);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

        assertEquals("Owner is required.", exception.getMessage());
    }

    @Test
    void testCreate_whenEmptyStartDate_thenThrowException() {
        String title = "Event title";
        EventDto eventDto = createEventDto(null, USER_ID, title, null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

        assertEquals("Start date is required.", exception.getMessage());
    }

    @Test
    void testGetEvent_whenEventExists_thenReturnEvent() {
        String title = "Event title";
        EventDto eventDto = createEventDto(FIRST_EVENT_ID, USER_ID, title);
        when(eventService.getEvent(FIRST_EVENT_ID)).thenReturn(eventDto);

        EventDto foundEventDto = eventController.getEvent(FIRST_EVENT_ID);

        assertNotNull(foundEventDto);
        assertEquals(FIRST_EVENT_ID, foundEventDto.getId());
        assertEquals(USER_ID, foundEventDto.getOwnerId());
        assertEquals(title, foundEventDto.getTitle());
    }

    @Test
    void testGetEventsByFilter_whenAllPassed_thenReturnList() {
        String title = "Event title";
        EventDto firstEventDto = createEventDto(FIRST_EVENT_ID, USER_ID, title);
        EventDto secondEventDto = createEventDto(SECOND_EVENT_ID, USER_ID, title);
        EventFilterDto filter = EventFilterDto.builder().build();
        when(eventService.getEventsByFilter(filter)).thenReturn(List.of(firstEventDto, secondEventDto));

        List<EventDto> events = eventController.getEventsByFilter(filter);

        assertNotNull(events);
        assertEquals(2, events.size());
        assertTrue(events.contains(firstEventDto));
        assertTrue(events.contains(secondEventDto));
    }

    @Test
    void testDeleteEvent() {
        assertDoesNotThrow(() -> eventController.deleteEvent(FIRST_EVENT_ID));
        verify(eventService, times(1)).deleteEvent(FIRST_EVENT_ID);
    }

    @Test
    void testUpdateEvent_whenValid_thenReturnEvent() {
        String title = "Event title";
        EventDto eventDto = createEventDto(FIRST_EVENT_ID, USER_ID, title);
        when(eventService.updateEvent(eventDto)).thenReturn(eventDto);

        EventDto updatedEventDto = eventController.updateEvent(eventDto);

        verify(eventService, times(1)).updateEvent(eventDto);
        assertNotNull(updatedEventDto);
        assertEquals(FIRST_EVENT_ID, updatedEventDto.getId());
        assertEquals(USER_ID, updatedEventDto.getOwnerId());
        assertEquals(title, updatedEventDto.getTitle());
    }

    @Test
    void testGetOwnedEvents_whenExists_thenReturnList() {
        String title = "Event title";
        EventDto firstEventDto = createEventDto(FIRST_EVENT_ID, USER_ID, title);
        EventDto secondEventDto = createEventDto(SECOND_EVENT_ID, USER_ID, title);
        when(eventService.getOwnedEvents(USER_ID)).thenReturn(List.of(firstEventDto, secondEventDto));

        List<EventDto> events = eventController.getOwnedEvents(USER_ID);

        assertNotNull(events);
        assertEquals(2, events.size());
        assertTrue(events.contains(firstEventDto));
        assertTrue(events.contains(secondEventDto));
    }

    @Test
    void testGetParticipatedEvents_whenExists_thenReturnList() {
        String title = "Event title";
        EventDto firstEventDto = createEventDto(FIRST_EVENT_ID, USER_ID, title);
        EventDto secondEventDto = createEventDto(SECOND_EVENT_ID, USER_ID, title);
        when(eventService.getParticipatedEvents(USER_ID)).thenReturn(List.of(firstEventDto, secondEventDto));

        List<EventDto> events = eventController.getParticipatedEvents(USER_ID);

        assertNotNull(events);
        assertEquals(2, events.size());
        assertTrue(events.contains(firstEventDto));
        assertTrue(events.contains(secondEventDto));
    }

    private EventDto createEventDto(Long id, Long ownerId, String title) {
        return createEventDto(id, ownerId, title, LocalDateTime.now());
    }

    private EventDto createEventDto(Long id, Long ownerId, String title, LocalDateTime startDate) {
        return EventDto.builder().id(id).title(title).ownerId(ownerId).startDate(startDate).build();
    }
}