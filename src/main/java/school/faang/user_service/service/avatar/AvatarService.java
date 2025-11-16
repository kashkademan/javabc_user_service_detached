package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.client.DiceBearClientV2;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.avatar.validator.AvatarValidator;
import school.faang.user_service.service.s3.S3Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class AvatarService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final DiceBearClient diceBearClient;
    private final S3Client amazonS3;
    private final DiceBearClientV2 diceBearClientV2;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${avatar.base-folder}")
    private String avatarBaseFolder;

    @Value("${avatar.file-extension}")
    private String avatarFileExtension;

    @Value("${dice.bear.client.default-style}")
    private String avatarDefaultStyle;

    @Value("${avatar.content-type}")
    private String avatarContentType;

    public String getAvatarUsers(Long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();

        AvatarValidator.validateHaveUserAvatar(userProfilePic, userId);

        String smallFileId = userProfilePic.getSmallFileId();
        if (Objects.nonNull(smallFileId)) {
            return smallFileId;
        } else {
            throw new DataValidationException("SORRY!!!! Service under development!!!!");
        }
    }

    public CompletableFuture<String> assignRandomAvatarAsync(User user) {
        String key = buildAvatarKey(user);

        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] avatarBytes = diceBearClientV2.generateAvatarPng(avatarDefaultStyle);

                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(avatarContentType)
                        .build();

                amazonS3.putObject(putRequest, RequestBody.fromBytes(avatarBytes));
                log.info("Avatar uploaded for user {}", user.getUsername());
                return key;
            } catch (Exception e) {
                log.error("Failed to upload avatar for user {}", user.getUsername(), e);
                throw new RuntimeException(e);
            }
        });
    }

    public void generateAndSaveAvatarAsync(String key) {
        CompletableFuture.runAsync(() -> {
            MultipartFile multipartFile = diceBearClient.generateRandomAvatar();
            s3Service.saveToFileStorage(multipartFile, key);
        });
    }

    public String buildAvatarKey(User user) {
        String safeUsername = user.getUsername().replaceAll("[^a-zA-Z0-9_-]", "_");
        return String.format("%s/user-%s-%d%s", avatarBaseFolder, safeUsername, user.getId(), avatarFileExtension);
    }
}
