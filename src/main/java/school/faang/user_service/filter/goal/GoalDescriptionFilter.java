package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class GoalDescriptionFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filtersDto) {
        return filtersDto.descriptionContains() != null && !filtersDto.descriptionContains().isBlank();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filtersDto) {
        String pattern = filtersDto.descriptionContains();
        return goals.filter(goal -> goal.getTitle().matches(pattern));
    }
}