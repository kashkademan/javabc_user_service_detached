package school.faang.user_service.service.goal.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalFilter;

import java.util.stream.Stream;

@Component
public class GoalDescriptionFilterForTest implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filtersDto) {
        return true;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filtersDto) {
        return goals.filter(goal -> goal.getDescription().matches("descr"));
    }
}