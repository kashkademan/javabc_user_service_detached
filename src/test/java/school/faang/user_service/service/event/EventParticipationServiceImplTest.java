package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventParticipationServiceImplTest {
    @Mock
    EventParticipationRepository eventParticipationRepository;
    @Spy
    UserMapperImpl userMapper;
    EventParticipationServiceImpl eventParticipationService;
    List<User> userList;
    List<UserDto> expectedDtoList;
    long eventId;

    @BeforeEach
    void setup() {
        eventId = 1L;

        userList = List.of(
                createTestUser(1L, "user1", "user1@example.com"),
                createTestUser(2L, "user2", "user2@example.com")
        );

        expectedDtoList = List.of(
                new UserDto(1L, "user1", "user1@example.com"),
                new UserDto(2L, "user2", "user2@example.com")
        );

        eventParticipationService =
                new EventParticipationServiceImpl(eventParticipationRepository, userMapper);
    }

    @Test
    @DisplayName("Регистрация пользователя, по eventId и userId. Пользователь в БД уже присутствует. Выбрасывание ошибки.")
    void testRegisterParticipant_ParticipantIsPresentInDB() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        assertThrows(DataValidationException.class, () -> eventParticipationService.registerParticipant(eventId, 1L));
    }

    @Test
    @DisplayName("Регистрация пользователя, по eventId и userId. Пользователь в БД отсутствует. Успешная регистрация.")
    void testRegisterParticipant_ParticipantNotPresentInDB() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        eventParticipationService.registerParticipant(eventId, 4L);

        verify(eventParticipationRepository, times(1)).register(eventId, 4L);
    }

    @Test
    @DisplayName("Разрегистрация пользователя, по eventId. Пользователь в БД отсутствует. Выбрасывание ошибки.")
    void testUnregisterParticipant_ParticipantNotPresentInDB() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        assertThrows(DataValidationException.class, () -> eventParticipationService.unregisterParticipant(eventId, 4L));
    }

    @Test
    @DisplayName("Разрегистрация пользователя, по eventId. Пользователь в БД присутствует.")
    void testUnregisterParticipant_ParticipantIsPresentInDB() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        eventParticipationService.unregisterParticipant(eventId, 1L);

        verify(eventParticipationRepository, times(1)).unregister(eventId, 1L);
    }

    @Test
    @DisplayName("Проверка на получение списка UserDto по eventId.")
    void testGetParticipant_WhenParticipantsExist_ShouldReturnListOfUserDtos() {
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(userList);

        List<UserDto> userDtoList = eventParticipationService.getParticipant(eventId);

        assertNotNull(userDtoList);
        assertEquals(2, userDtoList.size());
        assertEquals(expectedDtoList, userDtoList);
        verify(userMapper, times(userDtoList.size())).toUserDTO(any(User.class));
    }

    @Test
    @DisplayName("Проверка на пустой список участников")
    void testGetParticipant_WhenNoParticipants_ShouldReturnEmptyList() {
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());

        List<UserDto> userDtoList = eventParticipationService.getParticipant(eventId);

        assertNotNull(userDtoList);
        assertEquals(0, userDtoList.size());
    }

    @Test
    @DisplayName("Проверка на поиск верного кол-ва участников по eventId")
    void testGetParticipantsCount_ShouldReturnCorrectCount() {
        long expectedCount = 5L;
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn((int) expectedCount);

        long actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);
        verify(eventParticipationRepository).countParticipants(eventId);
    }

    @Test
    @DisplayName("Проверка на отсутствие участников в БД")
    void testGetParticipantsCount_WhenNoParticipants() {
        when(eventParticipationRepository.countParticipants(5L)).thenReturn(0);

        long actualCount = eventParticipationService.getParticipantsCount(5L);

        assertEquals(0, actualCount);
        verify(eventParticipationRepository).countParticipants(5L);
    }

    private User createTestUser(Long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .password("password")
                .active(true)
                .build();
    }
}