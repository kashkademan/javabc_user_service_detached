package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.AvatarConfiguration;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;
import school.faang.user_service.service.UserPictureService;
import school.faang.user_service.util.ByteArrayMultipartFile;
import school.faang.user_service.util.ImageUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPictureServiceImpl implements UserPictureService {

    private final AvatarConfiguration config;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
    public UserPersonalDto uploadAvatar(long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        byte[] bigAvatar = ImageUtils.resizeImageToFitLongestSide(file, config.getBigImageLimit()).readAllBytes();
        byte[] smallAvatar = ImageUtils.resizeImageToFitLongestSide(file, config.getSmallImageLimit()).readAllBytes();

        ByteArrayMultipartFile resizedFileBig = new ByteArrayMultipartFile(bigAvatar, file.getName(),
                file.getOriginalFilename(), file.getContentType());
        ByteArrayMultipartFile resizedFileSmall = new ByteArrayMultipartFile(smallAvatar, file.getName(),
                file.getOriginalFilename(), file.getContentType());

        if (resizedFileBig.getSize() > DataSize.ofMegabytes(config.getImageLimitSize()).toBytes()) {
            throw new IllegalArgumentException("Max upload avatar image size is %d megabytes"
                    .formatted(config.getImageLimitSize()));
        }

        Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getFileId)
                .ifPresent(s3Service::deleteFile);

        Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getSmallFileId)
                .ifPresent(s3Service::deleteFile);

        String avatarBigKey = s3Service.uploadFile(resizedFileBig, "user/avatarBig");
        String avatarSmallKey = s3Service.uploadFile(resizedFileSmall, "user/avatarSmall");

        UserProfilePic newProfilePicture = new UserProfilePic();
        newProfilePicture.setSmallFileId(avatarSmallKey);
        newProfilePicture.setFileId(avatarBigKey);
        user.setUserProfilePic(newProfilePicture);

        userRepository.saveAndFlush(user);

        return userMapper.toUserPersonalDto(user);
    }

    @Override
    public byte[] getAvatar(long userId, String size) {
        if (size != null && size.length() != 1) {
            throw new IllegalArgumentException("Image size marker must be 'b' or 's' or could be skipped");
        }
        boolean big = "b".equals(size);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        String avatarKey = Optional.ofNullable(user.getUserProfilePic())
                .map(userProfilePic -> big ? userProfilePic.getFileId() : userProfilePic.getSmallFileId())
                .orElseThrow(() -> new EntityNotFoundException("User doesn't have requested avatar"));

        try {
            return s3Service.downloadFile(avatarKey).readAllBytes();
        } catch (IOException e) {
            log.error("Error during download {}", e.getMessage());
        }
        return new byte[0];
    }

    @Override
    public void deleteAvatar(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getFileId)
                .filter(key -> !key.startsWith(config.getRandomPictureProviderRootUrl()))
                .ifPresent(s3Service::deleteFile);

        Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getSmallFileId)
                .filter(key -> !key.startsWith(config.getRandomPictureProviderRootUrl()))
                .ifPresent(s3Service::deleteFile);

        user.setUserProfilePic(null);
        userRepository.saveAndFlush(user);
    }
}
