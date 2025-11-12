package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.minios3.FileUploadResponse;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.AvatarNotFoundException;
import school.faang.user_service.exception.AvatarUploadException;
import school.faang.user_service.exception.FileSizeExceededException;
import school.faang.user_service.exception.InvalidFileTypeException;
import school.faang.user_service.repository.user.UserRepository;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserAvatarService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ImageProcessingService imageProcessingService;
    private final UserContext userContext;

    private static final int LARGE_AVATAR_SIZE = 1080;
    private static final int SMALL_AVATAR_SIZE = 170;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private static final Set<String> SUPPORTED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    public void uploadAvatar(MultipartFile file) {
        long userId = userContext.getUserId();
        validateInput(userId, file);

        try {
            User user = userRepository.getByIdOrThrow(userId);
            UserProfilePic newAvatars = processAndUploadAvatars(userId, file);

            user.setUserProfilePic(newAvatars);
            userRepository.save(user);

            deleteOldAvatars(user);
            log.info("Avatar successfully uploaded to user {}", userId);

        } catch (Exception e) {
            log.error("Error uploading avatar for user {}", userId, e);
            throw new AvatarUploadException("Failed to upload avatar");
        }
    }

    public byte[] getAvatar(boolean small) {
        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);

        UserProfilePic profilePic = user.getUserProfilePic();
        if (profilePic == null || !profilePic.hasAvatar()) {
            throw new AvatarNotFoundException("Avatar not found");
        }

        String fileId = small ? profilePic.getSmallFileId() : profilePic.getFileId();

        if (!fileStorageService.fileExists(fileId)) {
            throw new AvatarNotFoundException("Avatar file not found in storage");
        }

        return fileStorageService.downloadFile(fileId);
    }

    public void deleteAvatar() {
        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);

        deleteOldAvatars(user);

        user.setUserProfilePic(null);
        userRepository.save(user);

        log.info("Avatar successfully deleted for user {}", userId);
    }

    private void deleteOldAvatars(User user) {
        if (user.getUserProfilePic() != null) {
            try {
                UserProfilePic oldProfilePic = user.getUserProfilePic();
                if (oldProfilePic.getFileId() != null) {
                    fileStorageService.deleteFile(oldProfilePic.getFileId());
                }

                if (oldProfilePic.getSmallFileId() != null) {
                    fileStorageService.deleteFile(oldProfilePic.getSmallFileId());
                }
            } catch (Exception e) {
                log.warn("Failed to delete old avatars", e);
            }
        }
    }

    private String getOptimalFileExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpeg";
        };
    }

    private BufferedImage resizeImageIfNeeded(BufferedImage image, int maxSize) {
        if (imageProcessingService.getMaxDimension(image) <= maxSize) {
            return image;
        }
        return imageProcessingService.resizeImage(image, maxSize);
    }

    private String generateFileName(Long userId, String size, String extension) {
        return String.format("user-%d-%s-%d.%s",
                userId, size, System.currentTimeMillis(), extension);
    }

    private String uploadAvatarFile(byte[] imageData, Long userId, String size, String extension) {
        String fileName = generateFileName(userId, size, extension);
        FileUploadResponse response = fileStorageService.uploadFile(imageData, fileName, "image/" + extension);
        return response.getFileId();
    }

    private UserProfilePic processAndUploadAvatars(Long userId, MultipartFile file) {
        try {
            BufferedImage originalImage = imageProcessingService.readImage(file);
            String fileExtension = getOptimalFileExtension(Objects.requireNonNull(file.getContentType()));

            BufferedImage Image = resizeImageIfNeeded(originalImage, LARGE_AVATAR_SIZE);
            byte[] ImageData = imageProcessingService.convertToByteArray(Image, fileExtension);
            String FileId = uploadAvatarFile(ImageData, userId, "large", fileExtension);

            BufferedImage smallImage = imageProcessingService.resizeImage(originalImage, SMALL_AVATAR_SIZE);
            byte[] smallImageData = imageProcessingService.convertToByteArray(smallImage, fileExtension);
            String smallFileId = uploadAvatarFile(smallImageData, userId, "small", fileExtension);

            return new UserProfilePic(FileId, smallFileId);

        } catch (Exception e) {
            throw new AvatarUploadException("Image processing error");
        }
    }

    private void validateInput(Long userId, MultipartFile file) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("File size exceeds 5MB");
        }

        String contentType = file.getContentType();
        if (!SUPPORTED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException(
                    String.format("Unsupported image format. Supported: %s",
                            String.join(", ", SUPPORTED_CONTENT_TYPES)));
        }
    }


}

