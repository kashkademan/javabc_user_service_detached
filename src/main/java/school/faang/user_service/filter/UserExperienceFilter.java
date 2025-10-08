package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

@Component
public class UserExperienceFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFiltersDto filtersDto) {
        return filtersDto.getExperienceMin() <= filtersDto.getExperienceMax();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filtersDto) {
        Integer minExperience = filtersDto.getExperienceMin();
        Integer maxExperience = filtersDto.getExperienceMax();
        return users.filter(
            user -> user.getExperience() >= minExperience && user.getExperience() <= maxExperience
        );
    }
}
