package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalFilterByUsers implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return Objects.nonNull(filterDto.usersId()) && !filterDto.usersId().isEmpty();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> filteredData, GoalFilterDto filterDto) {
        return filteredData.filter(goal -> Objects.nonNull(goal.getUsers())
                && goal.getUsers()
                        .stream()
                        .anyMatch(user -> filterDto.usersId().contains(user.getId()))
        );
    }
}