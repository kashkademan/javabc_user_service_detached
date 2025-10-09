package school.faang.user_service.mapper;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

public interface GoalMapper {
    GoalDto toGoalDto(Goal goal);
}
