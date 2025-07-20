package school.faang.user_service.service.filter.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

@Component
@Slf4j
public class GoalTitleFilter implements Filter<Goal, GoalFilterDto> {
    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        log.debug("goal title filter isApplicable method called");
        return filterDto.titleContains() != null;
    }

    @Override
    public Stream<Goal> filter(Stream<Goal> entities, GoalFilterDto dto) {
        return entities.filter(goal -> goal.getTitle().contains(dto.titleContains()));
    }
}
