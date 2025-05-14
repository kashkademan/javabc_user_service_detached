package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

public interface UserService {
    UserDto findUserById(Long userId);

    UserDto updateUser(UserDto userDto);
}