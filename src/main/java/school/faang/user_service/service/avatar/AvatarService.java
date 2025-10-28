package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.avatar.validator.AvatarValidator;
import school.faang.user_service.service.s3.S3Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class AvatarService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public ResponseEntity<byte[]> getAvatarUsers(Long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();

        AvatarValidator.validateHaveUserAvatar(userProfilePic, userId);

        String smallFileId = userProfilePic.getSmallFileId();
        if (Objects.nonNull(smallFileId)) {

            var metadata = s3Service.getFileMetadata(smallFileId);
            byte[] fileBytes = s3Service.downloadAvatarAsBytes(smallFileId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(metadata.contentType()));
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } else {
            throw new DataValidationException("SORRY!!!! Service under development!!!!");
        }
    }
}
