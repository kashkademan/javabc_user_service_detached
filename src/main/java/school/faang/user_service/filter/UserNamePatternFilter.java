package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserNamePatternFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> userFilterDto.getNamePattern().equalsIgnoreCase(user.getUsername()));
    }
}
