package school.faang.user_service.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.PublishViewUserProfileKafka;
import school.faang.user_service.dto.notification.ViewProfile;
import school.faang.user_service.publisher.ViewUserProfilePublisher;
import school.faang.user_service.service.kafka.KafkaMessageService;

@Aspect
@Component
@RequiredArgsConstructor
public class ViewUserProfileAspect {
    private final KafkaMessageService kafkaMessageService;
    private final ViewUserProfilePublisher viewUserProfilePublisher;

    @AfterReturning(pointcut = "@annotation(viewUserProfile)", argNames = "joinPoint,viewUserProfile")
    public void publishProfileView(JoinPoint joinPoint, PublishViewUserProfileKafka viewUserProfile) {
        Object[] args = joinPoint.getArgs();
        if (args.length < 2) {
            throw new IllegalArgumentException("\"Expected at least 2 arguments: followerId and followeeId\"");
        }
        long ownerId = (long) args[0];
        long follower = (long) args[1];
        viewUserProfilePublisher.publish(
                new ViewProfile(
                        kafkaMessageService.getUserDtoById(ownerId),
                        kafkaMessageService.getUserDtoById(follower)
                )
        );
    }
}
