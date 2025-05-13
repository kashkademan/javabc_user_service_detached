package school.faang.user_service.filter.subscription;

import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.User;

public interface SubscriptionFilter {
    boolean apply(User user, SubscriptionFilterDto filters);

    boolean isApplicable(SubscriptionFilterDto filters);
}
