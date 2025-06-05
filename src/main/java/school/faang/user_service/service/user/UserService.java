package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final UserValidator userValidator;
    private final DiceBearClient diceBearClient;
    private final S3Service s3Service;
    private final CountryService countryService;

    @Transactional
    public User registrationUser(User user, Long countryId) {
        userValidator.checkExistsUsername(user.getUsername());
        // TODO: кодировать пароль

        Country country = countryService.getCountryById(countryId);
        user.setCountry(country);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("User {} has been saved", user);

        userContext.setUserId(savedUser.getId());
        generateRandomUserAvatar(user);

        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        long userId = userContext.getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    private void generateRandomUserAvatar(User user) {
        byte[] image = diceBearClient.getRandomAvatar();

        // TODO: проверка вместимости, 2 ГБ
//        BigInteger newStorageSize =

        String key = s3Service.uploadFile(image, "avatars");

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(key);
        user.setUserProfilePic(userProfilePic);
    }
}
