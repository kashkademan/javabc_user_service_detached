package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

@Component
public class ExperienceMaxFilter implements UserFilterStrategy {
    @Override
    public boolean isApplicable(UserDtoFilter filter) {
        return filter.getExperienceMax() != 0;
    }

    @Override
    public boolean filterUsers(User user, UserDtoFilter userDtoFilter) {
        return user.getExperience() <= userDtoFilter.getExperienceMax();
    }
}
