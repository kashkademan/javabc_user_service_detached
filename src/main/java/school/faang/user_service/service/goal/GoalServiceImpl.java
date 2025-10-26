package school.faang.user_service.service.goal;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final ValidationService validationService;

    @Transactional
    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        Goal goal = goalMapper.toGoal(createGoalDto);
        validationService.validatePossibleToCreateOrUpdate(goal);
        goalRepository.save(goal);
        log.info("Goal with title {} was created successfully for User with id {}!",
                createGoalDto.title(), createGoalDto.userIds());
        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        validationService.validatePossibleToCreateOrUpdate(goal);
        goalMapper.update(updateGoalDto, goal);
        log.info("Goal with id {} was updated successfully!", goalId);
        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    @Override
    public void deleteGoal(long goalId) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        goal.getUsers().clear();
        goalRepository.save(goal);
        goalRepository.delete(goal);
    }

    @Transactional
    @Override
    public void deleteGoalFromUser(long goalId, long userId) {
        goalRepository.deleteUserFromGoal(userId, goalId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        return goalRepository.findAll().stream()
                .filter(goal -> filters.titleContains() == null
                        || goal.getTitle().toLowerCase().contains(filters.titleContains().toLowerCase()))
                .filter(goal -> filters.descriptionContains() == null
                        || goal.getDescription().toLowerCase()
                        .contains(filters.descriptionContains().toLowerCase()))
                .filter(goal -> filters.status() == null
                        || goal.getStatus() == filters.status())
                .filter(goal -> filters.mentorId() == null
                        || goal.getMentor().getId().equals(filters.mentorId()))
                .map(goalMapper::toGoalDto)
                .toList();
    }

}

