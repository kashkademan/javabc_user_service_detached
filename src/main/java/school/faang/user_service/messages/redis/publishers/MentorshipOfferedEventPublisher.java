package school.faang.user_service.messages.redis.publishers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipEventDto;

@Slf4j
@Component
public class MentorshipOfferedEventPublisher extends PublishAbstract {
    private final ChannelTopic mentorshipOfferedTopic;

    public MentorshipOfferedEventPublisher(
            RedisTemplate<String,
            Object> redisTemplate,
            ChannelTopic mentorshipOfferedTopic
    ) {
        super(redisTemplate);
        this.mentorshipOfferedTopic = mentorshipOfferedTopic;
    }

    public void sendNotification(MentorshipEventDto mentorshipEventDto) {
        log.info("Start publish {} - event start", mentorshipEventDto);
        super.publish(mentorshipOfferedTopic, mentorshipEventDto);
    }
}
