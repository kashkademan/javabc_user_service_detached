package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validatorUserExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("пользователя с данным id не существует!");
        }
    }

    public void validateNotSameUser(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            throw new DataValidationException("Bad Request: Нельзя сделать запрос самому себе");
        }
    }
}