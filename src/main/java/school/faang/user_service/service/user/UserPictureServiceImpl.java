package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.AvatarConfiguration;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.S3FileIOException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;
import school.faang.user_service.service.UserPictureService;
import school.faang.user_service.util.ByteArrayMultipartFile;
import school.faang.user_service.util.ImageUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPictureServiceImpl implements UserPictureService {

    private final AvatarConfiguration config;
    private final S3Service s3Service;
    private final UserRepository userRepository;

    @Override
    public String getDefaultPictureLink() {
        return config.getRandomPictureProviderRootUrl() + '?' + config.getDefaultSmallAvatarSeed();
    }

    @Override
    public String generateNewSmallPicture() {
        return seedValueToPath(UUID.randomUUID().toString());
    }

    private String seedValueToPath(String smallFileId) {
        return config.getRandomPictureProviderRootUrl() + "?seed=" + smallFileId;
    }

    @Override
    @Transactional
    public void uploadAvatar(long userId, MultipartFile file) {
        User user = getUserFromDb(userId);

        BigSmallPair<ByteArrayMultipartFile> avatarsByteMultipartPair = transformIntoByteArraysMultipartPair(file);
        deleteUsersAvatarsFromS3IfExists(user);
        BigSmallPair<String> generatedKeyPair = generateKeysForS3(user, file);
        BigSmallPair<String> savedKeys = uploadPairImages(avatarsByteMultipartPair, generatedKeyPair);
        updateUserWithAvatarKeys(user, savedKeys);

        userRepository.saveAndFlush(user);
        log.info("Avatar was uploaded for user id {}, big avatar: {}, small avatar {}",
                userId, savedKeys.big(), savedKeys.small());
    }

    @Override
    public byte[] getAvatar(long userId, String size) {
        boolean big = "big".equals(size);
        User user = getUserFromDb(userId);

        String avatarKey = Optional.ofNullable(user.getUserProfilePic())
                .map(userProfilePic -> big ? userProfilePic.getFileId() : userProfilePic.getSmallFileId())
                .filter(key -> key.startsWith(config.getBucketSubstorage()))
                .orElseThrow(() -> new EntityNotFoundException("User doesn't have requested avatar"));

        try {
            return s3Service.downloadFile(avatarKey).readAllBytes();
        } catch (IOException e) {
            log.error("Error during download {}", e.getMessage());
            throw new S3FileIOException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteAvatar(long userId) {
        User user = getUserFromDb(userId);
        deleteUsersAvatarsFromS3IfExists(user);
        user.setUserProfilePic(null);
        userRepository.saveAndFlush(user);
        log.info("Avatar was deleted for user id {}", userId);
    }

    private User getUserFromDb(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));
    }

    private BigSmallPair<ByteArrayMultipartFile> transformIntoByteArraysMultipartPair(MultipartFile file) {
        byte[] bigAvatar = ImageUtils.resizeImageToFitLongestSide(file, config.getBigImageLimit()).readAllBytes();
        byte[] smallAvatar = ImageUtils.resizeImageToFitLongestSide(file, config.getSmallImageLimit()).readAllBytes();

        ByteArrayMultipartFile resizedFileBig = new ByteArrayMultipartFile(bigAvatar, file.getName(),
                file.getOriginalFilename(), file.getContentType());
        ByteArrayMultipartFile resizedFileSmall = new ByteArrayMultipartFile(smallAvatar, file.getName(),
                file.getOriginalFilename(), file.getContentType());

        return new BigSmallPair<>(resizedFileBig, resizedFileSmall);
    }

    private void deleteUsersAvatarsFromS3IfExists(User user) {
        Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getFileId)
                .filter(key -> !key.startsWith(config.getRandomPictureProviderRootUrl()))
                .ifPresent(s3Service::deleteFile);

        Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getSmallFileId)
                .filter(key -> !key.startsWith(config.getRandomPictureProviderRootUrl()))
                .ifPresent(s3Service::deleteFile);
    }

    private BigSmallPair<String> generateKeysForS3(User user, MultipartFile file) {
        String baseKey = String.format("%s/u%did%d", config.getBucketSubstorage(), user.getId(),
                Objects.requireNonNull(file.getOriginalFilename()).hashCode()
        );

        return new BigSmallPair<>(
                String.format("%ss%s", baseKey, config.getBigImageLimit()),
                String.format("%ss%s", baseKey, config.getSmallImageLimit())
        );
    }

    private BigSmallPair<String> uploadPairImages(BigSmallPair<ByteArrayMultipartFile> avatarsByteMultipartPair,
                                                  BigSmallPair<String> keyPair) {
        String avatarBigKey = s3Service.uploadFile(avatarsByteMultipartPair.big(),
                keyPair.big());
        String avatarSmallKey = s3Service.uploadFile(avatarsByteMultipartPair.small(),
                keyPair.small());
        return new BigSmallPair<>(avatarBigKey, avatarSmallKey);
    }

    private void updateUserWithAvatarKeys(User user, BigSmallPair<String> savedKeys) {
        UserProfilePic newProfilePicture = new UserProfilePic();
        newProfilePicture.setFileId(savedKeys.big());
        newProfilePicture.setSmallFileId(savedKeys.small());
        user.setUserProfilePic(newProfilePicture);
    }

    private record BigSmallPair<T>(T big, T small) {
    }
}
