package school.faang.user_service.service.mentorship;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.dto.mentorship.MentorshipDto;

import java.util.List;

/**
 * Сервис для управления менторскими отношениями между пользователями
 * <p>
 * Имеет методы для добавления менторских связей, их прекращение,
 * а так же поиск актуальных менторов у пользователей и пользователей у определенного ментора.
 * </p>
 * Менторская связь представляет собой двустороннюю связь где:
 * <p>
 * - пользователь может быть менти у нескольких менторов
 * </p>
 * - пользователь может быть ментором у нескольких менти
 */
@Service
public interface MentorshipService {
    /**
     * <p>
     * Метод устанавливает менторскую связь между двумя пользователями
     * </p>
     * Пользователь с {@code mentorId} становится ментором, а пользователь с {@code menteeId} менти.
     *
     * @param mentorId идентификатор пользователя, который становится ментором, должен быть положительным
     * @param menteeId идентификатор пользователя, который становится менти, должен быть положительным
     * @throws DataValidationException если {@code mentorId} равен {@code menteeId}
     * @throws ForbiddenException      если {@code currentUserId} не равен {@code menteeId} или {@code mentorId}
     */
    MentorshipDto addMentorship(long mentorId, long menteeId);

    /**
     * <p>
     * Возвращает менти у определенного ментора
     * </p>
     * Возвращает список пользователей, которые являются менти у данного пользователя.
     * Если у пользователя нет менти, возвращается пустой список.
     * <p>
     *
     * @param userId идентификатор пользователя, должен быть положительным
     * @return - список DTO менти, может быть пустым, но не null
     */

    List<UserDto> getMentees(long userId);

    /**
     * <p>
     * Возвращает менторов у определенного пользователя
     * </p>
     * Возвращает список пользователей, которые являются менторами у данного пользователя.
     * Если у пользователя нет менторов, возвращает пустой список.
     * <p>
     *
     * @param userId идентификатор пользователя, должен быть положительным
     * @return - список DTO менторов, может быть пустым, но не null
     */

    List<UserDto> getMentors(long userId);

    /**
     * <p>
     * Удаляет менторский отношения между пользователями
     * </p>
     * <p>
     * Пользователи с идентификаторами {@code menteeId} и {@code mentorId} разрывают менторские связи,
     * установленные ранее. Если указанные пользователи не состояли в менторских связях,
     * то метод завершится без ошибок.
     * </p>
     *
     * @param menteeId идентификатор пользователя, являющимся менти, должен быть положительным.
     * @param mentorId идентификатор пользователя, являющимся ментором, должен быть положительным.
     * @throws ForbiddenException      если {@code currentUserId} не равен {@code menteeId} или {@code mentorId}
     * @throws DataValidationException если {@code mentorId} равен {@code menteeId}
     */

    void deleteMentorship(long menteeId, long mentorId);
}
