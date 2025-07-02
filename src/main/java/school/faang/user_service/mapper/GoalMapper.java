package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target="parent", ignore = true)
    @Mapping(target="mentor", ignore = true)
    @Mapping(target="users", ignore = true)
    Goal toGoal(CreateGoalDto createGoalDto);

    GoalDto toGoalDto(Goal goal);
}
