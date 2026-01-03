package school.faang.user_service.mapper.goal;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toGoal(CreateGoalDto createGoalDto);

    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "users", target = "userIds")
    @Mapping(source = "skillsToAchieve", target = "skillsToAchieveIds")
    GoalDto toGoalDto(Goal goal);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", ignore = true)
    void update(@MappingTarget Goal entity, UpdateGoalDto dto);

    default Long mapUserId(User user) {
        return user == null ? null : user.getId();
    }

    default Long mappingSkillId(Skill skill) {
        return skill == null ? null : skill.getId();
    }
}