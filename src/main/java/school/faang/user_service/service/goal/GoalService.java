package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;

import java.util.List;

/**
 * Сервисный интерфейс для управления целями пользователя.
 * <p>
 * Определяет контракт для создания, обновления, получения, удаления и поиска целей по фильтрам.
 * </p>
 *
 * @author Myrzakhmet
 * @since 08.07.2025
 */
public interface GoalService {
    /**
     * Создает новую цель.
     *
     * @param goalCreateDto данные для создания цели
     * @return созданная цель в виде {@link GoalDto}
     */
    GoalDto create(GoalCreateDto goalCreateDto);

    /**
     * Обновляет существующую цель по её идентификатору.
     *
     * @param goalId        идентификатор цели
     * @param goalUpdateDto данные для обновления цели
     * @return обновлённая цель в виде {@link GoalDto}
     */
    GoalDto update(long goalId, GoalUpdateDto goalUpdateDto);

    /**
     * Получает цель по её идентификатору.
     *
     * @param goalId идентификатор цели
     * @return найденная цель в виде {@link GoalDto}
     */
    GoalDto getById(long goalId);

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param goalId идентификатор цели
     */
    void delete(long goalId);

    /**
     * Возвращает список целей, соответствующих указанным фильтрам.
     *
     * @param filters параметры фильтрации
     * @return список целей в виде List<{@link GoalDto}>
     */
    List<GoalDto> getByFilters(GoalFilterDto filters);
}
