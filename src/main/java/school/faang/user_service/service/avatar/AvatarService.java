package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.amazons3.AvatarValidator;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final DiceBearClient diceBearClient;
    private final S3Client amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public ResponseEntity<Resource> getAvatarUsers(Long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();

        AvatarValidator.validateUserAvatar(userProfilePic, userId);

        String smallFileId = userProfilePic.getSmallFileId();
        if (Objects.isNull(smallFileId)) {
            throw new DataValidationException("Avatar not found or not yet generated");
        }

        var metadata = s3Service.getFileMetadata(smallFileId);
        byte[] fileBytes = s3Service.downloadFileAsBytes(smallFileId);
        String contentType = metadata.contentType() != null ? metadata.contentType() : "application/octet-stream";

        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(fileBytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"avatar-" + userId + ".png\"")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=3600, must-revalidate")
                .body(resource);
    }

    @Async
    public void assignRandomAvatarAsync(Long userId) {
        try {
            log.info("Starting async avatar generation for user {}", userId);

            byte[] avatarBytes = diceBearClient.generateAvatarPng("adventurer");
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
