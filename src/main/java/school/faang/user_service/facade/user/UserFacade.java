package school.faang.user_service.facade.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFacade {
    private final UserMapper userMapper;
    private final UserService userService;

    public UserResponseDto getCurrentUser() {
        User user = userService.getCurrentUser();

        UserResponseDto userResponseDto = userMapper.toUserResponseDto(user);
        log.debug("Mapping User entity to UserResponseDto. Entity content: {}. DTO content: {}.",
                user, userResponseDto);
        return userResponseDto;
    }

    public UserResponseDto getUserById(long userId) {
        User user = userService.getUserById(userId);

        UserResponseDto userResponseDto = userMapper.toUserResponseDto(user);
        log.debug("Mapping User entity to UserResponseDto. Entity content: {}. DTO content: {}.",
                user, userResponseDto);
        return userResponseDto;
    }

    public List<UserResponseDto> getUsersByIds(List<Long> userIds) {
        List<User> users = userService.getUsersByIds(userIds);

        List<UserResponseDto> userResponseDtoList = userMapper.toUserResponseDtoList(users);
        log.debug("Mapping User entity list to UserResponseDto list. Entity content: {}. DTO content: {}.",
                users, userResponseDtoList);
        return userResponseDtoList;
    }
}
