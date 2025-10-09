package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    EventParticipationRepository eventParticipationRepository;

    @Mock
    EventRepository eventRepository;

    @Spy
    UserMapper userMapper;

    @Mock
    UserContext userContext;

    @InjectMocks
    EventParticipationServiceImpl eventParticipationService;

    @Test
    public void testParticipantRegistered() {
        long eventId = 4L;
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        Event event = new Event();
        event.setId(eventId);
        List<Event> events = new ArrayList<>();
        events.add(event);

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);
        long finalUserId = userId;
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.registerParticipant(eventId, finalUserId),
                "User has already registered for the event");

        userId = 2L;
        eventParticipationService.registerParticipant(eventId, userId);
        verify(eventParticipationRepository).register(anyLong(), anyLong());
        verify(eventParticipationRepository).register(eq(eventId), eq(userId));
        verify(eventParticipationRepository, times(1)).register(eq(eventId), eq(userId));
    }

    @Test
    public void testParticipantUnregistered() {
        long eventId = 1L;
        long userId = 1L;
        long currentUserId = 123L;
        when(userContext.getUserId()).thenReturn(currentUserId);
        assertThrows(ForbiddenException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId),
                "Can't delete someone else from the event");
        when(userContext.getUserId()).thenReturn(userId);

        assertThrows(DataValidationException.class,
                () -> eventParticipationService.unregisterParticipant(eventId, userId),
                "User hasn't registered for the event");
        Event event = new Event();
        event.setId(eventId);
        List<Event> events = new ArrayList<>();
        events.add(event);
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);
        eventParticipationService.unregisterParticipant(eventId, userId);
        verify(eventParticipationRepository).unregister(eq(eventId), eq(userId));
        verify(eventParticipationRepository, times(1)).unregister(eq(eventId), eq(userId));
    }

    @Test
    public void testParticipantsCountGet() {
        int expectedCount = 10;
        CountResponse expectedResponse = new CountResponse(expectedCount);
        long eventId = 1L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);
        CountResponse actualResponse = eventParticipationService.countParticipantsByEventId(eventId);
        assertEquals(expectedResponse, actualResponse);
        verify(eventParticipationRepository).countParticipants(eq(eventId));
        verify(eventParticipationRepository, times(1)).countParticipants(eq(eventId));
    }

    @Test
    public void testAllParticipantsByEventIdGet() {
        long eventId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        List<User> users = new ArrayList<>();
        users.add(user);

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);

        List<UserDto> actualResult = eventParticipationService.getAllParticipantsByEventId(eventId);
        assertEquals(users.stream().map(userMapper::toUserDto).toList(), actualResult);
        verify(eventParticipationRepository).findAllParticipantsByEventId(eq(eventId));
        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eq(eventId));
    }
}
