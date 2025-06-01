package school.faang.user_service.service.avatar;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.avatar.DiceBearConfig;
import school.faang.user_service.dto.avatar.AvatarDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
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
    public AvatarDto getAvatar(Long userId) {
        getAvatarUrl(userId);
        User user = findUserById(userId);
        return avatarMapper.toDto(user);
    }

    @Override
    public AvatarDto generateAvatar(Long userId) {
        log.info("Generating avatar for user: {}", userId);

        try {
            User user = findUserById(userId);
            generateAndSaveAvatar(user);

            User updatedUser = findUserById(userId);
            return avatarMapper.toDto(updatedUser);

        } catch (Exception e) {
            log.error("Failed to generate avatar for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    @Override
    public String generateAvatarUrl(User user) {
        try {
            String seed = getSeedFromUser(user);
            return buildAvatarUrl(seed);

        } catch (Exception e) {
            log.error("Failed to generate avatar URL for user {}: {}", user.getId(), e.getMessage());
            throw new AvatarException("Failed to generate avatar URL", e);
        }
    }

    @Override
    public String getAvatarUrl(Long userId) {
        User user = findUserById(userId);
        UserProfilePic profilePic = user.getUserProfilePic();

        if (profilePic != null && profilePic.getFileId() != null && !profilePic.getFileId().trim().isEmpty()) {
            return profilePic.getFileId();
        }

        log.info("No avatar found for user {}, generating new one", userId);
        return generateAndSaveAvatar(user);
    }

    private String generateAndSaveAvatar(User user) {
        String avatarUrl = generateAvatarUrl(user);
        saveAvatarToUser(user, avatarUrl);
        return avatarUrl;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
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
        return String.format("%s/%s/svg?seed=%s", diceBearConfig.getApiUrl(), diceBearConfig.getStyle(), encodedSeed);
    }

    private void saveAvatarToUser(User user, String avatarUrl) {
        try {
            UserProfilePic profilePic = user.getUserProfilePic();

            if (profilePic == null) {
                profilePic = new UserProfilePic();
                user.setUserProfilePic(profilePic);
            }

            profilePic.setFileId(avatarUrl);
            userRepository.save(user);

        } catch (Exception e) {
            log.error("Failed to save avatar for user {}: {}", user.getId(), e.getMessage());
            throw new AvatarException("Failed to save avatar URL", e);
        }
    }
}