package school.faang.user_service.publisher.profile;

import school.faang.user_service.dto.event.ProfileViewEventDto;

public interface ProfileViewEventPublisher {
    void publish(ProfileViewEventDto event);
}
