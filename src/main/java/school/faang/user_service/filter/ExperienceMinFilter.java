package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

@Component
public class ExperienceMinFilter implements UserFilterStrategy {
    @Override
    public boolean filterUsers(User user, UserDtoFilter filter) {
        return user.getExperience() >= filter.getExperienceMin();
    }

    @Override
    public boolean isApplicable(UserDtoFilter filter) {
        return filter.getExperienceMin() != 0;
    }
}