package school.faang.user_service.service.event;

import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

/**
 * Сервис для управления участием пользователей в событиях.
 * <p>
 * Предоставляет методы для регистрации и отмены регистрации пользователя на событие,
 * а также получения информации об участниках событий.
 * </p>
 *
 * <ul>
 *   <li>Регистрация пользователя на событие</li>
 *   <li>Отмена регистрации пользователя</li>
 *   <li>Получение количества участников события</li>
 *   <li>Получение списка участников события</li>
 * </ul>
 *
 * <p>В случае нарушения бизнес-правил (например, повторная регистрация или отмена
 * регистрации несуществующего участника) выбрасываются соответствующие исключения.</p>
 *
 * @author JekaCAP
 * @since 2025-07-05
 */
public interface EventParticipationService {

    /**
     * Регистрирует пользователя на событие.
     * <p>
     * Пользователь не может зарегистрироваться повторно на одно и то же событие.
     * Если событие не найдено, выбрасывается {@link school.faang.user_service.exception.DataValidationException}.
     * Если пользователь уже зарегистрирован,
     * выбрасывается {@link school.faang.user_service.exception.ForbiddenException}.
     * </p>
     *
     * @param eventId идентификатор события
     * @param userId  идентификатор пользователя
     */
    void registerParticipant(long eventId, long userId);

    /**
     * Отменяет регистрацию пользователя на событие.
     * <p>
     * Пользователь не может отменить регистрацию, если он не был зарегистрирован на событие.
     * Если событие не найдено, выбрасывается {@link school.faang.user_service.exception.DataValidationException}.
     * Если пользователь не зарегистрирован на событие,
     * выбрасывается {@link school.faang.user_service.exception.ForbiddenException}.
     * </p>
     *
     * @param eventId идентификатор события
     * @param userId  идентификатор пользователя
     */
    void unregisterParticipant(long eventId, long userId);

    /**
     * Возвращает количество участников, зарегистрированных на событие.
     * <p>
     * Если событие не найдено, выбрасывается {@link school.faang.user_service.exception.DataValidationException}.
     * </p>
     *
     * @param eventId идентификатор события
     * @return объект {@link CountResponse} с количеством участников
     */
    CountResponse countParticipantsByEventId(long eventId);

    /**
     * Возвращает список всех участников события.
     * <p>
     * Если событие не найдено, выбрасывается {@link school.faang.user_service.exception.DataValidationException}.
     * Возвращаемый список может быть пустым, если участников нет.
     * </p>
     *
     * @param eventId идентификатор события
     * @return список {@link UserDto} участников события
     */
    List<UserDto> getAllParticipantsByEventId(long eventId);
}
