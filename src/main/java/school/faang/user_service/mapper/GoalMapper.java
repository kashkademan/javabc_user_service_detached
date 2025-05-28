package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    @Mapping(source = "parentId", target = "parent.id")
    Goal toEntity(GoalDto goalDto);

    @Mapping(source = "parent.id", target = "parentId")
    GoalDto toDto(Goal goal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "skillsToAchieve", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void update(@MappingTarget Goal goal, GoalDto goalDto);

}
