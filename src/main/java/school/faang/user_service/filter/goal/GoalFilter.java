package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

public interface GoalFilter extends Filter<GoalFilterDto, Stream<Goal>> {}