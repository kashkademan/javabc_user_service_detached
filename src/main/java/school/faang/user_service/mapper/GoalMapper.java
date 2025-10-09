package school.faang.user_service.mapper;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

public interface GoalMapper {
    GoalDto toGoalDto(Goal goal);
}
