package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AvatarService avatarService;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->  new EntityNotFoundException(
                        String.format("User with id %d not found!", id)
                ));
    }

    public UserDto create(String username, String countryTitle, String email, String password) {
        validate(username, countryTitle, email, password);
        Country country = countryRepository.findByTitle(countryTitle)
                .orElseThrow(() -> new DataValidationException("Unknown country: " + countryTitle));;
        User userEntity = userMapper.toEntity(username, country, email, passwordEncoder.encode(password));
        User user = userRepository.save(userEntity);
        avatarService.generateRandomAvatar(user.getId());
        return userMapper.toDto(user);
    }

    public void delete(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    private void validate(String username, String countryTitle, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new DataValidationException("Username must not be empty");
        }
        if (countryTitle == null || countryTitle.isBlank()) {
            throw new DataValidationException("Country must not be empty");
        }
        if (email == null || email.isBlank()) {
            throw new DataValidationException("Email must not be empty");
        }
        if (password == null || password.isBlank()) {
            throw new DataValidationException("Password must not be empty");
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new DataValidationException("Email is not a valid format");
        }
        if (password.length() < 6) {
            throw new DataValidationException("Password must be at least 6 characters long");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DataValidationException("An account with that email already exists");
        }
    }
}
