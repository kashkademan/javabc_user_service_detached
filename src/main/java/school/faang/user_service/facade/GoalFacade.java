package school.faang.user_service.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCreateRequestDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalUpdateRequestDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalFacade {
    private final GoalService goalService;
    private final GoalMapper goalMapper;

    public GoalResponseDto createGoal(final GoalCreateRequestDto goalCreateRequestDto) {
        Goal goal = goalMapper.toGoalEntity(goalCreateRequestDto);

        goal = goalService.createGoal(goal,
                goalCreateRequestDto.getParentId(),
                goalCreateRequestDto.getSkillIds());

        return goalMapper.toGoalResponseDto(goal);
    }

    public GoalResponseDto updateGoal(final GoalUpdateRequestDto goalUpdateRequestDto) {
        Goal goal = goalService.getGoalByIdOrThrow(goalUpdateRequestDto.getId());

        goalMapper.update(goal, goalUpdateRequestDto);

        goal = goalService.updateGoal(goal,
                goalUpdateRequestDto.getSkillIds());

        return goalMapper.toGoalResponseDto(goal);
    }

    public void deleteGoalById(long goalId) {
        goalService.deleteGoalById(goalId);
    }

    public List<GoalResponseDto> getSubtasksByParentGoalId(long goalParentId) {
        List<Goal> goals = goalService.getSubtasksByParentGoalId(goalParentId);
        return goalMapper.toGoalResponseDtoList(goals);
    }

    public List<GoalResponseDto> getGoalsByUserAndFilter(GoalFilterDto filter) {
        List<Goal> goals = goalService.getGoalsByUserAndFilter(filter);
        return goalMapper.toGoalResponseDtoList(goals);
    }
}
