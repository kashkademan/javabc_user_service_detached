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
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventController eventController;

    @Test
    void testCreateSuccess() {
        long eventId = 1L;
        long userId = 2L;
        String title = "Event title";
        EventDto eventDto = createEventDto(null, userId, title);
        EventDto returnedEventDto = createEventDto(eventId, userId, title);
        when(eventService.create(eventDto)).thenReturn(returnedEventDto);

        EventDto createdEventDto = eventController.create(eventDto);
        verify(eventService, times(1)).create(eventDto);

        assertNotNull(createdEventDto);
        assertEquals(eventId, createdEventDto.getId());
        assertEquals(userId, createdEventDto.getOwnerId());
        assertEquals(title, createdEventDto.getTitle());
    }

    @Test
    void testCreateWithTitleValidationError() {
        long userId = 2L;
        EventDto eventDto = createEventDto(null, userId, null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

        assertEquals("Title is required.", exception.getMessage());
    }

    @Test
    void testCreateWithOwnerValidationError() {
        String title = "Event title";
        EventDto eventDto = createEventDto(null, null, title);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

        assertEquals("Owner is required.", exception.getMessage());
    }

    @Test
    void testCreateWithStartDateValidationError() {
        long userId = 2L;
        String title = "Event title";
        EventDto eventDto = createEventDto(null, userId, title, null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventController.create(eventDto));

        assertEquals("Start date is required.", exception.getMessage());
    }

    @Test
    void testGetEvent() {
        long eventId = 1L;
        long userId = 2L;
        String title = "Event title";
        EventDto eventDto = createEventDto(eventId, userId, title);
        when(eventService.getEvent(eventId)).thenReturn(eventDto);

        EventDto foundEventDto = eventController.getEvent(eventId);

        assertNotNull(foundEventDto);
        assertEquals(eventId, foundEventDto.getId());
        assertEquals(userId, foundEventDto.getOwnerId());
        assertEquals(title, foundEventDto.getTitle());
    }

    @Test
    void testGetEventsByFilter() {
        long firstEventId = 1L;
        long secondEventId = 2L;
        long userId = 3L;
        String title = "Event title";
        EventDto firstEventDto = createEventDto(firstEventId, userId, title);
        EventDto secondEventDto = createEventDto(secondEventId, userId, title);
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
        long eventId = 1L;

        assertDoesNotThrow(() -> eventController.deleteEvent(eventId));
        verify(eventService, times(1)).deleteEvent(eventId);
    }

    @Test
    void testUpdateEvent() {
        long eventId = 1L;
        long userId = 2L;
        String title = "Event title";
        EventDto eventDto = createEventDto(eventId, userId, title);
        when(eventService.updateEvent(eventDto)).thenReturn(eventDto);

        EventDto updatedEventDto = eventController.updateEvent(eventDto);
        verify(eventService, times(1)).updateEvent(eventDto);

        assertNotNull(updatedEventDto);
        assertEquals(eventId, updatedEventDto.getId());
        assertEquals(userId, updatedEventDto.getOwnerId());
        assertEquals(title, updatedEventDto.getTitle());
    }

    @Test
    void testGetOwnedEvents() {
        long firstEventId = 1L;
        long secondEventId = 2L;
        long userId = 3L;
        String title = "Event title";
        EventDto firstEventDto = createEventDto(firstEventId, userId, title);
        EventDto secondEventDto = createEventDto(secondEventId, userId, title);
        when(eventService.getOwnedEvents(userId)).thenReturn(List.of(firstEventDto, secondEventDto));

        List<EventDto> events = eventController.getOwnedEvents(userId);

        assertNotNull(events);
        assertEquals(2, events.size());
        assertTrue(events.contains(firstEventDto));
        assertTrue(events.contains(secondEventDto));
    }

    @Test
    void testGetParticipatedEvents() {
        long firstEventId = 1L;
        long secondEventId = 2L;
        long userId = 3L;
        String title = "Event title";
        EventDto firstEventDto = createEventDto(firstEventId, userId, title);
        EventDto secondEventDto = createEventDto(secondEventId, userId, title);
        when(eventService.getParticipatedEvents(userId)).thenReturn(List.of(firstEventDto, secondEventDto));

        List<EventDto> events = eventController.getParticipatedEvents(userId);

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