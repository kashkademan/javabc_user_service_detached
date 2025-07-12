package school.faang.user_service.filter.goal;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

@Component
public class DescriptionGoalFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto goalFilterDto) {
        return goalFilterDto.getDescription() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto) {
        return goals.filter(goal -> goal.getDescription().contains(goalFilterDto.getDescription())); 
    }

}
