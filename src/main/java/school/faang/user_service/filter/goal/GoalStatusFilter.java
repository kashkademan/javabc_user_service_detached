package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalStatusFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filtersDto) {
        return filtersDto.status() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filtersDto) {
        return goals.filter(goal -> goal.getStatus().equals(filtersDto.status()));
    }
}