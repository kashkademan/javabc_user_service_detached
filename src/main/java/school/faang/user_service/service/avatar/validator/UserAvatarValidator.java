package school.faang.user_service.service.avatar.validator;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.FileSizeExceededException;
import school.faang.user_service.exception.InvalidFileTypeException;

import java.util.Set;

public class UserAvatarValidator {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private static final Set<String> SUPPORTED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    public static void validateInput(Long userId, MultipartFile file) {
        validateUser(userId);
        validateSizeFile(file);
        validateContentType(file);
    }
    private static void validateUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

    }

    private static void validateSizeFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("File size exceeds 5MB");
        }
    }

    private static void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (!SUPPORTED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException(
                    String.format("Unsupported image format. Supported: %s",
                            String.join(", ", SUPPORTED_CONTENT_TYPES)));
        }
    }
}
