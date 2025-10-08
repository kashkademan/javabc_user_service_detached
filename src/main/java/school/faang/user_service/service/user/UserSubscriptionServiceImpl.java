package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Этот сервис обрабатывает операции подписки, такие как подписывание и отмена подписки на пользователей,
 * получение количества подписчиков и последователей, а также получение списков подписчиков и фолловеров
 * на основе фильтров. Он использует SubscriptionRepository для взаимодействия с базой данных и UserMapper
 * для преобразования сущностей в DTO
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    public static final String FOLLOWERS = "follower(s)";
    public static final String FOLLOWEES = "followee(s)";
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    @Override
    @Transactional
    public void followUser(long followerId, long followeeId) {
        validateExistence(followerId, followeeId);
        validateSelfSubscription(followerId, followeeId);
        validateAlreadyFollowing(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User with ID {} now follows user with ID {}", followerId, followeeId);
    }

    @Override
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        validateSelfSubscription(followerId, followeeId);
        validateExistingFollowing(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User with ID {} no longer follows user with ID {}", followerId, followeeId);
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        int followersCount = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.info("Number of followers for user with ID {}: {}", followeeId, followersCount);
        return new CountResponse(followersCount);
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        int followeesCount = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.info("Number of followees for user with ID {}: {}", followerId, followeesCount);
        return new CountResponse(followeesCount);
    }

    @Override
    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFiltersDto userFiltersDto) {
        return getUsersByUserId(followeeId, userFiltersDto, subscriptionRepository::findByFolloweeId, FOLLOWERS);
    }

    @Override
    @Transactional
    public List<UserDto> getFollowees(long followerId, UserFiltersDto userFiltersDto) {
        return getUsersByUserId(followerId, userFiltersDto, subscriptionRepository::findByFollowerId, FOLLOWEES);
    }

    public List<UserDto> getUsersByUserId(long userId,
                                          UserFiltersDto userFiltersDto,
                                          Function<Long, Stream<User>> findUsersFunction,
                                          String usersType) {
        Stream<User> usersStream = findUsersFunction.apply(userId);

        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(userFiltersDto)) {
                usersStream = filter.apply(usersStream, userFiltersDto);
            }
        }

        return usersStream
            .map(userMapper::toUserDto)
            .peek(userDto -> log.info("User name found in {}: {}", usersType, userDto.username()))
            .collect(Collectors.toList());
    }

    private void validateExistingFollowing(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(String.format("User with ID %d is not following user with ID %d",
                    followerId, followeeId));
        }
    }

    private void validateUserExists(long userId, String userType) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException(String.format("%s with ID %d does not exist.", userType, userId));
        }
    }

    private void validateExistence(long followerId, long followeeId) {
        validateUserExists(followerId, FOLLOWERS);
        validateUserExists(followeeId, FOLLOWEES);
    }

    private void validateSelfSubscription(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("A user cannot follow themselves.");
        }
    }

    private void validateAlreadyFollowing(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are already following this user.");
        }
    }
}
