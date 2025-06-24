package school.faang.user_service.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.PublishGoalCompletedEventKafka;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.notification.GoalCompletionNotificationEvent;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.service.user.UserServiceFacade;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventAspect {

    private final UserServiceFacade userServiceFacade;
    private final UserContext userContext;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;

    @AfterReturning(value = "@annotation(school.faang.user_service.annotation.PublishGoalCompletedEventKafka)", returning = "result")
    public void publishGoalCompletedEvent(Goal result) {
        if (result.getStatus() == GoalStatus.COMPLETED) {
            goalCompletedEventPublisher.publish(
                    GoalCompletionNotificationEvent.builder()
                            .userDto(userServiceFacade.getUserById(userContext.getUserId()))
                            .goalTitle(result.getTitle())
                            .build()
            );
        }
    }
}
