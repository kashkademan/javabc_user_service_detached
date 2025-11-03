package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<UserDto> getUser(Pageable pageable) {

        int countRow = (pageable.getPageNumber() + 1) * pageable.getPageSize();
        System.out.println(countRow);
        List<UserDto> redisUsers = getUsersWithPromotions(pageable, countRow);

        if (redisUsers.size() >= countRow) {
            return new PageImpl<>(redisUsers, pageable, redisUsers.size());
        }

        List<Long> redisUserIds = redisUsers.stream()
                .map(userDto -> userDto.id())
                .collect(Collectors.toList());

        int sizeRedisList = redisUsers.size();
        int remainingCount = countRow - sizeRedisList;

        Page<User> dbUsers = userRepository.findByIdNotIn(redisUserIds,
                PageRequest.of(pageable.getPageNumber(), remainingCount, pageable.getSort()));
        List<UserDto> dbUserDtos = dbUsers.stream()
                .map(userMapper::toUserDto)
                .toList();

        List<UserDto> allUsers = new ArrayList<>();
        allUsers.addAll(redisUsers);
        allUsers.addAll(dbUserDtos);
        return getPageFromList(allUsers, pageable);
    }

    private List<UserDto> getUsersWithPromotions(Pageable pageable, int countRow) {

        List<UserDto> redisUsers = new ArrayList<>();
        redisUsers.addAll(promotionRedisService.fetchPromotionsAndUpdateViews(countRow, pageable));

        //List<UserDto> result = redisUsers.subList(0, countRow);
        return redisUsers;
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

}
