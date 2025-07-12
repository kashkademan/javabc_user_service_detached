package school.faang.user_service.filter.goal;

import java.util.stream.Stream;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

public interface GoalFilter {
    
    boolean isApplicable(GoalFilterDto goalFilterDto);
    
    Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto goalFilterDto);
}
