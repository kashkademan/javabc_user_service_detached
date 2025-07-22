package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.user.UserDto;
import java.util.List;

/**
 * Интерфейс сервиса для управления менторскими отношениями между пользователями.
 * <p>
 * Предоставляет методы для:
 * <ul>
 *     <li>установления менторских связей,</li>
 *     <li>получения списка подопечных (менти) и менторов,</li>
 *     <li>удаления менторских отношений.</li>
 * </ul>
 * </p>
 *
 * @author fomchenkoandrey
 */
public interface MentorshipService {

    /**
     * Устанавливает менторскую связь между пользователями.
     *
     * @param mentorId ID пользователя, который становится ментором
     * @param menteeId ID пользователя, который становится подопечным (менти)
     * @throws school.faang.user_service.exception.DataValidationException если:
     *          <ul>
     *              <li>mentorId == menteeId (пользователь не может быть ментором сам себе),</li>
     *              <li>пользователь с mentorId не найден,</li>
     *              <li>пользователь с menteeId не найден,</li>
     *              <li>связь уже существует.</li>
     *          </ul>
     */
    void addMentorship(long mentorId, long menteeId);

    /**
     * Возвращает список подопечных (менти) указанного пользователя.
     *
     * @param userId ID пользователя (ментора)
     * @return список {@link UserDto} подопечных
     * @throws school.faang.user_service.exception.DataValidationException если пользователь не найден
     */
    List<UserDto> getMentees(long userId);

    /**
     * Возвращает список менторов указанного пользователя.
     *
     * @param userId ID пользователя (менти)
     * @return список {@link UserDto} менторов
     * @throws school.faang.user_service.exception.DataValidationException если пользователь не найден
     */
    List<UserDto> getMentors(long userId);

    /**
     * Удаляет менторскую связь между пользователями.
     *
     * @param menteeId ID подопечного (менти)
     * @param mentorId ID ментора
     * @throws school.faang.user_service.exception.DataValidationException если:
     *          <ul>
     *              <li>связь не существует,</li>
     *              <li>пользователь с mentorId не найден,</li>
     *              <li>пользователь с menteeId не найден.</li>
     *          </ul>
     */
    void deleteMentorship(long menteeId, long mentorId);
}
