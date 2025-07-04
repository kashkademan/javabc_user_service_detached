package school.faang.user_service.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.notification.EventStartNotificationEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.service.user.UserServiceFacade;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Aspect
public class EventStartEventAspect {

    private final EventStartEventPublisher eventStartEventPublisher;
    private final UserMapper userMapper;
    private final UserServiceFacade userServiceFacade;
    private final UserContext userContext;

    @AfterReturning(
            value = "@annotation(school.faang.user_service.annotation.PublishStartEventKafka)",
            returning = "result")
    public void publisherEventStartEvent(Event result) {
        List<User> users = result.getAttendees();

        eventStartEventPublisher.publish(
                EventStartNotificationEvent.builder()
                        .owner(userServiceFacade.getUserById(userContext.getUserId()))
                        .eventTitle(result.getTitle())
                        .attendees(userMapper.toEventResponses(users))
                        .build()
        );
    }
}