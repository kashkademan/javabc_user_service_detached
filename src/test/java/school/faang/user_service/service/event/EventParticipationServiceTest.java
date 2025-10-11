package school.faang.user_service.service.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {
    @Mock
    EventRepository eventRepository;
    @Mock
    EventParticipationRepository eventParticipationRepository;
    @Mock
    UserContext userContext;
    @Spy
    UserMapper userMapper;
    @InjectMocks
    EventParticipationServiceImpl eventParticipationService;

    Event event;
    User user;
    List<Event> events = new ArrayList<>();
    List<User> users = new ArrayList<>();
    int expectedCount;
    CountResponse countResponse;

    @BeforeEach
    void prepareData() {
        userContext.setUserId(123L);
        event = Event.builder()
                .id(4L)
                .build();
        user = User.builder()
                .id(1L)
                .build();

        events.add(event);
        users.add(user);
        expectedCount = 10;
        countResponse = new CountResponse(expectedCount);
    }

    @Test
    void testSuccessfullyParticipantRegistered() {
        when(eventRepository.findParticipatedEventsByUserId(user.getId())).thenReturn(new ArrayList<>());
        eventParticipationService.registerParticipant(event.getId(), user.getId());
        verify(eventParticipationRepository, times(1)).register(eq(event.getId()), eq(user.getId()));
    }

    @Test
    void testDataValidationExceptionBeforeParticipantRegistered() {
        when(eventRepository.findParticipatedEventsByUserId(user.getId())).thenReturn(events);
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.registerParticipant(event.getId(), user.getId()),
                "User has already registered for the event");
    }

    @Test
    void testSuccessfullyParticipantUnregistered() {
        when(userContext.getUserId()).thenReturn(user.getId());
        when(eventRepository.findParticipatedEventsByUserId(user.getId())).thenReturn(events);
        eventParticipationService.unregisterParticipant(event.getId(), user.getId());
        verify(eventParticipationRepository, times(1)).unregister(eq(event.getId()), eq(user.getId()));
    }

    @Test
    void testForbiddenExceptionWhileParticipantUnregistered() {
        assertThrows(ForbiddenException.class,
                () -> eventParticipationService.unregisterParticipant(event.getId(), user.getId()),
                "Can't delete someone else'e registration for the event");
    }

    @Test
    void testDataValidationExceptionWhileParticipantUnregistered() {
        when(userContext.getUserId()).thenReturn(user.getId());
        when(eventRepository.findParticipatedEventsByUserId(user.getId())).thenReturn(new ArrayList<>());
        assertThrows(DataValidationException.class,
                () -> eventParticipationService.unregisterParticipant(event.getId(), user.getId()),
                "User hasn't registered for the event");
    }

    @Test
    void testParticipantsCountGet() {
        when(eventParticipationRepository.countParticipants(event.getId())).thenReturn(expectedCount);
        CountResponse actualResult = eventParticipationService.countParticipantsByEventId(event.getId());
        assertEquals(countResponse, actualResult);
    }

    @Test
    void testEventsAllParticipantsGet() {
        when(eventParticipationRepository.findAllParticipantsByEventId(event.getId())).thenReturn(users);
        List<UserDto> actualResult = eventParticipationService.getAllParticipantsByEventId(event.getId());
        assertEquals(users.stream().map(userMapper::toUserDto).toList(), actualResult);
    }
}
