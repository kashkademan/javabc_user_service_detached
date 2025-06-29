package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final CountryService countryService;
    private final RestTemplate restTemplate;
    private final MinioService minioService;

    @Value("${dice-bear-api}")
    private String diceBearApi;

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("The Requester with id = " + id + " does not exist"));
    }

    @Transactional
    public UserDto createUser(UserFullDto userFullDto) {
        validation(userFullDto);
        Country countryUser = countryService.getCountryById(userFullDto.countryId());

        String api = createApiPath(userFullDto.defaultPhoto());

        minioService.createBucket();
        try {
            putS3Client(api, userFullDto.email());
        } catch (IOException e) {
            log.error("IOException {}", e.getMessage());
        }
        User user = userMapper.toEntity(userFullDto);
        user.setCountry(countryUser);

        UserProfilePic pic = new UserProfilePic();
        pic.setFileId(api);
        pic.setSmallFileId(userFullDto.email());
        user.setUserProfilePic(pic);

        return userMapper.toDto(userRepo.save(user));
    }

    private void validation(UserFullDto userFullDto) {
        if (validationString("[a-zA-Z]+", userFullDto.username())) {
            throw new IllegalArgumentException("Incorrect username");
        }

        if (validationString("[a-zA-Z.0-9]+[@][a-z]{3,}[.][a-z]{2,}", userFullDto.email())) {
            throw new IllegalArgumentException("Incorrect email");
        }

        if (validationString("[0-9]{11}", userFullDto.phone())) {
            throw new IllegalArgumentException("Incorrect phone");
        }

        if (userFullDto.experience() < 0) {
            throw new IllegalArgumentException("The experience cannot be less than 0");
        }
    }

    private boolean validationString(String patternText, String text) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(text);
        return !matcher.matches();
    }

    private String createApiPath(String photo) {
        return photo == null ? diceBearApi : diceBearApi + "?" + photo;
    }

    private void putS3Client(String api, String userEmail) throws IOException {
        String file = restTemplate.getForObject(api, String.class);

        if (file == null) {
            throw new IOException("No file");
        }
        minioService.uploadFile(file, userEmail, Long.valueOf(file.length()));
    }

    @PreDestroy
    private void shutdownBucket() {
        if (minioService != null) {
            minioService.shutdownBucket();
        }
    }
}