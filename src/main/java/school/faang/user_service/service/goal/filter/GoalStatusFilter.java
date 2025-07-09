package school.faang.user_service.service.goal.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.Filter;

import java.util.stream.Stream;

@Component
@Slf4j
public class GoalStatusFilter implements Filter<Goal, GoalFilterDto> {
    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        log.debug("goal status filter isApplicable method called");
        return filterDto.status() != null;
    }

    @Override
    public Stream<Goal> filter(Stream<Goal> entities, GoalFilterDto dto) {
        return entities.filter(goal -> goal.getStatus().equals(dto.status()));
    }
}
