package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;

import java.util.List;

public interface UserService {
    UserDto findUserById(Long userId);
    UserDto updateUser(UserDto userDto);
    List<UserDto> getUsersByIds(List<Long> userIds);
    UserPersonalDto getUserPersonals(Long userId);
    UserPersonalDto refreshUserAvatar(Long userId);
}