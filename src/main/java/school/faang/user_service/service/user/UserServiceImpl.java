package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto existingUser = findUserById(userDto.getId());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setUsername(userDto.getUsername());
        existingUser.setMentors(userDto.getMentors());
        User user = userRepository.save(userMapper.toUser(existingUser));
        return userMapper.toUserDto(user);
    }
}