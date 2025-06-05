package school.faang.user_service.validation.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.common.RecordNotFoundException;

import static school.faang.user_service.util.LogsConstants.TOO_LARGE_FILE;
import static school.faang.user_service.util.LogsConstants.USER_PICTURE_NOT_FOUND;
import static school.faang.user_service.util.SettingsConstants.MAX_FILE_SIZE;
import static school.faang.user_service.util.SettingsConstants.MAX_FILE_SIZE_MB;

@Component
@Slf4j
public class UserValidation {

    public void validateMaxFileSize(MultipartFile file) {
        long fileSize = file.getSize();
        if(fileSize > MAX_FILE_SIZE) {
            log.error(TOO_LARGE_FILE, MAX_FILE_SIZE_MB);
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }
    }

    public void validateProfilePicNotNull(UserProfilePic profilePic, long userId) {
        if (profilePic == null) {
            log.error(String.format(USER_PICTURE_NOT_FOUND, userId));
            throw new RecordNotFoundException(String.format(USER_PICTURE_NOT_FOUND, userId));
        }
    }
}
