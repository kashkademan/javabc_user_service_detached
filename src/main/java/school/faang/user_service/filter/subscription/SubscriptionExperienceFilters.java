package school.faang.user_service.filter.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.SubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.time.temporal.ValueRange;
import java.util.Objects;

@Component
public class SubscriptionExperienceFilters implements SubscriptionFilter {

    @Override
    public boolean isApplicable(SubscriptionFilterDto filterDto) {
        return filterDto.getExperienceMin() > 0 || filterDto.getExperienceMax() > 0;
    }

    @Override
    public boolean apply(User user, SubscriptionFilterDto filterDto) {
        return Objects.nonNull(user.getExperience()) && ValueRange.of(filterDto.getExperienceMin(),
                filterDto.getExperienceMax()).isValidIntValue(user.getExperience());
    }
}
