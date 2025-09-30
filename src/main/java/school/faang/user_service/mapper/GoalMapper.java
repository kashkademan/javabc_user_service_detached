package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.ObjectUtils;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GoalMapper {

    Goal toGoal(CreateGoalDto createGoalDto);

    @Mapping(source = "users", target = "userIds")
    @Mapping(source = "mentor", target = "mentorId")
    @Mapping(source = "parent", target = "parentGoalId")
    GoalDto toGoalDto(Goal goal);

    default List<Long> mapUsersToUserIds(List<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(User::getId)
                .toList();
    }

    default Long mapMentorToId(User mentor) {
        return ObjectUtils.isEmpty(mentor) ? null : mentor.getId();
    }

    default Long mapParentGoalToId(Goal parentGoal) {
        return ObjectUtils.isEmpty(parentGoal) ? null : parentGoal.getId();
    }
}
