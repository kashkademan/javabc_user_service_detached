package school.faang.user_service.filter;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

/**
 * UserNamePatternFilterTest — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 21.07.2025
 */
public class UserNamePatternFilterTest implements UserFilter {
    @Override
    public boolean isApplicable(UserFiltersDto filters) {
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filters) {
        return users.filter(user -> user.getUsername().equals("name"));
    }
}