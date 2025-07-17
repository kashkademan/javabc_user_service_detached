package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

/**
 * UserPatternNameFilter — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 16.07.2025
 */
@Component
public class UserNamePatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFiltersDto filters) {
        return filters.namePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filters) {
        return users
                .filter(user -> filters.namePattern().equalsIgnoreCase(user.getUsername()));
    }
}