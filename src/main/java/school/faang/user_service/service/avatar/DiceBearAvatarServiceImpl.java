package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.avatar.DiceBearConfig;
import school.faang.user_service.dto.avatar.AvatarDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.avatar.AvatarException;
import school.faang.user_service.mapper.AvatarMapper;
import school.faang.user_service.repository.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiceBearAvatarServiceImpl implements AvatarService {

    private final DiceBearConfig diceBearConfig;
    private final UserRepository userRepository;
    private final AvatarMapper avatarMapper;

    @Override
    public AvatarDto getAvatarDto(Long userId) {
        validateUserId(userId);
        log.debug("Getting avatar DTO for user: {}", userId);

        getAvatarUrl(userId);
        User updatedUser = findUserById(userId);
        return avatarMapper.toDto(updatedUser);
    }

    @Override
    public AvatarDto generateAvatarDto(Long userId) {
        log.info("=== START generateAvatarDto for userId: {}", userId);

        try {
            validateUserId(userId);
            log.info("UserId validation passed");

            User user = findUserById(userId);
            log.info("Found user: ID={}, username={}", user.getId(), user.getUsername());

            String avatarUrl = generateAvatarUrl(user);
            log.info("Generated avatar URL: {}", avatarUrl);

            User updatedUser = findUserById(userId);
            log.info("Retrieved updated user");

            AvatarDto result = avatarMapper.toDto(updatedUser);
            log.info("Mapped to DTO: {}", result);

            return result;

        } catch (Exception e) {
            log.error("ERROR in generateAvatarDto - Exception type: {}, Message: {}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    public String generateAvatarUrl(User user) {
        log.info("=== START generateAvatarUrl for user: {}", user != null ? user.getId() : "NULL");

        if (user == null) {
            log.error("User is null!");
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            log.info("Step 1: Getting seed from user ID: {}, username: {}", user.getId(), user.getUsername());
            String seed = getSeedFromUser(user);
            log.info("Step 2: Got seed: {}", seed);

            String avatarUrl = buildAvatarUrl(seed);
            log.info("Step 3: Built avatar URL: {}", avatarUrl);

            log.info("Step 4: Saving avatar to user...");
            saveAvatarToUser(user, avatarUrl);
            log.info("Step 5: Avatar saved successfully");

            log.info("Avatar generated successfully for user: {} with seed: {}", user.getUsername(), seed);
            return avatarUrl;

        } catch (Exception e) {
            log.error("ERROR in generateAvatarUrl - Exception type: {}, Message: {}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw new AvatarException("Failed to generate avatar", e);
        }
    }

    @Override
    public String getAvatarUrl(Long userId) {
        validateUserId(userId);

        User user = findUserById(userId);
        UserProfilePic profilePic = user.getUserProfilePic();
        if (profilePic != null && profilePic.getFileId() != null && !profilePic.getFileId().trim().isEmpty()) {
            log.debug("Found existing avatar for user: {}", userId);
            return profilePic.getFileId();
        }
        log.info("No avatar found for user: {}, generating new one", userId);
        return generateAvatarUrl(user);
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new DataValidationException("User ID must be positive");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found with id: " + userId));
    }

    private String getSeedFromUser(User user) {
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            return user.getUsername().trim();
        }
        return diceBearConfig.getDefaultSeed();
    }

    private String buildAvatarUrl(String seed) {
        if (seed == null || seed.trim().isEmpty()) {
            seed = diceBearConfig.getDefaultSeed();
        }

        String encodedSeed = URLEncoder.encode(seed.trim(), StandardCharsets.UTF_8);
        return String.format("%s/%s/svg?seed=%s",
                diceBearConfig.getApiUrl(),
                diceBearConfig.getStyle(),
                encodedSeed);
    }

    private void saveAvatarToUser(User user, String avatarUrl) {
        log.info("=== START saveAvatarToUser for user ID: {}", user.getId());
        try {
            UserProfilePic profilePic = user.getUserProfilePic();
            log.info("Current profile pic: {}", profilePic != null ? "exists" : "null");

            if (profilePic == null) {
                log.info("Creating new UserProfilePic");
                profilePic = new UserProfilePic();
                user.setUserProfilePic(profilePic);
            }

            log.info("Setting fileId: {}", avatarUrl);
            profilePic.setFileId(avatarUrl);

            log.info("Saving user to repository...");
            User savedUser = userRepository.save(user);
            log.info("User saved successfully with ID: {}", savedUser.getId());

        } catch (Exception e) {
            log.error("ERROR in saveAvatarToUser - Exception type: {}, Message: {}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw new AvatarException("Failed to save avatar URL", e);
        }
    }
}