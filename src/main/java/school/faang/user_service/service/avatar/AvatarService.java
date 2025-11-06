package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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
import java.util.Optional;
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

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

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

    public void generateAndSaveAvatarAsync(String key) {
        CompletableFuture.supplyAsync(() -> {
            MultipartFile multipartFile = diceBearClient.generateRandomAvatar();
            s3Service.saveToFileStorage(multipartFile, key);
            return multipartFile;
        });
    }

    @Async
    public void assignRandomAvatarAsync(Long userId) {
        try {
            log.info("Starting async avatar generation for user {}", userId);

            byte[] avatarBytes = diceBearClientV2.generateAvatarPng("adventurer");
            String key = "avatars/user-" + userId + ".png";

            amazonS3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType("image/png")
                            .build(),
                    RequestBody.fromBytes(avatarBytes)
            );

            userRepository.findById(userId).ifPresent(user -> {
                UserProfilePic profilePic = Optional.ofNullable(user.getUserProfilePic())
                        .orElse(new UserProfilePic());
                profilePic.setSmallFileId(key);
                user.setUserProfilePic(profilePic);
                userRepository.save(user);
                log.info("Avatar assigned for user {} and saved to DB", userId);
            });

        } catch (Exception e) {
            log.error("Error generating or uploading avatar for user {}", userId, e);
        }
    }
}
