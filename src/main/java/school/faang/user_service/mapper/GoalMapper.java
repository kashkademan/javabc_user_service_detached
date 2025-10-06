package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.Objects;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toGoal(CreateGoalDto createGoalDto);

    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", target = "userIds", qualifiedByName = "mapUsersId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsId")
    GoalDto toGoalDto(Goal goal);

    @Named("mapUsersId")
    default List<Long> mapUsersId(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }

    static void update(UpdateGoalDto updateGoalDto, @MappingTarget Goal entity) {
        if (Objects.nonNull(updateGoalDto.title())) {
            entity.setTitle(updateGoalDto.title());
        }

        if (Objects.nonNull(updateGoalDto.description())) {
            entity.setDescription(updateGoalDto.description());
        }

        if (Objects.nonNull(updateGoalDto.deadline())) {
            entity.setDeadline(updateGoalDto.deadline());
        }

        if (Objects.nonNull(updateGoalDto.status())) {
            entity.setStatus(updateGoalDto.status());
        }
    }

    @Named("mapSkillsId")
    default List<Long> mapSkillsId(List<Skill> users) {
        return users.stream()
                .map(Skill::getId)
                .toList();
    }
}
