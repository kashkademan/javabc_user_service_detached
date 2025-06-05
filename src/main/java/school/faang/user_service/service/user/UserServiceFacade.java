package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
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
}