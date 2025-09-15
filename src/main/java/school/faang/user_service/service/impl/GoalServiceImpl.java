package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.publish.GoalCompletedEventDto;
import school.faang.user_service.dto.request.CreateGoalRequestDto;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.CreateGoalResponseDto;
import school.faang.user_service.dto.response.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalAlreadyCompletedException;
import school.faang.user_service.exception.GoalNotAssignedToUserException;
import school.faang.user_service.exception.GoalNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.genericSpecification.GenericSpecification;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.SkillService;

import java.time.LocalDateTime;
import java.util.List;

import static school.faang.user_service.constants.AppConstants.MAX_COUNT_OF_ACTIVE_GOALS;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;
    private final SkillService skillService;
    private final GoalMapper goalMapper;

    @Override
    @Transactional
    public CreateGoalResponseDto createGoal(Long userId, CreateGoalRequestDto request) {
        validateActiveGoalLimit(userId);
        validateSkills(request.getSkillsToAchieveIds());
        Goal savedGoal = goalRepository.createGoalWithMentor(request.getTitle(), request.getDescription(),
                request.getParentId(), request.getMentorId());
        associateSkillsWithGoal(savedGoal.getId(), request.getSkillsToAchieveIds());
        associateGoalWithUsers(savedGoal.getId(), request.getUserIds());
        return goalMapper.toCreateGoalResponseDto(savedGoal);
    }

    @Override
    @Transactional
    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new IllegalArgumentException(String.format("Goal with ID %d does not exist.", goalId));
        }
        goalRepository.removeSkillsFromGoal(goalId);
        goalRepository.removeUsersFromGoal(goalId);
        goalRepository.deleteById(goalId);
    }

    @Override
    public List<GoalDto> findSubtasksByGoalId(Long parentGoalId) {
        return goalMapper.toDto(goalRepository.findAllByParentId(parentGoalId));
    }

    @Override
    public List<GoalDto> search(SearchRequest request) {
        GenericSpecification<Goal> spec = new GenericSpecification<>(
                Goal.class, request.getRootGroup(), request.getSort());
        return goalMapper.toDto(goalRepository.findAll(spec));
    }

    @Transactional
    @Override
    public void completeUserGoal(Long userId, Long goalId) {
        log.info("Starting goal completion for user with ID {} and goal with ID {}", userId, goalId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new UserNotFoundException(userId);
                });

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.error("Goal not found with ID: {}", goalId);
                    return new GoalNotFoundException(goalId);
                });

        validateUserGoal(user, goal);
        skillService.updateSkills(user, goal.getId());
        goal.setStatus(GoalStatus.COMPLETED);
        goalRepository.save(goal);

        log.info("Successfully completed goal '{}' (ID: {}) for user with ID {}", goal.getTitle(), goalId, userId);

        GoalCompletedEventDto goalCompletedEventDto = new GoalCompletedEventDto(
                user.getId(),
                goal.getId(),
                LocalDateTime.now());
        goalCompletedEventPublisher.publish(goalCompletedEventDto);

        log.info("Published goal completion event for goal with ID {} and user with ID {}", goalId, userId);
    }

    private void validateUserGoal(User user, Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            log.info("Attempt to complete already completed goal '{}' (ID: {}) by user with ID {}",
                    goal.getTitle(), goal.getId(), user.getId());

            throw new GoalAlreadyCompletedException(goal.getId(), goal.getTitle());
        }
        if (!goalRepository.existsByIdAndUsersId(goal.getId(), user.getId())) {
            log.warn("User with ID {} attempted to complete goal with ID {} which is not assigned to them",
                    user.getId(), goal.getId());

            throw new GoalNotAssignedToUserException(goal.getId(), user.getId());
        }
    }

    private void validateActiveGoalLimit(Long userId) {
        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals > MAX_COUNT_OF_ACTIVE_GOALS) {
            throw new IllegalArgumentException(String.format("User with ID %d cannot have more than %d active goals.",
                    userId, MAX_COUNT_OF_ACTIVE_GOALS));
        }
    }

    private void validateSkills(List<Long> skillIds) {
        int existingSkills = skillRepository.countExisting(skillIds);
        if (existingSkills != skillIds.size()) {
            throw new IllegalArgumentException("Some of the provided skills do not exist in the database.");
        }
    }

    private void associateSkillsWithGoal(Long goalId, List<Long> skillIds) {
        skillIds.forEach(skillId -> skillRepository.assignSkillToGoal(goalId, skillId));
    }

    private void associateGoalWithUsers(Long goalId, List<Long> userIds) {
        userIds.forEach(userId -> goalRepository.assignGoalToUser(userId, goalId));
    }
}
