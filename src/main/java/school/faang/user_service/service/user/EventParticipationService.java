package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponseDto;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

/**
 * Сервис для реализации функциональности участия в событиях
 * Предоставляет методы для регистрации, отписки, получения количества, списка участников события
 *
 * @author Mikhail Gevre
 */
public interface EventParticipationService {

    /**
     * Регистрирует пользователя на событие
     *
     * @param eventId ID события
     * @param userId ID пользователя
     */
    void registerParticipant(Long eventId, Long userId);

    /**
     * Отписывает пользователя от события
     *
     * @param eventId ID события
     * @param userId ID пользователя
     */

    void unregisteredParticipation(Long eventId, Long userId);

    /**
     * Получения количества участников события
     *
     * @param eventId ID события
     */
    CountResponseDto countParticipantsByEventId(Long eventId);

    /**
     * Получения списка участников события
     *
     * @param eventId ID события
     */
    List<UserDto> getAllParticipantsByEventId(Long eventId);
}
