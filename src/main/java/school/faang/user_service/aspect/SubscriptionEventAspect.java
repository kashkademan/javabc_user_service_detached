package school.faang.user_service.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.PublishNewFollowerEventKafka;
import school.faang.user_service.dto.notification.NewFollowerEvent;
import school.faang.user_service.publisher.NewFollowerEventPublisher;
import school.faang.user_service.service.kafka.KafkaMessageService;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionEventAspect {

    private final KafkaMessageService messageService;
    private final NewFollowerEventPublisher newFollowerEventPublisher;

    @AfterReturning(pointcut = "@annotation(publishEvent)", argNames = "joinPoint,publishEvent")
    public void publishSubscriptionEvent(JoinPoint joinPoint, PublishNewFollowerEventKafka publishEvent) {
        Object[] args = joinPoint.getArgs();
        if (args.length < 2) {
            throw new IllegalArgumentException("Expected at least 2 arguments: followerId and followeeId");
        }

        long followerId = (long) args[0];
        long ownerId = (long) args[1];

        newFollowerEventPublisher.publish(
                new NewFollowerEvent(
                        messageService.getUserDtoById(ownerId),
                        messageService.getUserDtoById(followerId)
                )
        );
    }
}