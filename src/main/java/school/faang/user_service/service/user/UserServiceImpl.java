package school.faang.user_service.service.user;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.Person;
import school.faang.user_service.dto.ProfileViewedEventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.UserPictureService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.util.CountryMapperUtil;
import school.faang.user_service.util.PasswordGeneratorUtil;

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
    private final CsvMapper csvMapper;
    private final UserPictureService pictureService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserContext userContext;

    @Override
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));
        kafkaTemplate.send("user.profile.viewed", "user.profile.viewed", getProfileViewEventDto(userId))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send profile view event", ex);
                    } else {
                        log.info("Profile view event sent successfully: {}", result.getRecordMetadata());
                    }
                });
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

    @Override
    public UserPersonalDto getUserPersonals(Long userId) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));
        UserPersonalDto userPersonalDto = userMapper.toUserPersonalDto(foundUser);

        if (StringUtils.isBlank(userPersonalDto.getPictureSmallFileId())) {
            userPersonalDto.setPictureSmallFileId(pictureService.getDefaultPictureLink());
        }

        return userPersonalDto;
    }

    @Override
    @Transactional
    public UserPersonalDto refreshUserAvatar(Long userId) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        if (foundUser.getUserProfilePic() != null && foundUser.getUserProfilePic().getFileId() != null) {
            throw new IllegalStateException("User use photo as avatar");
        }

        UserProfilePic newProfilePic = new UserProfilePic();
        newProfilePic.setSmallFileId(pictureService.generateNewSmallPicture());
        foundUser.setUserProfilePic(newProfilePic);

        User savedUser = userRepository.saveAndFlush(foundUser);

        return userMapper.toUserPersonalDto(savedUser);
    }

    @Transactional
    @Override
    public List<UserDto> processCsv(MultipartFile file) {
        log.info("Starting parsing {}", file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            List<Person> persons = csvMapper.readerFor(Person.class)
                    .with(schema)
                    .<Person>readValues(inputStream)
                    .readAll();

            List<Country> countries = (List<Country>) countryRepository.findAll();
            List<User> users = persons.stream()
                    .map(person -> processPerson(person, countries))
                    .toList();
            userRepository.saveAll(users);
            log.info("Parsing completed. Processed {} users", users.size());

            return userMapper.mapListOfUsers(users);

        } catch (IOException e) {
            log.error("Parsing {} failed: {}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Failed to parse CSV file", e);
        }
    }

    private User processPerson(Person person, List<Country> countries) {
        person.setCountry(CountryMapperUtil.getFullName(person.getCountry()));
        User user = userMapper.personToUser(person);
        user.setPassword(PasswordGeneratorUtil.generatePassword());
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

    @Transactional
    @Override
    public void banUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException(String.format("User with id %d not found", userId)));
        if (user.isBanned()) {
            return;
        }

        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException(String.format("User with id %d not found", userId)));
        if (!user.isBanned()) {
            return;
        }

        user.setBanned(false);
        userRepository.save(user);
    }

    private ProfileViewedEventDto getProfileViewEventDto(Long id) {
        Long viewerId = userContext.getUserId();
        User profileViewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(viewerId)));
        String viewerName = profileViewer.getUsername();
        return new ProfileViewedEventDto(viewerName, viewerId, id);
    }
}