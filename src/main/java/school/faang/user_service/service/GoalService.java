package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

public interface GoalService {
    GoalDto createGoal(Long userId, GoalDto goalDto);

    GoalDto updateGoal(Long goalId, GoalDto goalDto);

    GoalDto deleteGoal(long goalId);

    List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter);

    List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter);
}
