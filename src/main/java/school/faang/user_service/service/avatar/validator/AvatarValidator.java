package school.faang.user_service.service.avatar.validator;

import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.Objects;

public class AvatarValidator {

    public static void validateHaveUserAvatar(UserProfilePic userProfilePic, Long userId) {
        if (Objects.isNull(userProfilePic)) {
            throw new EntityNotFoundException(String.format("The user's %d avatar was not found.", userId));
        }
    }
}
