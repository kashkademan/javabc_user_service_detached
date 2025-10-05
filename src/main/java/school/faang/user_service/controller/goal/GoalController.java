package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public GoalDto create(CreateGoalDto createGoalDto) {
        validateString(createGoalDto.title(), "title");
        validateString(createGoalDto.description(), "description");
        validateNotEmpty(createGoalDto.userIds(), "user ids");
        return goalService.create(createGoalDto);
    }

    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        validateString(updateGoalDto.title(), "title");
        validateString(updateGoalDto.description(), "description");
        return goalService.update(goalId, updateGoalDto);
    }

    public void delete(long goalId) {
        goalService.delete(goalId);
    }

    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        return goalService.getByFilters(filters);
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotEmpty(Collection value, String paramName) {
        if (value.isEmpty()) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}