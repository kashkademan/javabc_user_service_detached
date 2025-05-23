package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

@Component
public class PhonePatternFilter implements UserFilterStrategy {
    @Override
    public boolean filterUsers(User user, UserDtoFilter filter) {
        return user.getPhone().equals(filter.getPhonePattern());
    }

    @Override
    public boolean isApplicable(UserDtoFilter filter) {
        return filter.getPhonePattern() != null && !filter.getPhonePattern().isEmpty();
    }
}
