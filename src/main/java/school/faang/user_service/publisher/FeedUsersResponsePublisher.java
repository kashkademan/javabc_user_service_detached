package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.properties.FeedUsersResponseTopicProperties;
import school.faang.user_service.dto.event.UserSubscriptionsEvent;

@Component
@RequiredArgsConstructor
public class FeedUsersResponsePublisher {

    private final AbstractEventPublisher abstractEventPublisher;
    private final FeedUsersResponseTopicProperties feedUsersResponseTopicProperties;

    public void publish(UserSubscriptionsEvent event) {
        abstractEventPublisher.sendMessage(event, feedUsersResponseTopicProperties.name());
    }
}
