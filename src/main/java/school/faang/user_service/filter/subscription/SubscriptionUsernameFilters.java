package school.faang.user_service.filter.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;

@Component
public class SubscriptionUsernameFilters implements SubscriptionFilter {

    @Override
    public boolean isApplicable(SubscriptionFilterDto filterDto) {
        return Objects.nonNull(filterDto.getNamePattern()) && !filterDto.getNamePattern().isEmpty();
    }

    @Override
    public boolean apply(User user, SubscriptionFilterDto filterDto) {
        if (Objects.isNull(user.getUsername())) {
            return false;
        }
        return user.getUsername().contains(filterDto.getNamePattern());
    }
}
