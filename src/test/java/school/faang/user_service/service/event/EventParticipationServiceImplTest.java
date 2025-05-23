package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceImplTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private EventParticipationServiceImpl eventParticipationService;

    private User user;
    private final long eventId = 10;
    private final long userId = 10;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(userId);
        user.setUsername("Ivan");
        user.setEmail("ivan@mail.ru");
    }

    @Test
    void testRegisterParticipant_whenNotRegistered_thenShouldRegisterSuccessfully() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());
        eventParticipationService.registerParticipant(eventId, userId);
        verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    void testRegisterParticipant_whenUserAlreadyRegistered_thenShouldThrowException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> {
                    eventParticipationService.registerParticipant(eventId, userId);
                });
        assertEquals("User is already registered", runtimeException.getMessage());
    }

    @Test
    void testUnregisterParticipant_whenUserIsRegistered_thenShouldUnregisterSuccessfully() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
        eventParticipationService.unregisterParticipant(eventId, userId);
        verify(eventParticipationRepository).unregister(eventId, userId);
    }

    @Test
    void testUnregisterParticipant_whenUserIsNotRegistered_thenShouldThrowException() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of());
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> {
                    eventParticipationService.unregisterParticipant(eventId, userId);
                });
        assertEquals("User is not registered", runtimeException.getMessage());
    }

    @Test
    void testGetParticipants_whenUsersExist_thenShouldReturnMappedUserDto() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("Anna");
        user1.setEmail("anna@mail.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("Dima");
        user2.setEmail("dima@gmail.com");

        List<User> users = List.of(user1, user2);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId))
                .thenReturn(users);
        List<UserDto> result = eventParticipationService.getParticipants(eventId);

        assertEquals(2, result.size());
        UserDto dto1 = result.get(0);
        UserDto dto2 = result.get(1);

        assertAll("First user",
                () -> assertEquals(user1.getId(), dto1.getId()),
                () -> assertEquals(user1.getUsername(), dto1.getUsername()),
                () -> assertEquals(user1.getEmail(), dto1.getEmail())
        );

        assertAll("Second user",
                () -> assertEquals(user2.getId(), dto2.getId()),
                () -> assertEquals(user2.getUsername(), dto2.getUsername()),
                () -> assertEquals(user2.getEmail(), dto2.getEmail())
        );
    }
}

