package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.resource.S3FileDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.image.ImageResizer;
import school.faang.user_service.validation.user.UserValidation;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static school.faang.user_service.util.LogsConstants.USER_NOT_FOUND;
import static school.faang.user_service.util.SettingsConstants.AVATAR_FOLDER;
import static school.faang.user_service.util.SettingsConstants.AVATAR_MINI_FOLDER;
import static school.faang.user_service.util.SettingsConstants.MAX_SIDE_SIZE;
import static school.faang.user_service.util.SettingsConstants.MAX_SIDE_SIZE_MINI;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserValidation userValidation;
    private final S3Service s3Service;
    private final UserContext userContext;
    private final ImageResizer imageResizer;

    @Transactional(readOnly = true)
    public User getUserById(long userId) {
       return userRepository.findById(userId).orElseThrow(() -> {
           log.error(String.format(USER_NOT_FOUND, userId));
           throw new UserNotFoundException(String.format(USER_NOT_FOUND, userId));
       });
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        long userId = userContext.getUserId();
        return getUserById(userId);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersById(List<Long> usersId) {
        return usersId.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProfilePic uploadAvatar(MultipartFile file) {
        User user = getCurrentUser();

        userValidation.validateMaxFileSize(file);

        MultipartFile compressedImage = imageResizer.resizeMultipartImage(file, MAX_SIDE_SIZE);
        String fileKey = s3Service.uploadFile(AVATAR_FOLDER, compressedImage);
        log.info("Upload avatar, fileKey = {}", fileKey);
        MultipartFile compressedImageMini = imageResizer.resizeMultipartImage(file, MAX_SIDE_SIZE_MINI);
        String smallFileKey = s3Service.uploadFile(AVATAR_MINI_FOLDER, compressedImageMini);
        log.info("Upload avatar, smallFileKey = {}", smallFileKey);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(fileKey);
        userProfilePic.setSmallFileId(smallFileKey);
        user.setUserProfilePic(userProfilePic);
        User savedUser =  userRepository.save(user);
        return savedUser.getUserProfilePic();
    }
    @Transactional
    public void deleteAvatar() {
        User user = getCurrentUser();
        String key = user.getUserProfilePic().getFileId();
        String keyMini = user.getUserProfilePic().getSmallFileId();
        s3Service.deleteFile(key);
        s3Service.deleteFile(keyMini);
        user.setUserProfilePic(null);
    }
    @Transactional
    public S3FileDto downloadFile() {
        return downloadFile(UserProfilePic::getFileId);
    }

    @Transactional
    public S3FileDto downloadFileMini() {
        return downloadFile(UserProfilePic::getSmallFileId);
    }

    private S3FileDto downloadFile(Function<UserProfilePic, String> fileIdExtractor) {
        User user = getCurrentUser();
        long userId = user.getId();

        UserProfilePic profilePic = user.getUserProfilePic();
        userValidation.validateProfilePicNotNull(profilePic, userId);

        String key = fileIdExtractor.apply(profilePic);

        S3FileDto fileDto = s3Service.downloadFile(key);
        log.info("Download file, key = {}, fileDto = {}", key, fileDto);
        return fileDto;
    }
}