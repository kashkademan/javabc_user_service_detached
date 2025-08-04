package school.faang.user_service.filter.subscription;

import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

public interface UserFilterStrategy {
    boolean isApplicable(UserDtoFilter filter);

    boolean filterUsers(User user, UserDtoFilter userDtoFilter);
}
