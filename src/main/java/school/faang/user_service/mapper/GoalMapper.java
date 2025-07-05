package school.faang.user_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(target = "parent", source = "parent.id")
    @Mapping(target = "skillsToAchieve", source = "skillsToAchieve", qualifiedByName = "skillsToIds")
    @Mapping(target = "users", source = "users", qualifiedByName = "usersToIds")
    GoalDto toDto(Goal goal);

    @Mapping(target = "parent", source = "parent", qualifiedByName = "idToGoal")
    @Mapping(target = "skillsToAchieve", source = "skillsToAchieve", qualifiedByName = "idsToSkills")
    @Mapping(target = "users", source = "users", qualifiedByName = "idsToUsers")
    Goal toEntity(GoalDto goalDto);

    @Named("skillsToIds")
    default List<Long> mapSkillsToIds(List<Skill> skills) {
        return skills != null ? skills.stream().map(Skill::getId).toList() : null;
    }

    @Named("usersToIds")
    default List<Long> mapUsersToIds(List<User> users) {
        return users != null ? users.stream().map(User::getId).toList() : null;
    }

    @Named("idToGoal")
    default Goal mapIdToGoal(Long id) {
        if (id == null) return null;
        return Goal.builder().id(id).build();
    }

    @Named("idsToSkills")
    default List<Skill> mapIdsToSkills(List<Long> ids) {
        return ids != null ? ids.stream().map(id -> Skill.builder().id(id).build()).toList() : null;
    }

    @Named("idsToUsers")
    default List<User> mapIdsToUsers(List<Long> ids) {
        return ids != null ? ids.stream().map(id -> User.builder().id(id).build()).toList() : null;
    }
}
