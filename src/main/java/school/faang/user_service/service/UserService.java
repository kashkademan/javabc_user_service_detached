package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;

import java.util.List;

public interface UserService {
    UserDto findUserById(Long userId);
    UserDto updateUser(UserDto userDto);
    List<UserDto> getUsersByIds(List<Long> userIds);
    UserPersonalDto getUserPersonals(Long userId);
    UserPersonalDto refreshUserAvatar(Long userId);
    UserPersonalDto uploadAvatar(long userId, MultipartFile file);
    UserPersonalDto getAvatar(long userId);
    UserPersonalDto deleteAvatar(long userId);
}