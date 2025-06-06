package school.faang.user_service.service.goal;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalIdDto;
import school.faang.user_service.entity.ErrorField;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.Violation;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.enums.ErrorCode;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.util.Util;
import school.faang.user_service.validator.ValidationResult;
import school.faang.user_service.validator.Validator;
import school.faang.user_service.validator.goal.GoalValidationParams;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<Validator<Goal, GoalValidationParams>> validators;
    private final Util util;

    @Value("${user-service.goals.max-per-user}")
    private int maxGoalsPerUser;

    @Override
    public Goal getGoalById(Long goalId) {
        return goalRepository.getReferenceById(goalId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public GoalIdDto createGoal(GoalCreateDto goalCreateRq) throws BusinessException {

        return util.executeInTransaction(status -> {
            Goal mainGoal = createGoalInner(
                    goalCreateRq.getUserId(),
                    goalMapper.toEntity(goalCreateRq.getGoal(), this),
                    goalCreateRq.getGoal(),
                    new Goal(),
                    "goal"
            );

            for (int i = 0; i < goalCreateRq.getGoal().getSubGoals().size(); i++) {
                GoalDto subGoalRq = goalCreateRq.getGoal().getSubGoals().get(i);
                createGoalInner(
                        goalCreateRq.getUserId(),
                        goalMapper.toEntity(subGoalRq, this),
                        subGoalRq,
                        mainGoal,
                        "goal.subGoals[" + i + "]"
                );
            }

            //todo здесь по плану должны возвращаться данные созданной цели, но для упрощения возвращается только Id
            return new GoalIdDto(mainGoal.getId());
        });
    }

    private Goal createGoalInner(Long userId, Goal goal, GoalDto goalRq, Goal parentGoal, String path) throws BusinessException {
        List<Violation> violations = new ArrayList<>();
        GoalValidationParams validationParams = new GoalValidationParams(userId, goalRq, path);

        if (userId != null && goalRepository.countActiveGoalsPerUser(userId) > maxGoalsPerUser) {
            violations.add(new Violation(ErrorCode.GOALS_MORE_THAN_MAXIMUM));
        }

        if (goal.getDeadline() != null && parentGoal.getDeadline() != null
            && goal.getDeadline().isAfter(parentGoal.getDeadline())) {
            violations.add(new Violation(ErrorCode.DEADLINE_GREATER_PARENT,
                    new ErrorField(path + "deadline", "body", goal.getDeadline().toString(), null)));
        }

        validateGoal(goal, validationParams, violations);

        Long mentorId = goal.getMentor().getId();
        if (mentorId == null && parentGoal.getMentor() != null) {
            mentorId = parentGoal.getMentor().getId();
        }

        Goal createdGoal = goalRepository.create(
                goal.getTitle(),
                goal.getDescription(),
                parentGoal.getId(),
                goal.getDeadline() != null ? goal.getDeadline() : parentGoal.getDeadline(),
                mentorId
        );

        if (userId != null && parentGoal.getId() == null) {
            goalRepository.assignGoalToUser(createdGoal.getId(), userId);
        }

        goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .forEach(goalSkillId -> goalRepository.addSkillToGoal(goalSkillId, createdGoal.getId()));

        return createdGoal;
    }

    @Override
    public GoalIdDto deleteGoal(Long goalId) {
        // Приглашения и подцели не удаляются автоматически через Goal Entity, тк не привязаны к ней
        // Удаление скилов и целей пользователя добавлены для единообразия
        return util.executeInTransaction(status -> {
            goalRepository.findByParent(goalId)
                    .mapToLong(Goal::getId)
                    .forEach(this::deleteGoal);
            goalRepository.deleteGoalSkills(goalId);
            goalRepository.deleteGoalInvitations(goalId);
            goalRepository.deleteGoalFromUser(goalId);
            goalRepository.deleteById(goalId);

            return new GoalIdDto(goalId);
        });
    }

    @Override
    @Transactional
    public GoalIdDto updateGoal(Long goalId, GoalDto goalUpdateRq, JsonNode rawRequest) {
        return util.executeInTransaction(status -> {
            Goal storedGoal = getGoalById(goalId);
            Goal newGoal = goalMapper.toEntity(goalUpdateRq, this);

            newGoal.setTitle(newGoal.getTitle().isEmpty() ? storedGoal.getTitle() : null);
            newGoal.setDescription(newGoal.getDescription().isEmpty() ? storedGoal.getTitle() : null);

            if (newGoal.getDeadline() == null && !rawRequest.has("deadline")) {
                newGoal.setDeadline(storedGoal.getDeadline());
            }

            if (newGoal.getMentor().getId() == null && !rawRequest.has("mentorId")) {
                newGoal.setMentor(storedGoal.getMentor());
            }

            ErrorField errorField = new ErrorField("goalId", "query", goalId.toString(), null);
            GoalValidationParams validationParams = new GoalValidationParams(null, goalUpdateRq, "");

            if (storedGoal.getId() == null) {
                throw new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.DATA_NOT_FOUND,
                        List.of(new Violation(ErrorCode.GOAL_NOT_EXISTS, errorField)));
            }

            if (storedGoal.getStatus() == GoalStatus.COMPLETED) {
                throw new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.BUSINESS_ERROR,
                        List.of(new Violation(ErrorCode.GOAL_COMPLETED, errorField)));
            }

            validateGoal(storedGoal, validationParams, new ArrayList<>());

            updateGoalSkills(storedGoal, goalUpdateRq);
            updateSubGoals(storedGoal, goalUpdateRq);

            Goal updatedGoal = goalRepository.updateGoal(
                    goalId,
                    newGoal.getTitle(),
                    newGoal.getDescription(),
                    newGoal.getDeadline(),
                    newGoal.getMentor().getId()
            );

            //todo здесь по плану должны возвращаться обновленные данные цели, но для упрощения возвращается только Id
            return new GoalIdDto(updatedGoal.getId());
        });
    }

    private void updateGoalSkills(Goal goal, GoalDto goalUpdateRq) {
        if (goalUpdateRq.getSkillIds() != null) {
            goalRepository.deleteGoalSkills(goal.getId());
            goalUpdateRq.getSkillIds().forEach(skillId -> goalRepository.addSkillToGoal(skillId, goal.getId()));
        }
    }

    private void updateSubGoals(Goal goal, GoalDto goalUpdateRq) {
        if (goalUpdateRq.getSubGoals() != null) {
            goalRepository.findByParent(goal.getId())
                    .mapToLong(Goal::getId)
                    .forEach(this::deleteGoal);

            for (int i = 0; i < goalUpdateRq.getSubGoals().size(); i++) {
                GoalDto subGoalRq = goalUpdateRq.getSubGoals().get(i);
                createGoalInner(
                        null,
                        goalMapper.toEntity(subGoalRq, this),
                        subGoalRq,
                        goal,
                        "subGoals[" + i + "]"
                );
            }
        }
    }

    private void validateGoal(Goal goal, GoalValidationParams params, List<Violation> violations) {
        validators.stream()
                .filter(validator -> validator.applicable(goal, params))
                .forEach(validator -> {
                    ValidationResult validationResult = validator.validate(goal, params);

                    if (!validationResult.isValid()) {
                        violations.addAll(validationResult.violations());
                    }
                });

        if (!violations.isEmpty()) {
            throw new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.BUSINESS_ERROR, violations);
        }
    }
}
