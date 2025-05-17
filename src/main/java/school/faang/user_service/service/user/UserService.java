package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.dto.csv.CsvUserDto;
import school.faang.user_service.dto.user.UserAuthDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.csv.CsvUserMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.EducationRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private static final int GOALS_PER_USER = 3;

    private final CountryRepository countryRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CsvMapper csvMapper;
    private final Validator validator;
    private final CsvUserMapper csvUserMapper;
    private final EducationRepository educationRepository;
    private final UserAvatarService userAvatarService;

    public boolean isWithinGoalLimit(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        return user.getGoals().size() < GOALS_PER_USER;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void processCsv(MultipartFile file) throws IOException {

        List<CsvUserDto> users = parseUsers(file);
        log.info("📄 Total rows parsed from CSV: {}", users.size());

        List<CsvUserDto> validUsers = getValidUsers(users);
        log.info("✅ Valid users to save: {}", validUsers.size());
        validUsers.forEach(this::saveUserWithEducation);
    }

    public UserAuthDto findAuthByUsername(String username) {
        return userMapper.toAuthDto(userRepository.findByUsernameLike(username)
                .orElseThrow(() -> new EntityNotFoundException("User with name " + username + " not found")));
    }

    private String generatePassword() {
        int length = 12;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private Country getOrCreateCountry(String countryName) {
        return countryRepository.findByTitleIgnoreCase(countryName).orElseGet(() -> {
            Country newCountry = new Country();
            newCountry.setTitle(countryName);
            return countryRepository.save(newCountry);
        });
    }

    private boolean isDuplicate(CsvUserDto dto) {
        boolean emailExists = userRepository.existsByEmail(dto.getEmail());
        boolean phoneExists = userRepository.existsByPhone(dto.getPhone());

        if (emailExists || phoneExists) {
            log.warn("⛔ Duplicate: email = {}, phone = {}", dto.getEmail(), dto.getPhone());
        }

        return emailExists || phoneExists;
    }

    private List<CsvUserDto> parseUsers(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            MappingIterator<CsvUserDto> it = csvMapper
                    .readerFor(CsvUserDto.class)
                    .with(schema)
                    .readValues(inputStream);
            return it.readAll();

        }
    }

    private List<CsvUserDto> getValidUsers(List<CsvUserDto> users) {
        List<CsvUserDto> validUsers = new ArrayList<>();
        for (CsvUserDto dto : users) {
            Set<ConstraintViolation<CsvUserDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                log.warn("x Validation failed for user: {}", dto.getEmail());
                violations.forEach(v -> log.warn("  {} - {}", v.getPropertyPath(), v.getMessage()));
                continue;
            }

            if (isDuplicate(dto)) {
                log.warn("⛔ Duplicate found for user: {}", dto.getEmail());
                continue;
            }

            validUsers.add(dto);
        }
        return validUsers;
    }

    private void saveUserWithEducation(CsvUserDto dto){
        User user = csvUserMapper.toUser(dto);
        user.setPassword(generatePassword());
        user.setCountry(getOrCreateCountry(dto.getCountry()));
        user.setActive(true);

        log.info("💾 Saving user: {} ({})", user.getUsername(), user.getEmail());
        userRepository.save(user);

        Education education = csvUserMapper.toEducation(dto);
        education.setUser(user);
        educationRepository.save(education);
        log.info("📚 Education and previous education saved for: {}", user.getUsername());
    }

    public User registerUser(UserRegistrationDto dto) {
        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + dto.getCountryId()));

        User user = userMapper.toEntity(dto);

        user.setCountry(country);
        user.setActive(true);
        user.setExperience(0);

        userAvatarService.generateAvatarForNewUser(user, AvatarType.PNG);

        return userRepository.save(user);
    }
}