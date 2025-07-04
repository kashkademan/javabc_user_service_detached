package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.annotation.PublishViewUserProfileKafka;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.resource.S3FileDto;
import school.faang.user_service.dto.user.UserRegisterRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.avatar.AvatarGenerationException;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarGeneratorService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.image.ImageResizer;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validation.file.FileValidation;
import school.faang.user_service.validation.user.UserValidation;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static school.faang.user_service.util.LogsConstants.USER_COUNTRY_NOT_FOUND;
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
    private final FileValidation fileValidation;
    private final S3Service s3Service;
    private final UserContext userContext;
    private final ImageResizer imageResizer;
    private final AvatarGeneratorService avatarGeneratorService;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error(String.format(USER_NOT_FOUND, userId));
            return new UserNotFoundException(String.format(USER_NOT_FOUND, userId));
        });
    }

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

        fileValidation.validateMaxFileSize(file);

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
        User savedUser = userRepository.save(user);
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

    public S3FileDto downloadFile(long userId) {
        return downloadFile(UserProfilePic::getFileId, userId);
    }

    public S3FileDto downloadFileMini(long userId) {
        return downloadFile(UserProfilePic::getSmallFileId, userId);
    }

    private S3FileDto downloadFile(Function<UserProfilePic, String> fileIdExtractor, long userId) {
        User user = getUserById(userId);

        UserProfilePic profilePic = user.getUserProfilePic();
        userValidation.validateProfilePicNotNull(profilePic, userId);

        String key = fileIdExtractor.apply(profilePic);

        S3FileDto fileDto = s3Service.downloadFile(key);
        log.info("Download file, key = {}, fileDto = {}", key, fileDto);
        return fileDto;
    }

    @Transactional
    public User createUser(UserRegisterRequestDto userRegisterRequestDto) {
        User user = userMapper.toUserEntity(userRegisterRequestDto);
        UserProfilePic userProfilePic = new UserProfilePic();
        String password = userRegisterRequestDto.getPassword();

        String s3key = null;
        try {
            s3key = avatarGeneratorService.generateAndUpload();
            log.info("Аватар успешно сгенерирован для пользователя {}, ключ: {}",
                    user.getUsername(), s3key);
        } catch (AvatarGenerationException e) {
            log.error("Ошибка при генерации аватара для пользователя {}: {}",
                    user.getUsername(), e.getMessage(), e);
        }

        long countryId = userRegisterRequestDto.getCountryId();

        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> {
                    log.error(String.format(USER_COUNTRY_NOT_FOUND, countryId));
                    return new EntityNotFoundException(String.format(USER_COUNTRY_NOT_FOUND, countryId));
                });

        userProfilePic.setSmallFileId(s3key);
        user.setUserProfilePic(userProfilePic);
        user.setPassword(password);
        user.setCountry(country);
        user.setActive(true);

        return userRepository.save(user);
    }

    @Transactional
    @PublishViewUserProfileKafka
    public User viewUserProfile(long owner, long follower) {
        return getUserById(follower);
    }
}