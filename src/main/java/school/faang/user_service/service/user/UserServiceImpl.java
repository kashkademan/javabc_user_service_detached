package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.password.min.length}")
    private int minPasswordLength;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;

    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);

        MultipartFile multipartFile = DiceBearClient.generateRandomAvatar();
        String key = String.format("%s %s", user.getUsername(), user.getEmail());
        saveToFileStorage(multipartFile, key);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFileId(key);

        user.setUserProfilePic(userProfilePic);

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
    public UserDto getById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    private void saveToFileStorage(MultipartFile multipartFile, String key) {
        long sizeFile = multipartFile.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentLength(sizeFile);
        objectMetaData.setContentType(multipartFile.getContentType());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, multipartFile.getInputStream(), objectMetaData
            );
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
