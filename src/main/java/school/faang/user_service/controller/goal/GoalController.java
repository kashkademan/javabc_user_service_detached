package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;
import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto create(@RequestBody CreateGoalDto createGoalDto) {
        validateParamValue(createGoalDto.title(), "title");
        validateParamValue(createGoalDto.description(), "description");
        validateParamValue(createGoalDto.userIds(), "userIds");
        return goalService.create(createGoalDto);
    }

    @PutMapping("/{id}")
    public GoalDto update(@PathVariable long id, @RequestBody UpdateGoalDto updateGoalDto) {
        validateParamValue(updateGoalDto.title(), "title");
        validateParamValue(updateGoalDto.description(), "description");
        return goalService.update(id, updateGoalDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        goalService.delete(id);
    }

    @PostMapping("/search")
    public List<GoalDto> getByFilters(@RequestBody GoalFilterDto filters) {
        return goalService.getByFilters(filters);
    }

    private void validateParamValue(Object value, String paramName) {
        if (value == null || (value instanceof String && StringUtils.isBlank((String) value))) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}
