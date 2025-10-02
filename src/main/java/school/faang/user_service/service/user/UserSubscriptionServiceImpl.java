package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("The user {} tried to subscribe to himself", followerId);
            throw new DataValidationException("The user cannot subscribe to himself");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("The user {} is already subscribed to the user {}", followerId, followeeId);
            throw new DataValidationException("You have already subscribed to this user");
        }

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("The user {} successfully subscribed to the user {}",
                followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("The user {} tried to unsubscribe from himself", followerId);
            throw new DataValidationException("The user can't unsubscribe from himself");
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("The user {} is not subscribed to the user {}", followerId, followeeId);
            throw new DataValidationException("The user is not subscribed to this user");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("The user {} has successfully unsubscribed from the user {}", followerId, followeeId);
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.debug("Number of subscribers of the user {}: {}", followeeId, count);
        return new CountResponse(count);
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.debug("Number of user subscriptions {}: {}", followerId, count);
        return new CountResponse(count);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFiltersDto filters) {
        Stream<User> followersStream = subscriptionRepository.findByFolloweeId(followeeId);

        return applyFilters(followersStream, filters)
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getFollowees(long followerId, UserFiltersDto filters) {
        Stream<User> followeesStream = subscriptionRepository.findByFollowerId(followerId);

        return applyFilters(followeesStream, filters)
                .map(userMapper::toUserDto)
                .toList();
    }

    private Stream<User> applyFilters(Stream<User> usersStream, UserFiltersDto filters) {
        Stream<User> filteredStream = usersStream;

        if (filters.namePattern() != null && !filters.namePattern().isBlank()) {
            filteredStream = filteredStream.filter(user ->
                    user.getUsername().toLowerCase().contains(filters.namePattern().toLowerCase()));
        }

        if (filters.phonePattern() != null && !filters.phonePattern().isBlank()) {
            filteredStream = filteredStream.filter(user ->
                    user.getPhone() != null && user.getPhone().contains(filters.phonePattern()));
        }

        filteredStream = filteredStream.filter(user ->
                user.getExperience() >= filters.experienceMin()
                        && user.getExperience() <= filters.experienceMax());

        return filteredStream;
    }
}