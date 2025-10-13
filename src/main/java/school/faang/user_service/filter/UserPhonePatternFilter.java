package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

@Component
public class UserPhonePatternFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFiltersDto filtersDto) {
        return filtersDto.phonePattern() != null && !filtersDto.phonePattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filtersDto) {
        String pattern = filtersDto.phonePattern();
        return users.filter(user -> user.getPhone().matches(pattern));
    }
}
