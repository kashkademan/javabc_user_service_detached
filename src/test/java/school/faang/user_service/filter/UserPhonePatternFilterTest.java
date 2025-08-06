package school.faang.user_service.filter;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

/**
 * UserPhonePatternFilterTest — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 23.07.2025
 */
public class UserPhonePatternFilterTest implements UserFilter {
    @Override
    public boolean isApplicable(UserFiltersDto filters) {
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filters) {
        return users.filter(user -> user.getPhone().equals("123456789"));
    }
}