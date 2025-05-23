package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.csv.model.person.Person;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.csv.PersonCsvDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonCsvMapper;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.parser.CsvParserService;
import school.faang.user_service.validator.user.UserValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User with id: %d not found";

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;
    private final CsvParserService csvParserService;
    private final PersonCsvMapper personCsvMapper;
    private final PersonMapper personMapper;

    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(format(USER_NOT_FOUND_MESSAGE, userId)));
        return userMapper.toUser(user);
    }

    public List<UserDto> getUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return userMapper.toListUserDto(users);
    }

    @Transactional
    public Map<String, String> uploadUsersFromCsv(MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        csvParserService.parseCsv(file).stream()
                .filter(personCsvDto -> checkPersonCsvDto(personCsvDto, response))
                .forEach(this::createUserFromCsvDto);

        return response;
    }

    public List<UserDto> getUsersByPage(int page, int size) {
        int offset = page * size;
        List<User> users = userRepository.findUsersByPage(size, offset);

        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }

    private Country getOldOrNewCountry(String countryName) {
        return countryRepository.findAll().stream()
                .filter(c -> c.getTitle().equalsIgnoreCase(countryName))
                .findFirst()
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setTitle(countryName);
                    return countryRepository.save(newCountry);
                });
    }

    private boolean checkPersonCsvDto(PersonCsvDto personCsvDto, Map<String, String> response) {
        String userName = personCsvDto.getFirstName() + personCsvDto.getLastName();
        String email = personCsvDto.getEmail();
        String phone = personCsvDto.getPhone();

        if (userRepository.existsByUsername(userName)) {
            response.put(userName, String.format("user name %s already exist", userName));
            return false;
        }

        if (userRepository.existsByEmail(email)) {
            response.put(userName, String.format("email %s already exist", email));
            return false;
        }

        if (userRepository.existsByPhone(phone)) {
            response.put(userName, String.format("phone %s already exist", phone));
            return false;
        }

        response.put(userName, String.format("created success"));
        return true;
    }

    private void createUserFromCsvDto(PersonCsvDto personCsvDto) {
        Person person = personCsvMapper.toPerson(personCsvDto);

        String countryName = person.getContactInfo().getAddress().getCountry();

        Country country = getOldOrNewCountry(countryName);

        User user = personMapper.toUser(person, country);
        userRepository.save(user);
    }

    public User getUserFromDb(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(format(USER_NOT_FOUND_MESSAGE, userId)));
    }
}
