package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalMentorFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filtersDto) {
        return filtersDto.mentorId() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filtersDto) {
        return goals.filter(goal -> Objects.equals(goal.getMentor().getId(), filtersDto.mentorId()));
    }
}