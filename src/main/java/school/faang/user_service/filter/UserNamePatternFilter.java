package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

@Component
public class UserNamePatternFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFiltersDto filtersDto) {
        return filtersDto.namePattern() != null && !filtersDto.namePattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filtersDto) {
        String pattern = filtersDto.namePattern();
        return users.filter(user -> user.getUsername().matches(pattern));
    }
}
