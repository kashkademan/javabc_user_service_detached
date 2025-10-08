package school.faang.user_service.filter;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

public class UserNameFilterTest implements UserFilter {

    @Override
    public boolean isApplicable(UserFiltersDto filtersDto) {
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filtersDto) {
        return users.filter(user -> user.getUsername() != null
            && user.getUsername().contains(filtersDto.namePattern()));
    }
}
