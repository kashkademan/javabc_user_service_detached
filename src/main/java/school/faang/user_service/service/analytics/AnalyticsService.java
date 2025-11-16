package school.faang.user_service.service.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.kafka.analytics.ProfileViewEvent;
import school.faang.user_service.publisher.ProfileViewEventPublisher;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AnalyticsService {
    private final UserContext userContext;
    private final ProfileViewEventPublisher profileViewEventPublisher;

    public void publishProfileViewEvent(long userId) {
        long viewerId = userContext.getUser().getId();
        if (viewerId == userId) {
            return;
        }

        ProfileViewEvent event = new ProfileViewEvent(userId, viewerId, LocalDateTime.now());
        profileViewEventPublisher.publish(event);
    }

}
