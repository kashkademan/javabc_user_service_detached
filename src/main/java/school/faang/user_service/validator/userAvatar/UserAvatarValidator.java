package school.faang.user_service.validator.userAvatar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.properties.ProfilePicProperties;
import school.faang.user_service.exception.FileTooLargeException;
import school.faang.user_service.exception.InvalidFileTypeException;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserAvatarValidator {

    private final ProfilePicProperties profilePicProperties;

    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileTooLargeException("File is empty");
        }
        if (file.getSize() > profilePicProperties.getMaxSize()) {
            throw new FileTooLargeException("File size exceeds the limit");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new InvalidFileTypeException("Only images are allowed");
        }
    }
}
