package school.faang.user_service.validator.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator<Long> {

    private final UserRepository userRepository;

    @Override
    public void validate(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }
}