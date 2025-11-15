package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.aspect.analytics.AnalyticsProfileView;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.redis.PromotionRedisService;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.password.min.length}")
    private int minPasswordLength;

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final AvatarService avatarService;
    private final PromotionRedisService promotionRedisService;

    private final SecureRandom random = new SecureRandom();

    private final UserMapperImpl userMapperImpl;

    @Transactional
    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);

        String avatarKey = assignAvatar(user);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFileId(avatarKey);

        avatarService.assignRandomAvatarAsync(user).thenAccept(key -> log.info("Avatar saved at {}", key));

        user.setUserProfilePic(userProfilePic);
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(long userId, UpdateUserDto userDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @AnalyticsProfileView//todo: возможно сделать общую аннотацию для аналитики с параметром тип события
    //todo: подумать над буфером, который бы не так часто генерил сообщения - присылаем пачку просмотров
    public UserDto getById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }


    @Override
    public Page<UserDto> getUser(Pageable pageable) {
        List<UserDto> allRedisUsers = getUsersWithPromotions(pageable, Integer.MAX_VALUE);

        List<Long> redisUserIds = allRedisUsers.stream()
                .map(UserDto::id)
                .collect(Collectors.toList());

        Page<User> dbUsers = userRepository.findByIdNotIn(
                redisUserIds,
                PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort())
        );
        List<UserDto> allDbUsers = dbUsers.stream()
                .map(userMapper::toUserDto)
                .toList();

        List<UserDto> allUsers = new ArrayList<>();
        allUsers.addAll(allRedisUsers);
        allUsers.addAll(allDbUsers);
        return getPageFromList(allUsers, pageable);
    }

    private List<UserDto> getUsersWithPromotions(Pageable pageable, int limit) {
        return promotionRedisService.fetchPromotionsAndUpdateViews(limit, pageable);
    }

    private Page<UserDto> getPageFromList(List<UserDto> list, Pageable pageable) {
        int start = (int) pageable.getOffset();

        if (start < 0 || start >= list.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, list.size());
        }

        int end = Math.min((start + pageable.getPageSize()), list.size());
        List<UserDto> pageContent = list.subList(start, end);
        return new PageImpl<>(pageContent, pageable, list.size());
    }

    private String assignAvatar(User user) {
        double probability = random.nextDouble();
        String key = avatarService.buildAvatarKey(user);

        if (probability < 0.5) {
            avatarService.assignRandomAvatarAsync(user);
        } else {
            avatarService.generateAndSaveAvatarAsync(key);
        }
        return key;
    }
}
