package school.faang.user_service.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.ActionType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.user.UserScoreService;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class UserScoreAspect {
    private final UserScoreService userScoreService;
    private final UserRepository userRepository;
    private final UserContext userContext;

    @AfterReturning("@annotation(userScore)")
    public void handleUserScore(JoinPoint joinPoint, UserScore userScore) {
        ActionType actionType = userScore.type();

        try {
            if (!shouldAddScore(joinPoint, actionType)) {
                log.debug("Skipping score for actionType={} (condition not met)", actionType);
                return;
            }

            long userId = userContext.getUserId();
            User user = userRepository.getByIdOrThrow(userId);

            userScoreService.addScore(user, actionType);
        } catch (Exception e) {
            log.error("Error handling user score for {}: {}", actionType, e.getMessage(), e);
        }
    }

    private boolean shouldAddScore(JoinPoint joinPoint, ActionType actionType) {
        return switch (actionType) {
            case GOAL_CREATED_BY_USER,
                 GOAL_CREATED_BY_MENTOR,
                 CAREER_ADDED -> true;

            case GOAL_COMPLETED -> checkGoalCompleted(joinPoint);
            default -> throw new IllegalStateException(
                    "Unhandled ActionType in UserScoreAspect: %s".formatted(actionType)
            );
        };
    }

    private boolean checkGoalCompleted(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof GoalUpdateDto dto) {
                if (dto.status() == GoalStatus.COMPLETED) {
                    return true;
                }
            }
        }
        return false;
    }
}
