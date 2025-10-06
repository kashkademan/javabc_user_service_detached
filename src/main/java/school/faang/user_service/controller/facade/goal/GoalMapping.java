package school.faang.user_service.controller.facade.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalMapping {

    private final GoalService goalService;
    private final GoalMapper goalMapper;

    public GoalDto mappingForCreate(CreateGoalDto createGoalDto) {
        Goal goal = goalMapper.toGoal(createGoalDto);

        return goalMapper.toGoalDto(goalService.create(goal, createGoalDto.userIds(),
                createGoalDto.skillIds(), createGoalDto.mentorId()));
    }

    public GoalDto mappingForUpdate(Long goalId, UpdateGoalDto updateGoalDto) {

        return goalMapper.toGoalDto(goalService.update(goalId, updateGoalDto));
    }

    public List<GoalDto> mappingForFilters(GoalFilterDto filters) {
        return goalService.getByFilters(filters)
                .map(goalMapper::toGoalDto)
                .toList();
    }
}
