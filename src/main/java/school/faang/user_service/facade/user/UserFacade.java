package school.faang.user_service.facade.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFacade {
    private final UserMapper userMapper;
    private final UserService userService;

    public UserResponseDto getUserById(long userId) {
        User user = userService.getUserById(userId);

        UserResponseDto userResponseDto = userMapper.toUserResponseDto(user);
        log.debug("Mapping User entity to UserResponseDto. Entity content: {}. DTO content: {}.",
                user, userResponseDto);
        return userResponseDto;
    }
}
