package school.faang.user_service.validator.amazons3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;

@Component
@Slf4j
public class AvatarValidator {

    public static void validateUserAvatar(UserProfilePic userProfilePic, Long userId) {
        if (userProfilePic == null) {
            log.warn("User {} has no UserProfilePic", userId);
            throw new DataValidationException("User " + userId + " does not have an avatar profile entity");
        }

        if (userProfilePic.getSmallFileId() == null || userProfilePic.getSmallFileId().isBlank()) {
            log.warn("User {} has invalid avatar file ID", userId);
            throw new DataValidationException("Avatar for user " + userId + " is missing or invalid");
        }
    }
}
