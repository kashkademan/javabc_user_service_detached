package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

import java.util.Optional;
import java.util.UUID;

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
    public UserProfilePic generateNewPicture() {
        UserProfilePic profilePic = new UserProfilePic();
        String newSeed = UUID.randomUUID().toString();
        profilePic.setSmallFileId(seedValueToPath(newSeed));
        return profilePic;
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
    public UserPersonalDto getAvatar(long userId) {
        return null;
    }

    @Override
    public UserPersonalDto deleteAvatar(long userId) {
        return null;
    }
}
