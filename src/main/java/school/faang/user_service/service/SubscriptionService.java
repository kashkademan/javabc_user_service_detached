package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowerEventDto;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.validator.user.SubscriptionValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final FollowerEventPublisher followerEventPublisher;
    private final UserValidator userValidator;
    private final SubscriptionValidator subscriptionValidator;

    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        userValidator.validate(followerId);
        userValidator.validate(followeeId);
        subscriptionValidator.validate(new SubscriptionValidator.SubscriptionValidationData(followerId, followeeId));

        FollowerEventDto event = new FollowerEventDto(followerId, followeeId, LocalDateTime.now());
        followerEventPublisher.publish(event);
    }
}