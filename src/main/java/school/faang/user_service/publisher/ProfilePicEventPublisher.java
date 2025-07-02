package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.ProfilePicEvent;

@Component
public class ProfilePicEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profilePicTopic;

    public ProfilePicEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic profilePicTopic) {
        this.redisTemplate = redisTemplate;
        this.profilePicTopic = profilePicTopic;
    }

    public void publish(ProfilePicEvent event) {
        if (event == null) {
            throw new NullPointerException("ProfilePicEvent cannot be null");
        }
        redisTemplate.convertAndSend(profilePicTopic.getTopic(), event);
    }
}