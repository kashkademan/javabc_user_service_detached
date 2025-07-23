package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;

import school.faang.user_service.messaging.events.FollowerEvent;
import school.faang.user_service.messaging.publishers.FollowerEventPublisher;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.subscription.UserFilterStrategy;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.SubscriptionService;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final List<UserFilterStrategy> strategies;
    private final FollowerEventPublisher followerEventPublisher;


    @Override
    public void followUser(long followerId, long followeeId) {
        validateUserExistance(followerId, followeeId);
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot subscribe to yourself");
        }
        if (isAlreadySubscribed(followerId, followeeId)) {
            throw new DataValidationException("You are already subscribed to this user");
        } else {
            subscriptionRepository.followUser(followerId, followeeId);
        }
        log.debug("""
                Create and publishing Follower Event with follower id: {},\
                and foloweeId: {} was started
                """, followerId, followeeId);
        FollowerEvent followerEvent = FollowerEvent.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .subscriptionTime(LocalDateTime.now())
                .build();

        followerEventPublisher.publishMessage(followerEvent);
        log.debug("""
                Publishing Follower Event with follower id: {}
                and with followee id: {} was finished
                """, followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        validateUserExistance(followerId, followeeId);
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot unsubscribe from yourself");
        } else {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserDtoFilter userDtoFilter) {
        validateUserExistance(followeeId);
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId)
                .filter(user -> filterUser(user, userDtoFilter))
                .toList();
        if (followers.isEmpty()) {
            throw new DataValidationException("No followers found matching the criteria");
        }
        return userMapper.mapListOfUsers(followers);
    }

    @Override
    public int getFollowerCount(long followeeId) {
        validateUserExistance(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    public List<UserDto> getFollowing(long followerId, UserDtoFilter userDtoFilter) {
        validateUserExistance(followerId);
        List<User> followers = subscriptionRepository.findByFollowerId(followerId)
                .filter(user -> filterUser(user, userDtoFilter))
                .toList();
        if (followers.isEmpty()) {
            throw new DataValidationException("Not following anyone matching the criteria");
        }
        return userMapper.mapListOfUsers(followers);
    }

    @Override
    public int getFollowingCount(long followerId) {
        validateUserExistance(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validateUserExistance(long... ids) {
        if (ids == null || ids.length == 0) {
            throw new DataValidationException("Id must not be null or empty");
        }
        for (long id : ids) {
            if (!userRepository.existsById(id)) {
                throw new DataValidationException("User with ID " + id + " does not exist");
            }
        }
    }

    private boolean isAlreadySubscribed(long followerId, long followeeId) {
        validateUserExistance(followerId, followeeId);
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    private boolean filterUser(User user, UserDtoFilter userDtoFilter) {
        return strategies.stream()
                .filter(strategy -> strategy.isApplicable(userDtoFilter))
                .allMatch(strat -> strat.filterUsers(user, userDtoFilter));
    }

}
