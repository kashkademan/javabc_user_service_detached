package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalFilterBySkills implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return Objects.nonNull(filterDto.skillsId()) && !filterDto.skillsId().isEmpty();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> filteredData, GoalFilterDto filterDto) {
        return filteredData.filter(goal -> Objects.nonNull(goal.getSkillsToAchieve())
                && goal.getSkillsToAchieve()
                        .stream()
                        .anyMatch(skill -> filterDto.skillsId().contains(skill.getId()))
        );
    }
}