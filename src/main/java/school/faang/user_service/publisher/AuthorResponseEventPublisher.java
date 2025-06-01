package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.properties.AuthorResponseTopicProperties;
import school.faang.user_service.dto.UserDto;

@Component
@RequiredArgsConstructor
public class AuthorResponseEventPublisher {

    private final AbstractEventPublisher abstractEventPublisher;
    private final AuthorResponseTopicProperties authorResponseTopicProperties;

    public void publish(UserDto event) {
        abstractEventPublisher.sendMessage(event, authorResponseTopicProperties.name());
    }
}
