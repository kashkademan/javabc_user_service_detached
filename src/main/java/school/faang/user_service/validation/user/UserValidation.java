package school.faang.user_service.validation.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.common.RecordNotFoundException;

import static school.faang.user_service.util.LogsConstants.USER_PICTURE_NOT_FOUND;

@Component
@Slf4j
public class UserValidation {
    public void validateProfilePicNotNull(UserProfilePic profilePic, long userId) {
        if (profilePic == null) {
            log.error(String.format(USER_PICTURE_NOT_FOUND, userId));
            throw new RecordNotFoundException(String.format(USER_PICTURE_NOT_FOUND, userId));
        }
    }
}
