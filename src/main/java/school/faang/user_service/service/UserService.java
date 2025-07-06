package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.utils.Utils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    public static final String USER_NOT_FOUND = "User by id=[{}] is not found";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Utils utils;

    public UserResponseDto getUserDtoById(Long userId) {
        User user = getUserById(userId);
        log.debug("contacts: {}", user.getContacts());
        return userMapper.toUserResponseDto(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(utils.format(USER_NOT_FOUND, userId)));
    }

    public List<UserResponseDto> getUsersDtoByIds(List<Long> userIds) {
        List<User> users = getUsersByIds(userIds);
        return users.stream()
            .map(userMapper::toUserResponseDto)
            .toList();
    }

    public List<User> getUsersByIds(List<Long> userIds) {
        return userRepository.findByIds(userIds);
    }
}

