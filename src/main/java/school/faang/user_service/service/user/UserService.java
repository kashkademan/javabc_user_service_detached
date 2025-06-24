package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);

    List<UserDto> getUsersByIds(List<Long> ids);
}

