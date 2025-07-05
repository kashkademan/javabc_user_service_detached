package school.faang.user_service.service.goal.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.Filter;
import school.faang.user_service.service.FilterService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalFilterServiceImpl implements FilterService<Goal, GoalFilterDto> {
    private final List<Filter<Goal, GoalFilterDto>> filters;

    @Override
    public List<Goal> toList(List<Goal> entities, GoalFilterDto dto) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        Stream<Goal> stream = entities.stream();
        for (Filter<Goal, GoalFilterDto> filter : filters) {
            if (filter.isApplicable(dto)) {
                stream = filter.filter(stream, dto);
            }
        }

        return stream.toList();
    }
}
