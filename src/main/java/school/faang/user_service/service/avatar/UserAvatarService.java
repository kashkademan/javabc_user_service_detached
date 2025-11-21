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
import school.faang.user_service.repository.user.UserRepository;

import java.awt.image.BufferedImage;
import java.util.Objects;

import static school.faang.user_service.service.avatar.validator.UserAvatarValidator.validateInput;

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

    public void uploadAvatar(MultipartFile file) {
        long userId = userContext.getUserId();
        validateInput(userId, file);
        User user = userRepository.getByIdOrThrow(userId);
        UserProfilePic oldAvatars = user.getUserProfilePic();
        UserProfilePic newAvatars = null;

        try {
            newAvatars = processAndUploadAvatars(userId, file);

            user.setUserProfilePic(newAvatars);
            userRepository.save(user);
            if (oldAvatars != null) {
                deleteOldAvatars(oldAvatars);
            }

            log.info("Avatar successfully uploaded to user {}", userId);

        } catch (Exception e) {
            if (newAvatars != null) {
                cleanupFailedUpload(newAvatars);
                user.setUserProfilePic(oldAvatars);
            }
            log.error("Error uploading avatar for user {}", userId, e);
            throw new AvatarUploadException("Failed to upload avatar");
        }
        if (oldAvatars != null) {
            deleteOldAvatars(oldAvatars);
        }
    }

    public byte[] getAvatar(boolean small, Long userId) {
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
        UserProfilePic userProfilePic = user.getUserProfilePic();
        deleteOldAvatars(userProfilePic);

        user.setUserProfilePic(null);
        userRepository.save(user);

        log.info("Avatar successfully deleted for user {}", userId);
    }

    private void deleteOldAvatars(UserProfilePic userProfilePic) {
        if (userProfilePic != null) {
            try {
                if (userProfilePic.getFileId() != null) {
                    fileStorageService.deleteFile(userProfilePic.getFileId());
                }

                if (userProfilePic.getSmallFileId() != null) {
                    fileStorageService.deleteFile(userProfilePic.getSmallFileId());
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

    private void cleanupFailedUpload(UserProfilePic newAvatars) {
        if (newAvatars == null) return;

        try {
            deleteOldAvatars(newAvatars);
            log.info("Cleaned up failed upload for avatars");
        } catch (Exception e) {
            log.error("Failed to cleanup uploaded files after error", e);
        }
    }
}

