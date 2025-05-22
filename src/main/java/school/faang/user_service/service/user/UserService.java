package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserById(long userId) {
       return userRepository.findById(userId)
               .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not Found", userId)));
    }

    @Transactional(readOnly = true)
    public List<User> getUsersById(List<Long> usersId) {
        return usersId.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

}