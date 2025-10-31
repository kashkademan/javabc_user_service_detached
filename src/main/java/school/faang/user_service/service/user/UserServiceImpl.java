package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.redis.PromotionRedisService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Map<String, Function<UserDto, String>> FIELD_EXTRACTORS = Map.of(
            "id", userDto -> String.valueOf(userDto.id()),
            "username", UserDto::username,
            "email", UserDto::email,
            "phone", UserDto::phone,
            "aboutMe", UserDto::aboutMe
    );


    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final PromotionRedisService promotionRedisService;

    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

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
    public UserDto getById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    public Page<UserDto> getFirstPromotionUser(int countRow, Pageable pageable) {

        List<UserDto> redisUsers = new ArrayList<>();
        redisUsers.addAll(promotionRedisService.decrementRemainingImpressionsForPromotions(countRow));
        List<Long> userIds = redisUsers.stream()
                .map(userDto -> userDto.id())
                .collect(Collectors.toList());

        List<User> users = new ArrayList<>();
        int sizeRedisList = redisUsers.size();
        if (sizeRedisList < countRow) {
            int currencyUserFromPromotion = countRow - sizeRedisList;
            users = userRepository.findFirstUserIdsNative(currencyUserFromPromotion, userIds);
        }

        List<UserDto> userDtos = users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        Comparator<UserDto> comparator = createComparatorFromSort(pageable.getSort());
        userDtos.sort(comparator);
        redisUsers.sort(comparator);
        redisUsers.addAll(userDtos);
        return getPageFromList(redisUsers, pageable);
    }

    private Page<UserDto> getPageFromList(List<UserDto> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        if (start > list.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, list.size());
        }

        List<UserDto> pageContent = list.subList(start, end);
        return new PageImpl<>(pageContent, pageable, list.size());
    }

    private Comparator<UserDto> createComparatorFromSort(Sort sort) {
        return sort.stream()
                .map(this::createComparatorForOrder)
                .reduce(Comparator::thenComparing)
                .orElse(Comparator.comparing(UserDto::id));
    }

    private Comparator<UserDto> createComparatorForOrder(Sort.Order order) {
        Function<UserDto, String> fieldExtractor = FIELD_EXTRACTORS.get(order.getProperty());

        Comparator<UserDto> comparator;

        comparator = Comparator.comparing(fieldExtractor, nullsLastIgnoreCase());

        return order.isAscending() ? comparator : comparator.reversed();
    }


    private static Comparator<String> nullsLastIgnoreCase() {
        return Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
    }
}
