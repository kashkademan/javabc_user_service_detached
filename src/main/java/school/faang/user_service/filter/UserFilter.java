package school.faang.user_service.filter;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFiltersDto filtersDto);

    Stream<User> apply(Stream<User> users, UserFiltersDto filtersDto);
}
