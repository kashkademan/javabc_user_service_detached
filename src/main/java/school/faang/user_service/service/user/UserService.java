package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.image.ImageService;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final UserValidator userValidator;
    private final CountryService countryService;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final ApplicationContext applicationContext;


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

    public void authorizeUser(long userId) {
        userContext.setUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    @Transactional
    public User registrationUser(User user, Long countryId) {
        userValidator.validateUser(user);

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        Country country = countryService.getCountryById(countryId);
        user.setCountry(country);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("User {} has been saved", savedUser);

        authorizeUser(user.getId());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                UserService self = applicationContext.getBean(UserService.class);
                self.createAvatarUser();
            }
        });

        return savedUser;
    }

    @Async(value = "generateRandomAvatarUserExecutor")
    public void createAvatarUser() {
        User user = getCurrentUser();

        Resource file = imageService.generateRandomUserAvatar(user.getId());
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFile(file);
        user.setUserProfilePic(userProfilePic);

        User savedUsed = userRepository.save(user);
        log.info("User {} avatar {} has been saved", savedUsed.getId(), user.getUserProfilePic().getSmallFile());
    }
}
