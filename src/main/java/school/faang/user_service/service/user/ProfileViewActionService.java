package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.ProfileViewEventDto;
import school.faang.user_service.publisher.profile.ProfileViewEventPublisher;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProfileViewActionService {

    private final ProfileViewEventPublisher profileViewEventPublisher;
    private final UserContext userContext;

    public void registerProfileView(Long profileOwnerId) {
        Long viewerId = userContext.getUserId();
        if (Objects.equals(viewerId, profileOwnerId)) {
            return;
        }

        ProfileViewEventDto event = ProfileViewEventDto.builder()
                .viewerId(viewerId)
                .profileOwnerId(profileOwnerId)
                .build();

        profileViewEventPublisher.publish(event);
    }
}
