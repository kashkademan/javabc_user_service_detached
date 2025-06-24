package school.faang.user_service.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaMessageService {

    private final UserService userService;
    private final UserMapper mapper;

    public UserDto getUserDtoById(long userId) {
        User user = userService.getUserById(userId);
        return mapper.userToDto(user);
    }
}