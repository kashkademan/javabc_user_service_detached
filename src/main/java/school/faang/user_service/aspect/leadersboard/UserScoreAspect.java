package school.faang.user_service.aspect.leadersboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.user.ActionType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.service.user.UserScoreService;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class UserScoreAspect {
    private final UserScoreService userScoreService;
    private final UserContext userContext;

    @AfterReturning("@annotation(userScore)")
    public void handleUserScore(JoinPoint joinPoint, UserScore userScore) {
        ActionType actionType = userScore.type();

        try {
            if (!actionType.shouldAddScore(joinPoint)) {
                log.debug("Skipping score for actionType={} (condition not met)", actionType);
                return;
            }

            User user = userContext.getUser();
            userScoreService.addScore(user, actionType);
        } catch (Exception e) {
            log.error("Error handling user score for {}: {}", actionType, e.getMessage());
        }
    }
}
