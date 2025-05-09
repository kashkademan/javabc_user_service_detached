package school.faang.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;

@Transactional(readOnly = true)
public interface UserService {
    UserDto findUserById(Long userId);

    @Transactional
    UserDto updateUser(UserDto userDto);
}