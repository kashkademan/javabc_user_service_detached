package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillAcquiredEvent;

@Service
@RequiredArgsConstructor
public class SkillAcquiredEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic skillAcquiredTopic;

    public void publishSkillAcquired(Long userId, Long skillId) {
        SkillAcquiredEvent event = new SkillAcquiredEvent(userId, skillId);
        redisTemplate.convertAndSend(skillAcquiredTopic.getTopic(), event);
    }
}
