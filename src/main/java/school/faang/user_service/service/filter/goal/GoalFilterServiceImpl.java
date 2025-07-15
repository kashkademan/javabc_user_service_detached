package school.faang.user_service.service.filter.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.filter.Filter;
import school.faang.user_service.service.filter.FilterService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalFilterServiceImpl implements FilterService<Goal, GoalFilterDto> {
    private final List<Filter<Goal, GoalFilterDto>> filters;

    @Override
    public List<Goal> getFilteredList(List<Goal> entities, GoalFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}
