package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalCreateByMentorDto;
import school.faang.user_service.dto.goal.GoalCreateByUserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(source = "users", target = "userIds")
    @Mapping(source = "mentor", target = "mentorId")
    @Mapping(source = "parent", target = "parentGoalId")
    GoalDto toGoalDto(Goal goal);

    default List<Long> mapUsersToUserIds(List<User> users) {
        return users == null ? List.of() : users.stream().map(User::getId).toList();
    }

    default Long mapMentorToId(User mentor) {
        return mentor == null ? null : mentor.getId();
    }

    default Long mapParentGoalToId(Goal parentGoal) {
        return parentGoal == null ? null : parentGoal.getId();
    }

    default Goal toGoal(GoalCreateByMentorDto dto) {
        if (dto == null) {
            return null;
        }

        return Goal.builder()
                .title(dto.title())
                .description(dto.description())
                .deadline(dto.deadline())
                .build();
    }

    default Goal toGoal(GoalCreateByUserDto dto) {
        if (dto == null) {
            return null;
        }

        return Goal.builder()
                .title(dto.title())
                .description(dto.description())
                .deadline(dto.deadline())
                .build();
    }


    default void update(GoalUpdateDto dto, Goal entity) {
        if (dto == null) {
            return;
        }

        if (dto.title() != null) {
            entity.setTitle(dto.title());
        }
        if (dto.description() != null) {
            entity.setDescription(dto.description());
        }
        if (dto.status() != null) {
            entity.setStatus(dto.status());
        }
        if (dto.deadline() != null) {
            entity.setDeadline(dto.deadline());
        }
    }
}
