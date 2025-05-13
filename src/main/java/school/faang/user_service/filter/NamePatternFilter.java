package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

@Component
public class NamePatternFilter implements UserFilterStrategy {
    @Override
    public boolean filterUsers(User user, UserDtoFilter filter) {
        return user.getAboutMe().contains(filter.getNamePattern());
    }

    @Override
    public boolean isApplicable(UserDtoFilter filter) {
        return filter.getNamePattern() != null && !filter.getNamePattern().isEmpty();
    }
}