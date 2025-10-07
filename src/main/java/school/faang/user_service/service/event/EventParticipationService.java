package school.faang.user_service.service.event;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.EventParticipationDto;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

/**
 * Сервис для регистрации на событие и для отмены этой регистрации
 */

public interface EventParticipationService {
    /**
     * Метод регистрирует участников на событие
     */
    EventParticipationDto registerParticipant(long eventId, long userId);

    /**
     * Метод отменяет участие в событии
     */
    void unregisterParticipant(long eventId, long userId);

    /**
     * @return количество участников
     */
    CountResponse countParticipantsByEventId(long eventId);

    /**
     * @return список участников
     */
    List<UserDto> getAllParticipantsByEventId(long eventId);
}
