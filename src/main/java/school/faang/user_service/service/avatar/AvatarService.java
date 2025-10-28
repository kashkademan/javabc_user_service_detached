package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.avatar.validator.AvatarValidator;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class AvatarService {

    private final UserRepository userRepository;

    public MultipartFile getAvatarUsers(Long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        UserProfilePic userProfilePic = user.getUserProfilePic();

        AvatarValidator.validateHaveUserAvatar(userProfilePic, userId);

        String smallFileId = userProfilePic.getSmallFileId();

        if (Objects.nonNull(smallFileId)) {

        } else {
            throw new DataValidationException("SORRY!!!! Service under development!!!!");
        }

        return null;
    }
}
