package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEvent;

@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher{
    @Value("${spring.data.redis.channel.profile_view}")
    private String profileViewChannelName;

    private final RedisTemplate<String, Object> template;

    public void publish(ProfileViewEvent event) {
        template.convertAndSend(profileViewChannelName, event);
    }
}
