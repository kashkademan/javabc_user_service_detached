package school.faang.user_service.service;

import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.UserFullDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final CountryService countryService;
    private final RestTemplate restTemplate = new RestTemplate();
    private MinioService minioService;
    private static final String DICE_BEAR_API = "https://api.dicebear.com/9.x/pixel-art/svg";

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException
                ("The Requester with id =" + id + " does not exist"));
    }

    @Transactional
    public Long newUser(UserFullDto userDto, String filter) throws IOException {
        validation(userDto);
        Country countryUser = countryService.getCountryByID(userDto.countryId());

        String api = createApi(filter);
        if (minioService == null) {
            minioService = new MinioService("user-pictures-bucket");
            minioService.createBucket();
        }
        putS3Client(api, userDto.email());

        User user = userMapper.toEntity(userDto);
        user.setCountry(countryUser);

        UserProfilePic pic = new UserProfilePic();
        pic.setFileId(api);
        pic.setSmallFileId(userDto.email());
        user.setUserProfilePic(pic);

        userRepo.save(user);

        return user.getId();
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

    private String createApi(String filter) {
        return filter == null ? DICE_BEAR_API : DICE_BEAR_API + "?" + filter;
    }

    private void putS3Client(String api, String userEmail) throws IOException {
        String file = restTemplate.getForObject(api, String.class);

        if (file == null) {
            throw new IOException("No file");
        }
        minioService.uploadFile(file, userEmail);
    }

    @PreDestroy
    private void shutdownBucket() {
        if (minioService != null) {
            minioService.shutdownBucket();
        }
    }
}
