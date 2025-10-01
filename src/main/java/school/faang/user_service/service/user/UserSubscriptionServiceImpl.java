package school.faang.user_service.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Этот сервис обрабатывает операции подписки, такие как подписывание и отмена подписки на пользователей,
 * получение количества подписчиков и последователей, а также получение списков подписчиков и фолловеров
 * на основе фильтров. Он использует SubscriptionRepository для взаимодействия с базой данных и UserMapper
 * для преобразования сущностей в DTO
 */

@Service
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserSubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                       UserRepository userRepository,
                                       UserMapper userMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

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

    private void validateExistingFollowing(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(String.format("User with ID %d is not following user with ID %d",
                followerId, followeeId));
        }
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
        return subscriptionRepository.findByFolloweeId(followeeId)
            .filter(user -> userMatchesFilters(user, userFiltersDto))
            .map(userMapper::toUserDto)
            .peek(p -> {
                log.info("User name found in followers: {}", p.username());
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<UserDto> getFollowees(long followerId, UserFiltersDto userFiltersDto) {
        return subscriptionRepository.findByFollowerId(followerId)
            .filter(user -> userMatchesFilters(user, userFiltersDto))
            .map(userMapper::toUserDto)
            .peek(p -> {
                log.info("User name found in followees: {}", p.username());
            })
            .collect(Collectors.toList());
    }

    private void validateUserExists(long userId, String userType) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException(String.format("%s with ID %d does not exist.", userType, userId));
        }
    }

    private void validateExistence(long followerId, long followeeId) {
        validateUserExists(followerId, "Follower");
        validateUserExists(followeeId, "Followee");
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

    private boolean userMatchesFilters(User user, UserFiltersDto filter) {
        return matchesNamePattern(user, filter)
            && matchesPhonePattern(user, filter)
            && matchesExperienceRange(user, filter);
    }

    private boolean matchesNamePattern(User user, UserFiltersDto filter) {
        return Objects.isNull(filter.getNamePattern()) || user.getUsername().contains(filter.getNamePattern());
    }

    private boolean matchesPhonePattern(User user, UserFiltersDto filter) {
        return Objects.isNull(filter.getPhonePattern()) || user.getPhone().contains(filter.getPhonePattern());
    }

    private boolean matchesExperienceRange(User user, UserFiltersDto filter) {
        return user.getExperience() >= filter.getExperienceMin() && user.getExperience() <= filter.getExperienceMax();
    }
}
