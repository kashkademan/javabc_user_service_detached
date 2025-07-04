package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GoalMapper {
    Goal toGoal(CreateGoalDto createGoalDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", target = "userIds", qualifiedByName = "extractUserIds")
    GoalDto toGoalDto(Goal goal);

    void update(UpdateGoalDto dto, @MappingTarget Goal entity);

    @Named("extractUserIds")
    default List<Long> extractUserIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }
}
