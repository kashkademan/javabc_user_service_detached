package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventParticipationServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тесты участия в эвентах")
class EventParticipationServiceImplTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EventParticipationServiceImpl service;

    private final long eventId = 1L;
    private final long userId = 2L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Успешная регистрация участника")
    void registerParticipant_success() {
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);

        service.registerParticipant(eventId, userId);

        verify(eventParticipationRepository).register(eventId, userId);
    }

    @Test
    @DisplayName("Падает при регистрации, если событие не найдено")
    void registerParticipant_eventNotFound_throws() {
        when(eventRepository.existsById(eventId)).thenReturn(false);

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> service.registerParticipant(eventId, userId));
        assertTrue(ex.getMessage().contains("Event not found"));
    }

    @Test
    @DisplayName("Падает при отмене регистрации, если участник не зарегистрирован")
    void unregisterParticipant_notRegistered_throws() {
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> service.unregisterParticipant(eventId, userId));
        assertTrue(ex.getMessage().contains("already unregistered"));
    }

    @Test
    @DisplayName("Возвращает количество участников")
    void countParticipants_returnsCount() {
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventParticipationRepository.countParticipants(eventId)).thenReturn(5);

        CountResponse response = service.countParticipantsByEventId(eventId);

        assertEquals(5, response.getCount());
    }

    @Test
    @DisplayName("Возвращает список участников с данными UserDto")
    void getAllParticipants_returnsUserDtoList() {
        when(eventRepository.existsById(eventId)).thenReturn(true);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user1, user2));

        UserDto dto1 = new UserDto(1L, "user1", "user1@example.com", "+1234567890", "About user 1");
        UserDto dto2 = new UserDto(2L, "user2", "user2@example.com", "+0987654321", "About user 2");

        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(userMapper.toUserDto(user2)).thenReturn(dto2);

        List<UserDto> dtos = service.getAllParticipantsByEventId(eventId);

        assertEquals(2, dtos.size());
        assertTrue(dtos.contains(dto1));
        assertTrue(dtos.contains(dto2));
    }
}