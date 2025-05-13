package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(source = "skillIds", target = "skillsToAchieve")
    Goal toEntity(GoalDto goalDto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds")
    GoalDto toDto(Goal goal);

    default List<Skill> mapIdsToSkills(List<Long> skillIds) {
        if (skillIds == null) {
            return null;
        }
        return skillIds.stream()
                .map(id -> {
                    Skill skill = new Skill();
                    skill.setId(id); // Устанавливаем ID для каждого объекта Skill
                    return skill;
                })
                .collect(Collectors.toList());
    }

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }
}
