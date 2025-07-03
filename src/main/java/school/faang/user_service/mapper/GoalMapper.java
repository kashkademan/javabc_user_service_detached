package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    Goal toGoal(CreateGoalDto createGoalDto);

    @Mapping(target = "userIds", expression = "java(getUserIds(goal.getUsers()))")
    GoalDto toGoalDto(Goal goal);

    void update(UpdateGoalDto goalDto, @MappingTarget Goal entity);

    default List<Long> getUserIds(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }
}
