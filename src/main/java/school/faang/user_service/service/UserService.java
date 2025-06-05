package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;

    public UserDto getUserById(Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException(
                "The Requester with id =" + id + " does not exist"));
        return userMapper.toDto(user);
    }
}
