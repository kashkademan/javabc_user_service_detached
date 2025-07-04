package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.resource.S3FileDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegisterRequestDto;
import school.faang.user_service.dto.user.UserRegisterResponseDto;
import school.faang.user_service.dto.user.UserViewProfileDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserDto getUserById(long userId) {
        User user = userService.getUserById(userId);
        return userMapper.userToDto(user);
    }

    public List<UserDto> getUsersById(List<Long> ids) {
        List<User> users = userService.getUsersById(ids);
        return userMapper.toEventResponses(users);
    }

    public UserProfilePic uploadAvatar(MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    public S3FileDto downloadAvatar(long userId) {
        return userService.downloadFile(userId);
    }

    public S3FileDto downloadAvatarMini(long userId) {
        return userService.downloadFileMini(userId);
    }

    public void deleteAvatar() {
        userService.deleteAvatar();
    }

    @Transactional
    public UserRegisterResponseDto registerUser(UserRegisterRequestDto userRegisterRequestDto) {
        User user = userService.createUser(userRegisterRequestDto);
        return userMapper.toUserRegisterResponseDto(user);
    }

    public UserViewProfileDto viewUserProfile(long owner, long follower){
        User user = userService.viewUserProfile(owner, follower);
        return userMapper.toUserViewProfileDto(user);
    }
}