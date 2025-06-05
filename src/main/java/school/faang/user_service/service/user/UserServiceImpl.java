package school.faang.user_service.service.user;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.Person;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.country.CountryMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final CountryMapper countryMapper;

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

    @Override
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Transactional
    @Override
    public List<UserDto> processCsv(InputStream inputStream) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        List<Person> persons = mapper.readerFor(Person.class)
                .with(schema)
                .<Person>readValues(inputStream)
                .readAll();

        List<Country> countries = (List<Country>) countryRepository.findAll();
        List<User> users = persons.stream()
                .map(person -> processPerson(person, countries))
                .toList();
        userRepository.saveAll(users);

        return userMapper.mapListOfUsers(users);
    }

    private User processPerson(Person person, List<Country> countries) {
        person.setCountry(countryMapper.getFullName(person.getCountry()));
        User user = userMapper.personToUser(person);
        user.setPassword(generatePassword());
        Country country = countries.stream()
                .filter(c -> person.getCountry().equalsIgnoreCase(c.getTitle()))
                .findFirst()
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setTitle(person.getCountry());
                    newCountry.setResidents(new ArrayList<>());
                    countryRepository.save(newCountry);
                    return newCountry;
                });

        country.getResidents().add(user);
        user.setCountry(country);

        return user;
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}