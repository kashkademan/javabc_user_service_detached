package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;

import java.util.List;

/**
 * Сервис для управления целями пользователя.
 * Предоставляет методы для создания, обновления, удаления и получения информации о целях.
 */
public interface GoalService {
    GoalDto create(CreateGoalDto createGoalDto);

    void delete(long goalId);

    GoalDto update(long goalId, UpdateGoalDto updateGoalDto);

    List<GoalDto> getByFilters(GoalFilterDto filters);
}