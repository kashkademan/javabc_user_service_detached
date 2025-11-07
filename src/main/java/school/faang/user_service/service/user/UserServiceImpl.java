package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.s3.S3Config;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserAvatarUploadDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileStorageException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.ImageProcessing;
import school.faang.user_service.service.S3AvatarService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final ImageProcessing imageProcessing;
    private final S3Config s3Config;
    private final AmazonS3 s3Client;
    private final S3AvatarService s3AvatarService;
    @Value("${user.avatar.max.sizeMb}")
    private int avatarMaxSizeMb;
    private static final int BYTES_IN_KB = 1024;
    private static final int BIG_SIDE_PX = 1080;
    private static final int SMALL_SIDE_PX = 170;
    @Value("#{'${user.avatar.allowed.types}'.split(',')}")
    private List<String> allowedImageTypes;
    @Value("${user.max-ids-per-request}")
    private int maxIdsPerRequest;

    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, UpdateUserDto userDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> userIds) {

        validateIds(userIds);

        return userRepository.findAllById(userIds).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void uploadAvatar(UserAvatarUploadDto dto) {
        MultipartFile file = dto.file();
        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);

        validateAvatar(file);

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage original = ImageIO.read(inputStream);
            String originalFilename = file.getOriginalFilename();
            String extension = Optional.ofNullable(originalFilename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(originalFilename.lastIndexOf('.') + 1))
                    .orElse("png");

            BufferedImage bigImage = imageProcessing.resizeImage(original, BIG_SIDE_PX);
            BufferedImage smallImage = imageProcessing.resizeImage(original, SMALL_SIDE_PX);

            String uuid = java.util.UUID.randomUUID().toString();

            String bigObjectName = "avatars/%d_big_%s.%s".formatted(userId, uuid, extension);
            String smallObjectName = "avatars/%d_small_%s.%s".formatted(userId, uuid, extension);

            String bigFileId = s3AvatarService.uploadImage(bigImage, bigObjectName, extension);
            String smallFileId = s3AvatarService.uploadImage(smallImage, smallObjectName, extension);

            UserProfilePic profilePic = user.getUserProfilePic();

            if (profilePic != null) {
                s3AvatarService.deleteImage(profilePic.getFileId());
                s3AvatarService.deleteImage(profilePic.getSmallFileId());
            } else {
                profilePic = new UserProfilePic();
                user.setUserProfilePic(profilePic);
            }

            profilePic.setFileId(bigFileId);
            profilePic.setSmallFileId(smallFileId);

            user.setUserProfilePic(profilePic);
            userRepository.save(user);

            log.info("User {} uploaded avatar: big={}, small={}", userId, bigFileId, smallFileId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process avatar", e);
        }
    }

    @Override
    @Transactional
    public void deleteAvatar() {
        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);
        UserProfilePic profilePic = user.getUserProfilePic();

        if (profilePic == null) {
            return;
        }

        try {
            if (profilePic.getFileId() != null) {
                s3AvatarService.deleteImage(profilePic.getFileId());
            }
            if (profilePic.getSmallFileId() != null) {
                s3AvatarService.deleteImage(profilePic.getSmallFileId());
            }

            user.setUserProfilePic(null);
            userRepository.save(user);
        } catch (AmazonS3Exception e) {
            throw new FileStorageException("Failed to delete avatar", e);
        }
    }

    @Override
    public ResponseEntity<byte[]> getAvatar(Long userId, String size) {
        UserProfilePic profilePic = userRepository.getByIdOrThrow(userId).getUserProfilePic();
        if (profilePic == null) {
            throw new DataValidationException("User has no avatar");
        }

        String objectKey;
        if ("small".equalsIgnoreCase(size)) {
            objectKey = profilePic.getSmallFileId();
        } else if ("big".equalsIgnoreCase(size)) {
            objectKey = profilePic.getFileId();
        } else {
            throw new DataValidationException("Invalid size parameter: " + size);
        }

        byte[] imageBytes = s3AvatarService.downloadImage(objectKey);
        String contentType = s3AvatarService.getContentType(objectKey);

        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(imageBytes);
    }

    private long getMaxAvatarBytes() {
        return avatarMaxSizeMb * BYTES_IN_KB * BYTES_IN_KB;
    }

    private void validateAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("File does not be Empty");
        }

        if (file.getSize() > getMaxAvatarBytes()) {
            throw new DataValidationException("The file size should not be more than %s MB".formatted(avatarMaxSizeMb));
        }

        String contentType = file.getContentType();

        if (!allowedImageTypes.contains(contentType)) {
            throw new DataValidationException("Unsupported file type: %s".formatted(contentType));
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new DataValidationException("File is not a valid image");
            }
        } catch (IOException e) {
            throw new DataValidationException("Failed to read image file");
        }
    }

    private void validateIds(List<Long> ids) {
        if (ids.size() > maxIdsPerRequest) {
            throw new DataValidationException("Max %d ids allowed".formatted(maxIdsPerRequest));
        }

        List<Long> invalidIds = ids.stream()
                .filter(id -> id == null || id <= 0)
                .toList();

        if (!invalidIds.isEmpty()) {
            throw new DataValidationException("Invalid IDs found: " + invalidIds);
        }
    }
}
