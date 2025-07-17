package school.faang.user_service.filter;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

/**
 * UserPhonePatternFilter — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 16.07.2025
 */
public class UserExperienceFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFiltersDto filters) {
        return filters.experienceMax() != null || filters.experienceMin() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filters) {
        return users
                .filter(user -> user.getExperience() >= filters.experienceMin()
                        && user.getExperience() <= filters.experienceMax());
    }
}