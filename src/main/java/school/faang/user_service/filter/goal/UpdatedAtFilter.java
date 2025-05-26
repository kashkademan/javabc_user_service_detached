package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import java.util.Objects;
import java.util.stream.Stream;

public class UpdatedAtFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.updatedAt() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> Objects.equals(goal.getUpdatedAt(), filters.updatedAt()));
    }
}
