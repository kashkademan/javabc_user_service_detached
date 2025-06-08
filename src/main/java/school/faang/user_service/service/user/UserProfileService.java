package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.s3.FileException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public String generatePresignedUrl(long userId, boolean isSmall) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден!")
        );

        UserProfilePic profilePic = user.getUserProfilePic();
        if (profilePic == null) {
            throw new FileException("У данного пользователя нет изображения профиля");
        }

        String s3Key = isSmall ? profilePic.getSmallFileId() : profilePic.getFileId();

        return s3Service.generatePresignedUrl(s3Key);
    }
}