package school.faang.user_service.adapter;

import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

public class UserRepositoryAdapter {

    public static User userFromRepository (UserRepository userRepository, long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }
}
