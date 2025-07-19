package school.faang.user_service.service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    void testRegisterParticipant() {
        long eventId = 1L;
        long userId = 22L;

        Mockito.when(userService.existsById(userId)).thenReturn(true);

        List<User> participants = createParticipantsList();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(participants);

        eventParticipationService.registerParticipant(eventId, userId);

        Mockito.verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    void testRegisterParticipant_InvalidParameters() {
        long eventId = 0L;
        long userId = -1L;

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId));
    }

    @Test
    void testRegisterParticipant_UserNotFound() {
        long eventId = 1L;
        long userId = 333L;

        Mockito.when(userService.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId)
        );
    }

    @Test
    void testRegisterParticipant_UserAlreadyRegistered() {
        long eventId = 1L;
        long userId = 44L;

        Mockito.when(userService.existsById(userId)).thenReturn(true);

        User mockUser = new User();
        mockUser.setId(userId);
        List<User> participants = createParticipantsList(mockUser);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(participants);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId)
        );
    }

    @Test
    void testUnregisterParticipant() {
        long eventId = 1L;
        long userId = 2L;

        Mockito.when(userService.existsById(userId)).thenReturn(true);

        User mockUser = new User();
        mockUser.setId(userId);
        List<User> participants = createParticipantsList(mockUser);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        eventParticipationService.unregisterParticipant(eventId, userId);

        Mockito.verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    void testUnregisterParticipant_InvalidParameters() {
        long eventId = -1L;
        long userId = 0L;

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId));
    }

    @Test
    void testUnregisterParticipant_UserNotFound() {
        long eventId = 1L;
        long userId = 51L;

        Mockito.when(userService.existsById(userId)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId)
        );
    }

    @Test
    void testUnregisterParticipant_AlreadyUnregistered() {
        long eventId = 1L;
        long userId = 38L;

        Mockito.when(userService.existsById(userId)).thenReturn(true);

        List<User> participants = createParticipantsList();
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId)
        );
    }

    @Test
    void testGetParticipants() {
        long eventId = 11L;

        Mockito.when(eventParticipationService.existsById(eventId)).thenReturn(true);

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("John");
        user1.setEmail("john1@email.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Steve");
        user2.setEmail("steve2@email.com");
        List<User> participants = createParticipantsList(user1, user2);

        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);


        UserDto dto1 = new UserDto(1L, "John", "john1@email.com");
        UserDto dto2 = new UserDto(2L, "Steve", "steve2@email.com");

        Mockito.when(userMapper.toDto(user1)).thenReturn(dto1);
        Mockito.when(userMapper.toDto(user2)).thenReturn(dto2);

        List<UserDto> actualParticipants = eventParticipationService.getParticipants(eventId);

        Assertions.assertEquals(List.of(dto1, dto2), actualParticipants);
    }

    @Test
    void testGetParticipants_EmptyList() {
        long eventId = 3L;

        Mockito.when(eventParticipationService.existsById(eventId)).thenReturn(true);
        Mockito.when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(Collections.emptyList());

        eventParticipationService.getParticipants(eventId);

        Assertions.assertTrue(eventParticipationService.getParticipants(eventId).isEmpty(),
                "Expected empty list of participants");
    }

    @Test
    void testGetParticipants_InvalidEventId() {
        long eventId = -1L;

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.getParticipants(eventId));
    }

    @Test
    void testGetParticipantsCount() {
        long eventId = 1L;
        int expectedCount = 5;

        Mockito.when(eventParticipationService.existsById(eventId)).thenReturn(true);
        Mockito.when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);

        Assertions.assertEquals(expectedCount, actualCount);
        Mockito.verify(eventParticipationRepository).countParticipants(eventId);
    }

    @Test
    void testGetParticipantsCount_InvalidEventId() {
        long eventId = -1L;

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.getParticipantsCount(eventId));
    }

    @Test
    void testGetParticipantsCount_EventNotFound() {
        long eventId = 88L;

        Mockito.when(eventParticipationService.existsById(eventId)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () ->
                eventParticipationService.getParticipantsCount(eventId));
    }

    private List<User> createParticipantsList(User... users) {
        List<User> list = new ArrayList<>();
        for (User user : users) {
            list.add(user);
        }
        return list;
    }
}
