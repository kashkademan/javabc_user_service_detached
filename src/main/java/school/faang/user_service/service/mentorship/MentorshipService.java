package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.user.UserDto;

import java.util.List;

/**
 * Сервис для управления менторскими связями между пользователями.
 * Предоставляет методы для добавления, удаления и получения менторских отношений.
 *
 * @author Маляров Максим
 */
public interface MentorshipService {

    /**
     * Добавляет менторскую связь между пользователями.
     *
     * @param mentorId ID ментора
     * @param menteeId ID менти
     */
    void addMentorship(Long mentorId, Long menteeId);

    /**
     * Получает список всех менти для указанного пользователя.
     *
     * @param userId ID пользователя
     * @return список менти в виде DTO
     */
    List<UserDto> getMentees(Long userId);

    /**
     * Получает список всех менторов для указанного пользователя.
     *
     * @param userId ID пользователя
     * @return список менторов в виде DTO
     */
    List<UserDto> getMentors(Long userId);

    /**
     * Удаляет менторскую связь между пользователями.
     *
     * @param mentorId ID ментора
     * @param menteeId ID менти
     */
    void deleteMentorship(Long mentorId, Long menteeId);
}
