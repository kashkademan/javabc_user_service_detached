package school.faang.user_service.filter.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;

@Component
public class SubscriptionPhoneFilters implements SubscriptionFilter {

    @Override
    public boolean isApplicable(SubscriptionFilterDto filterDto) {
        return Objects.nonNull(filterDto.getPhonePattern()) && !filterDto.getPhonePattern().isEmpty();
    }

    @Override
    public boolean apply(User user, SubscriptionFilterDto filterDto) {
        if (Objects.isNull(user.getPhone())) {
            return false;
        }
        return user.getPhone().contains(filterDto.getPhonePattern());
    }
}
