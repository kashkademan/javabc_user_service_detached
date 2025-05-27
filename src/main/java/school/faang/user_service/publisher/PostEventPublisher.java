package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.properties.PostsTopicProperties;
import school.faang.user_service.dto.event.PostFollowersEvent;

@Component
@RequiredArgsConstructor
public class PostEventPublisher {

    private final AbstractEventPublisher abstractEventPublisher;
    private final PostsTopicProperties postsTopicProperties;

    public void publish(PostFollowersEvent event) {
        abstractEventPublisher.sendMessage(event, postsTopicProperties.name());
    }
}
