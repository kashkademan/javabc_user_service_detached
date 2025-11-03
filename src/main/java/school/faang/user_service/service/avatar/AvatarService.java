package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.S3.S3Service;
import school.faang.user_service.validator.amazonS3.AvatarValidator;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final DiceBearClient diceBearClient;
    private final S3Client amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ResponseEntity<byte[]> getAvatarUsers(Long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();

        AvatarValidator.validateUserAvatar(userProfilePic, userId);

        String smallFileId = userProfilePic.getSmallFileId();
        if (Objects.nonNull(smallFileId)) {
            var metadata = s3Service.getFileMetadata(smallFileId);
            byte[] fileBytes = s3Service.downloadFileAsBytes(smallFileId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(metadata.contentType()));

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } else {
            throw new DataValidationException("Service under development");
        }
    }

    public String assignRandomAvatar(Long userId) {
        try {
            byte[] avatarBytes = diceBearClient.generateAvatarPng("adventurer");
            String key = "avatars/user-" + userId + ".png";

            amazonS3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType("image/png")
                            .build(),
                    RequestBody.fromBytes(avatarBytes)
            );

            log.info("Generated and uploaded avatar for user {}", userId);
            return key;
        } catch (Exception e) {
            log.error("Error generating avatar for user {}", userId, e);
            throw new FileException("Failed to generate or upload avatar");
        }
    }
}
