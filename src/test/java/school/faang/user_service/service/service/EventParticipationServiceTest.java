package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        List<User> participants = createParticipantsList();

        when(userService.existsById(userId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(participants);

        eventParticipationService.registerParticipant(eventId, userId);

        verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    void testRegisterParticipant_InvalidParameters() {
        long eventId = 0L;
        long userId = -1L;

        assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId));
    }

    @Test
    void testRegisterParticipant_UserNotFound() {
        long eventId = 1L;
        long userId = 333L;

        when(userService.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId)
        );
    }

    @Test
    void testRegisterParticipant_UserAlreadyRegistered() {
        long eventId = 1L;
        long userId = 44L;
        User user = createMockUser(userId);
        List<User> participants = createParticipantsList(user);

        when(userService.existsById(userId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(participants);

        assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.registerParticipant(eventId, userId)
        );
    }

    @Test
    void testUnregisterParticipant() {
        long eventId = 1L;
        long userId = 2L;
        User user = createMockUser(userId);
        List<User> participants = createParticipantsList(user);

        when(userService.existsById(userId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    void testUnregisterParticipant_InvalidParameters() {
        long eventId = -1L;
        long userId = 0L;

        assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId));
    }

    @Test
    void testUnregisterParticipant_UserNotFound() {
        long eventId = 1L;
        long userId = 51L;

        when(userService.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId)
        );
    }

    @Test
    void testUnregisterParticipant_AlreadyUnregistered() {
        long eventId = 1L;
        long userId = 38L;
        List<User> participants = createParticipantsList();

        when(userService.existsById(userId)).thenReturn(true);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.unregisterParticipant(eventId, userId)
        );
    }

    @Test
    void testGetParticipants() {
        long eventId = 11L;

        User user1 = createMockUser(1L, "John", "john1@email.com");
        User user2 = createMockUser(2L, "Steve", "steve2@email.com");
        List<User> participants = createParticipantsList(user1, user2);

        UserDto dto1 = new UserDto(1L, "John", "john1@email.com");
        UserDto dto2 = new UserDto(2L, "Steve", "steve2@email.com");

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(participants);

        List<UserDto> actualParticipants = eventParticipationService.getParticipants(eventId);

        assertEquals(List.of(dto1, dto2), actualParticipants);
    }

    @Test
    void testGetParticipants_EmptyList() {
        long eventId = 3L;

        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(Collections.emptyList());

        eventParticipationService.getParticipants(eventId);

        assertTrue(eventParticipationService.getParticipants(eventId).isEmpty(),
                "Expected empty list of participants");
    }

    @Test
    void testGetParticipants_InvalidEventId() {
        long eventId = -1L;

        assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.getParticipants(eventId));
    }

    @Test
    void testGetParticipantsCount() {
        long eventId = 1L;
        int expectedCount = 5;

        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);
        verify(eventParticipationRepository).countParticipants(eventId);
    }

    @Test
    void testGetParticipantsCount_InvalidEventId() {
        long eventId = -1L;

        assertThrows(IllegalArgumentException.class, () ->
                eventParticipationService.getParticipantsCount(eventId));
    }

    @Test
    void testGetParticipantsCount_EventNotFound() {
        long eventId = 88L;
        int countParticipants = 1;

        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(countParticipants);

        int result = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(countParticipants, result);
    }

    private List<User> createParticipantsList(User... users) {
        return new ArrayList<>(Arrays.stream(users).toList());
    }

    private User createMockUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }

    private User createMockUser(Long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }
}
