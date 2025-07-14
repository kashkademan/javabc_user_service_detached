package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.Validator;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator implements Validator<SubscriptionValidator.SubscriptionValidationData> {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void validate(SubscriptionValidationData data) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(data.followerId, data.followeeId)) {
            throw new IllegalArgumentException("Subscription already exists between follower " + data.followerId
                    + " and followee " + data.followeeId);
        }
    }

    public record SubscriptionValidationData(Long followerId, Long followeeId) {
    }
}