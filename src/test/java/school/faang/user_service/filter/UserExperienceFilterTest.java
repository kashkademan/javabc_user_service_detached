package school.faang.user_service.filter;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

/**
 * UserExperienceFilterTest — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 23.07.2025
 */
public class UserExperienceFilterTest implements UserFilter {
    @Override
    public boolean isApplicable(UserFiltersDto filters) {
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filters) {
        int minExp = 2;
        int maxExp = 4;
        return users.filter(user -> user.getExperience() >= minExp && user.getExperience() <= maxExp);
    }
}