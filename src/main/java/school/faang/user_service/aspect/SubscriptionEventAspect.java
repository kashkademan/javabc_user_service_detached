package school.faang.user_service.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.PublishSubscriptionEventKafka;
import school.faang.user_service.config.kafka.dto.SubscriptionEvent;
import school.faang.user_service.config.kafka.publisher.SubscriptionEventPublisher;
import school.faang.user_service.service.kafka.KafkaMessageService;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionEventAspect {

    private final KafkaMessageService messageService;
    private final SubscriptionEventPublisher subscriptionEventPublisher;

    @AfterReturning(pointcut = "@annotation(publishEvent)", argNames = "joinPoint,publishEvent")
    public void publishSubscriptionEvent(JoinPoint joinPoint, PublishSubscriptionEventKafka publishEvent) {
        Object[] args = joinPoint.getArgs();
        if (args.length < 2) {
            throw new IllegalArgumentException("Expected at least 2 arguments: followerId and followeeId");
        }

        long followerId = (long) args[0];
        long followeeId = (long) args[1];

        subscriptionEventPublisher.publish(
                new SubscriptionEvent(
                        publishEvent.type(),
                        messageService.getUserDtoById(followeeId),
                        messageService.getUserDtoById(followerId)
                )
        );
    }
}